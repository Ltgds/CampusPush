package com.ltgds.mypush.script;

import com.ltgds.mypush.domain.SmsRecord;
import com.ltgds.mypush.domain.sms.SmsParam;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 短信脚本 接口
 */
public interface SmsScript {

    /**
     * 发送短信
     *
     * @param smsParam
     * @return 渠道商发送接口返回值
     */
    List<SmsRecord> send(SmsParam smsParam);

    /**
     * 拉取回执
     * @param id 渠道账号的id
     * @return 渠道商回执接口返回值
     */
    List<SmsRecord> pull(Integer id);
}
