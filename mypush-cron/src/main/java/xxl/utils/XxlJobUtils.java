package xxl.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.domain.MessageTemplate;
import xxl.constants.XxlJobConstant;
import xxl.entity.XxlJobGroup;
import xxl.entity.XxlJobInfo;
import enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xxl.enums.*;
import xxl.service.CronTaskService;

import java.util.Date;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/8/3
 * @description xxlJob工具类
 */
@Component
public class XxlJobUtils {

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.executor.jobHandlerName}")
    private String jobHandlerName;

    @Autowired
    private CronTaskService cronTaskService;

    /**
     * 构建xxlJobInfo信息
     * @param messageTemplate
     * @return
     */
    public XxlJobInfo buildXxlJobInfo(MessageTemplate messageTemplate) {
        //获取推送消息的时间
        String scheduleConf = messageTemplate.getExpectPushTime();

        //若没有指定cron表达式, 说明立即执行(给xxl-job延迟5秒的cron表达式)
        if (messageTemplate.getExpectPushTime().equals(String.valueOf(CommonConstant.FALSE))) {
            scheduleConf = DateUtil.format(DateUtil.offsetSecond(new Date(), XxlJobConstant.DELAY_TIME), CommonConstant.CRON_FORMAT);
        }

        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(queryJobGroupId()).jobDesc(messageTemplate.getName()) //执行器
                .author(messageTemplate.getCreator())
                .scheduleConf(scheduleConf) //调度配置
                .scheduleType(ScheduleTypeEnum.CRON.name()) //调度类型
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name()) //调度过期策略
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name()) //执行器路由策略
                .executorHandler(XxlJobConstant.JOB_HANDLER_NAME) //执行器任务handler
                .executorParam(String.valueOf(messageTemplate.getId())) //执行器任务参数
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name()) //阻塞处理策略
                .executorTimeout(XxlJobConstant.TIME_OUT) //任务执行超时时间
                .executorFailRetryCount(XxlJobConstant.RETRY_COUNT) //失败重试次数
                .glueType(GlueTypeEnum.BEAN.name())
                .triggerStatus(CommonConstant.FALSE) //调度状态
                .glueRemark(StrUtil.EMPTY)
                .glueSource(StrUtil.EMPTY)
                .alarmEmail(StrUtil.EMPTY) //报警邮件
                .childJobId(StrUtil.EMPTY) //子任务id
                .build();

        if (Objects.nonNull(messageTemplate.getCronTaskId())) {
            xxlJobInfo.setId(messageTemplate.getCronTaskId());
        }
        return xxlJobInfo;
    }

    /**
     * 根据 配置文件 的内容获取jobGroupId,没有则创建
     * @return
     */
    private Integer queryJobGroupId() {

        BasicResultVO basicResultVO = cronTaskService.getGroupId(appName, jobHandlerName);
        //配置文件数据为空
        if (Objects.isNull(basicResultVO.getData())) {
            //设置执行器组消息
            XxlJobGroup xxlJobGroup = XxlJobGroup.builder().appname(appName).title(jobHandlerName).addressType(CommonConstant.FALSE).build();
            //若响应状态正确
            if (RespStatusEnum.SUCCESS.getCode().equals(cronTaskService.createGroup(xxlJobGroup).getStatus())) {
                return (int) cronTaskService.getGroupId(appName, jobHandlerName).getData();
            }
        }

        return (Integer) basicResultVO.getData();
    }


}
