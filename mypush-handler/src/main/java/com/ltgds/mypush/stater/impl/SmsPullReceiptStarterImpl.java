package com.ltgds.mypush.stater.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.dto.account.sms.SmsAccount;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.dao.SmsRecordDao;
import com.ltgds.mypush.domain.ChannelAccount;
import com.ltgds.mypush.domain.SmsRecord;
import com.ltgds.mypush.script.SmsScript;
import com.ltgds.mypush.stater.ReceiptMessageStater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 拉取短信回执信息
 */
@Slf4j
@Component
public class SmsPullReceiptStarterImpl implements ReceiptMessageStater {

    @Autowired
    private ChannelAccountDao channelAccountDao;

    @Autowired
    private Map<String, SmsScript> scriptMap;

    @Autowired
    private SmsRecordDao smsRecordDao;

    /**
     * 拉取消息并入库
     */
    @Override
    public void start() {
        try {
            List<ChannelAccount> channelAccountList = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE, ChannelType.SMS.getCode());

            for (ChannelAccount channelAccount : channelAccountList) {
                SmsAccount smsAccount = JSON.parseObject(channelAccount.getAccountConfig(), SmsAccount.class);
                List<SmsRecord> smsRecordList = scriptMap.get(smsAccount.getScriptName())
                        .pull(channelAccount.getId().intValue()); //拉取回执
            }
        } catch (Exception e) {
            log.error("SmsPullReceiptStarter#start fail:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
