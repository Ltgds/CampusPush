package com.ltgds.mypush.common.dto.account.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/10
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsAccount {

    /**
     * 渠道商标识id
     */
    protected Integer supplierId;

    /**
     * 渠道商标识名字
     */
    protected String supplierName;

    /**
     * 重要类名,定位到具体的处理"下发/回执"逻辑
     *
     * 根据ScriptName对应具体的某一个短信账号
     */
    protected String scriptName;
}
