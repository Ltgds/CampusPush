package com.ltgds.mypush.mq.eventbus;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.domain.MessageTemplate;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/6/10
 * @description 监听器
 */
public interface EventBusListener {

    /**
     * 消费消息
     * @param lists
     */
    void consume(List<TaskInfo> lists);

    /**
     * 撤回消息
     * @param messageTemplate
     */
    void recall(MessageTemplate messageTemplate);

}
