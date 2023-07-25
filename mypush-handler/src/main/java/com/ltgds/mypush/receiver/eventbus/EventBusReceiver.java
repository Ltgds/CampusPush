package com.ltgds.mypush.receiver.eventbus;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.constans.MessageQueuePipeline;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.mq.eventbus.EventBusListener;
import com.ltgds.mypush.receiver.service.ConsumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/6/10
 * @description
 */
@Component
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.EVENT_BUS)
@Slf4j
public class EventBusReceiver implements EventBusListener {

    @Autowired
    private ConsumeService consumeService;

    @Override
    @Subscribe
    public void consume(List<TaskInfo> lists) {
        log.error(JSON.toJSONString(lists));
        consumeService.consume2Send(lists);
    }

    @Override
    @Subscribe
    public void recall(MessageTemplate messageTemplate) {

    }
}
