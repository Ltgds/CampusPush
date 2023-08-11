package handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.config.SupportThreadPoolConfig;
import com.ltgds.mypush.utils.RedisUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description 夜间屏蔽的延迟处理类
 *
 * 当消息下发时,在凌晨;让消息在次日早上9点进行推送
 */
@Service
@Slf4j
public class NightShieldLazyPendingHandler {

    private static final String NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY = "night_shield_send";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${austin.business.topic.name}")
    private String topicName;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 处理 夜间屏蔽(次日早上9点发送任务)
     */
    @XxlJob("nightShieldLazyJob")
    public void execute() {
        log.info("NightShieldLazyPendingHandler#execute!");

        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
            //当 night_shield_send对应的列表大于0
            while (redisUtils.lLen(NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY) > 0) {
                //拿到taskInfo信息
                String taskInfo = redisUtils.lPop(NIGHT_SHIELD_BUT_NEXT_DAY_SEND_KEY);

                if (StrUtil.isNotBlank(taskInfo)) {
                    try {
                        //使用kafka发送 定时任务处理
                        kafkaTemplate.send(topicName,
                                JSON.toJSONString(Arrays.asList(JSON.parseObject(taskInfo, TaskInfo.class)),
                                        new SerializerFeature[]{SerializerFeature.WriteClassName}));
                    } catch (Exception e) {
                        log.error("nightShieldLazyJob send kafka fail! e:{}, params:{}", Throwables.getStackTraceAsString(e), taskInfo);
                    }
                }
            }
        });
    }
}
