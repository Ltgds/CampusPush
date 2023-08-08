package com.ltgds.mypush.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description 屏蔽 类型
 */
@Getter
@ToString
@AllArgsConstructor
public enum ShieldType implements PowerfulEnum{

    /**
     * 模板设置为夜间不屏蔽
     */
    NIGHT_NO_SHIELD(10, "夜间不屏蔽"),

    /**
     * 模板设置为夜间屏蔽 -- 凌晨接收到的消息将会被过滤掉
     */
    NIGHT_SHIELD(20, "夜间不屏蔽"),

    /**
     * 模板设置为夜间屏蔽(次日早上9点发送) -- 凌晨接收到的消息将会在次日发送
     */
    NIGHT_SHIELD_BUT_NEXT_DAY_SEND(30, "夜间屏蔽(次日早上9点发送)");

    private final Integer code;
    private final String description;
}
