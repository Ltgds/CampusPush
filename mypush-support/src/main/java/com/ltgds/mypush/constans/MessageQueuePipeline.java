package com.ltgds.mypush.constans;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 消息队列常量
 */
public interface MessageQueuePipeline {

    String EVENT_BUS = "eventBus";
    String KAFKA = "kafka";
    String RABBIT_MQ = "rabbitMq";
    String ROCKET_MQ = "rocketMq";

}
