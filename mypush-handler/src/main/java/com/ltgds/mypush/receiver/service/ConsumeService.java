package com.ltgds.mypush.receiver.service;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.domain.MessageTemplate;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/25
 * @description 消息消费服务
 */
public interface ConsumeService {

    /**
     * 从 MQ 拉取消息进行消费,发送消息
     * @param taskInfoLists
     */
    void consume2Send(List<TaskInfo> taskInfoLists);

    /**
     * 从 MQ 拉取消息进行消费,撤回消息
     * @param messageTemplate
     */
    void consume2recall(MessageTemplate messageTemplate);
}
