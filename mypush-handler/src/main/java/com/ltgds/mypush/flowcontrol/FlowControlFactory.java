package com.ltgds.mypush.flowcontrol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import com.ltgds.mypush.annotations.LocalRateLimit;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.enums.RateLimitStrategy;
import com.ltgds.mypush.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Li Guoteng
 * @data 2023/8/9
 * @description
 */
@Service
@Slf4j
public class FlowControlFactory implements ApplicationContextAware {

    private static final String FLOW_CONTROL_KEY = "flowControlRule";
    private static final String FLOW_CONTROL_PREFIX = "flow_control_";

    /**
     * 将限流策略 与 限流服务 进行映射
     */
    private final Map<RateLimitStrategy, FlowControlService> flowControlServiceMap = new ConcurrentHashMap<>();

    @Autowired
    private ConfigService config;

    private ApplicationContext applicationContext;

    /**
     * 应用上下文
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 对flowControlServiceMap进行初始化
     */
    @PostConstruct
    private void init() {
        //获取LocalRateLimit类型注解 对应的bean名称和bean对象
        Map<String, Object> serviceMap = this.applicationContext.getBeansWithAnnotation(LocalRateLimit.class);

        serviceMap.forEach((name, service) -> {
            if (service instanceof FlowControlService) {
                LocalRateLimit localRateLimit = AopUtils.getTargetClass(service).getAnnotation(LocalRateLimit.class);
                RateLimitStrategy rateLimitStrategy = localRateLimit.rateLimitStrategy(); //获取限流策略
                //通常情况下 实现的限流service与rateLimitStrategy一一对应
                flowControlServiceMap.put(rateLimitStrategy, (FlowControlService) service);
            }
        });
    }

    /**
     * 流量控制
     * @param taskInfo
     * @param flowControlParam
     */
    public void flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter;
        //限流器初始限流大小
        Double rateInitValue = flowControlParam.getRateInitValue();
        //获取限流值的配置
        Double rateLimitConfig = getRateLimitConfig(taskInfo.getSendChannel());
        //对比 初始限流值 与 配置限流值, 以配置中心的限流值为准
        if (Objects.nonNull(rateLimitConfig) && !rateInitValue.equals(rateLimitConfig)) {
            rateLimiter = RateLimiter.create(rateLimitConfig); //以配置中心的限流值为准
            flowControlParam.setRateInitValue(rateLimitConfig);
            flowControlParam.setRateLimiter(rateLimiter); //设置限流器
        }

        //通过限流策略获取对应限流服务
        FlowControlService flowControlService = flowControlServiceMap.get(flowControlParam.getRateLimitStrategy());

        if (Objects.isNull(flowControlService)) {
            log.error("没有找到对应的单机限流策略");
            return;
        }
        //得到耗费的时间
        double costTime = flowControlService.flowControl(taskInfo, flowControlParam);

        if (costTime > 0) {
            log.info("consumer {} flow control time {}",
                    ChannelType.getEnumByCode(taskInfo.getSendChannel()).getDescription(), costTime);
        }

    }

    /**
     * 得到限流值的配置
     *
     * apollo配置样例: key: flowControl value: {"flow_control_40":1}
     *
     * 渠道枚举: common.enums.ChannelType
     * @param sendChannel
     * @return
     */
    private Double getRateLimitConfig(Integer sendChannel) {

        //flowControlRule {} 得到限流值的配置
        String flowControlConfig = config.getProperty(FLOW_CONTROL_KEY, CommonConstant.EMPTY_JSON_OBJECT);
        JSONObject jsonObject = JSON.parseObject(flowControlConfig);

        //flow_control_发送渠道
        if (Objects.isNull(jsonObject.getDouble(FLOW_CONTROL_PREFIX + sendChannel))) {
            return null;
        }
        return jsonObject.getDouble(FLOW_CONTROL_PREFIX + sendChannel);
    }
}
