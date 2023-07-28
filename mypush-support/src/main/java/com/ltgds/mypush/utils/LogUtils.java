package com.ltgds.mypush.utils;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.CustomLogListener;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.domain.AnchorInfo;
import com.ltgds.mypush.common.domain.LogParam;
import com.ltgds.mypush.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Li Guoteng
 * @data 2023/7/28
 * @description 数据链路追踪
 */
@Slf4j
@Component
public class LogUtils extends CustomLogListener {

    @Autowired
    private SendMqService sendMqService;

    @Value("${austin.business.log.topic.name}") //austinTraceLog
    private String topicName;

    /**
     * 方法切面的日志 @OperationLog 所产生
     * @param logDTO
     * @throws Exception
     */
    @Override
    public void createLog(LogDTO logDTO) throws Exception {
        log.info(JSON.toJSONString(logDTO));
    }

    /**
     * 记录当前对象信息
     */
    public void print(LogParam logParam) {
        logParam.setTimestamp(System.currentTimeMillis());
        log.info(JSON.toJSONString(logParam));
    }

    /**
     * 记录打点信息
     */
    public void print(AnchorInfo anchorInfo) {
        anchorInfo.setLogTimestamp(System.currentTimeMillis());

        String message = JSON.toJSONString(anchorInfo);
        log.info(message);

        try {
            //将traceLog 发送给mq
            sendMqService.send(topicName, message);
        } catch (Exception e) {
            log.error("LogUtils#print send mq fail! e:{}, params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(anchorInfo));
        }
    }

    /**
     * 记录当前对象信息和打点信息
     */
    public void print(LogParam logParam, AnchorInfo anchorInfo) {
        print(logParam);
        print(anchorInfo);
    }

}
