package com.ltgds.mypush.service.api.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description
 */
@Getter
@ToString
@AllArgsConstructor
public enum BusinessCode {

    /**
     * 普通发送流程
     */
    COMMON_SEND("send", "普通发送"),

    /**
     * 撤回流程
     */
    RECALL("recall", "撤回消息");

    /**
     * code 用来关联责任链的模板
     */
    private String code;

    /**
     * 类型说明
     */
    private String description;



}
