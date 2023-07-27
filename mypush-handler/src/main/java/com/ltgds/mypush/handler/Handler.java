package com.ltgds.mypush.handler;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.domain.MessageTemplate;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description 消息处理器
 */
public interface Handler {

    /**
     * 处理器
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     * @param messageTemplate
     */
    void recall(MessageTemplate messageTemplate);
}
