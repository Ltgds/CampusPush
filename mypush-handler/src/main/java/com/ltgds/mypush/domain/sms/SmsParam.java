package com.ltgds.mypush.domain.sms;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 发送短信参数
 */
@Data
@Builder
public class SmsParam {

    /**
     * 业务id
     */
    private Long messageTemplateId;

    /**
     * 需要发送的手机号
     */
    private Set<String> phones;

    /**
     * 发送账号的id(如果短信模板制定了发送账号,则改字段有值)
     *
     * 如果有账号id,就使用账号id检索
     * 如果没有账号id,就根据handler.domain.sms.SmsParam#scriptName检索
     */
    private Integer sendAccountId;

    /**
     * 渠道账号的脚本名标识
     */
    private String scriptName;

    /**
     * 发送文案
     */
    private String content;
}
