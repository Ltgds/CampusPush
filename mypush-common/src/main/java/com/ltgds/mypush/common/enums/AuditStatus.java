package com.ltgds.mypush.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description 审计状态枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum AuditStatus implements PowerfulEnum{

    /**
     * 待审核
     */
    WAIT_AUDIT(10, "待审核"),
    /**
     * 审核成功
     */
    AUDIT_SUCCESS(20, "审核成功"),
    /**
     * 被拒绝
     */
    AUDIT_REJECT(30, "被拒绝")
    ;

    private final Integer code;
    private final String description;
}
