package com.ltgds.mypush.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description 消息状态枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum MessageStatus implements PowerfulEnum{

    /**
     * 新建
     */
    INIT(10, "初始化状态"),

    /**
     * 停用
     */
    STOP(20, "停用"),

    /**
     * 启用
     */
    RUN(30, "启用"),

    /**
     * 等待发送
     */
    PENDING(40, "等待发送"),

    /**
     * 发送中
     */
    SENDING(50, "发送中"),

    /**
     * 发送成功
     */
    SEND_SUCCESS(60, "发送成功"),

    /**
     * 发送失败
     */
    SEND_FAIL(70, "发送失败")

    ;

    private final Integer code;
    private final String description;

}
