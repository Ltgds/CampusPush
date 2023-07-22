package com.ltgds.mypush.common.enums;

import com.ltgds.mypush.common.dto.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 发送渠道类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum ChannelType {
    IM(10, "IM(站内信)", ImContentModel.class, "im"),
    PUSH(20, "push(通知栏)", PushContentModel.class, "push"),
    SMS(30, "sms(短信)", SmsContentModel.class, "sms"),
    EMAIL(40, "email(邮件)", EmailContentModel.class, "email"),
    OFFICIAL_ACCOUNT(50, "officialAccounts(服务号)", OfficialAccountsContentModel.class, "official_accounts"),
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
     * 内容模型Class
     */
    private Class contentModelClass;

    /**
     * 英文标识
     */
    private String codeEn;

    /**
     * 通过code获取Class
     * @param code
     * @return
     */
    public static Class getChanelModelClassByCode(Integer code) {
        ChannelType[] values = values();

        for (ChannelType value : values) {
            if (value.getCode().equals(code)) {
                return value.getContentModelClass();
            }
        }
        return null;
    }

    /**
     * 通过code获取enum
     * @param code
     * @return
     */
    public static ChannelType getEnumByCode(Integer code) {
        ChannelType[] values = values();

        for (ChannelType value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }


}
