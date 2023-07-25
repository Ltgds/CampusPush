package com.ltgds.mypush.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/7/22
 * @description 发送消息的类型
 */
@Getter
@ToString
@AllArgsConstructor
public enum MessageType {

    NOTICE(10, "通知类消息", "notice"),
    MARKETING(20, "营销类短信", "marketing"),
    AUTH_CODE(30, "验证码短信", "auth_code")
    ;

    /**
     * 编码值
     */
    private Integer code;

    /**
     * 描述
     */
    private String description;

    /**
     * 英文标识
     */
    private String codeEn;

    /**
     * 通过code获取enum
     * @param code
     * @return
     */
    public static MessageType getEnumByCode(Integer code) {
        MessageType[] values = values();
        for (MessageType value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
