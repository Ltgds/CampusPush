package com.ltgds.mypush.domain.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 对每种消息类型的 短信配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageTypeSmsConfig {

    /**
     * 权重(决定流量占比)
     */
    private Integer weight;

    /**
     * 短信模板若指定了账号,则该字段有值
     */
    private Integer sendAccount;

    /**
     * script名称
     */
    private String scriptName;
}
