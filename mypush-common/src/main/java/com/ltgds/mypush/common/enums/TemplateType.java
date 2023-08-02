package com.ltgds.mypush.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description 模板类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum TemplateType implements PowerfulEnum{

    /**
     * 定时类模板(后台定时调用)
     */
    CLOCKING(10, "定时类模板(后台定时调用)"),

    /**
     * 实时类模板(接口实时调用)
     */
    REALTIME(20, "实时类模板(接口实时调用)"),

    ;

    private final Integer code;
    private final String description;
}
