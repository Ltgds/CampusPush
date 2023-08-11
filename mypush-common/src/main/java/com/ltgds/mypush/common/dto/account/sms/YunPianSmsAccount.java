package com.ltgds.mypush.common.dto.account.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/10
 * @description 云片账号信息
 *
 * 账号参数示例：
 * {
 *  "url":"https://sms.yunpian.com/v2/sms/tpl_batch_send.json",
 *  "apikey":"caffff8234234231b5cd7",
 *  "tpl_id":"523333332",
 *  "supplierId":20,
 *  "supplierName":"云片",
 *  "scriptName":"YunPianSmsScript"
 * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YunPianSmsAccount extends SmsAccount{

    /**
     * apiKey
     */
    private String apiKey;

    /**
     * tplId
     */
    private String tplId;

    /**
     * api相关
     */
    private String url;
}
