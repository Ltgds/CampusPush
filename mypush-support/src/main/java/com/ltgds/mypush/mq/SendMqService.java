package com.ltgds.mypush.mq;

/**
 * @author Li Guoteng
 * @data 2023/6/10
 * @description 发送数据至消息队列
 */
public interface SendMqService {

    /**
     * 发送消息
     * @param topic
     * @param jsonValue
     * @param tagId
     */
    void send(String topic, String jsonValue, String tagId);

    /**
     * 发送消息
     * @param topic
     * @param jsonValue
     */
    void send(String topic, String jsonValue);
}
