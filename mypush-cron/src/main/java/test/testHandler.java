package test;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xxl.config.XxlJobConfig;

/**
 * @author Li Guoteng
 * @data 2023/8/3
 * @description
 */
@Service
@Slf4j
public class testHandler {

    /**
     * 处理后台的 定时任务消息
     */
    @XxlJob("testJob")
    public void execute() {
        log.info("CronTaskHandler#execute messageTemplateId:{} cron exec!", XxlJobHelper.getJobParam());
    }

}
