package com.ltgds.mypush.script.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.dto.account.sms.TencentSmsAccount;
import com.ltgds.mypush.common.enums.SmsStatus;
import com.ltgds.mypush.domain.SmsRecord;
import com.ltgds.mypush.domain.sms.SmsParam;
import com.ltgds.mypush.script.SmsScript;
import com.ltgds.mypush.utils.AccountUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.backoff.ObjectWaitSleeper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description
 * 1.发送短信接入文档:https://cloud.tencent.com/document/api/382/55981
 * 2.使用SDK直接调用
 * 3.使用API Explorer生成代码
 */
@Slf4j
@Component("TencentSmsScript")
public class TencentSmsScript implements SmsScript {

    private static final Integer PHONE_NUM = 11;

    @Autowired
    private AccountUtils accountUtils;


    @Override
    public List<SmsRecord> send(SmsParam smsParam) {

        try {
            TencentSmsAccount tencentSmsAccount = Objects.nonNull(smsParam.getSendAccountId())
                    ? accountUtils.getAccountById(smsParam.getSendAccountId(), TencentSmsAccount.class) //若账号存在使用账号id检索
                    : accountUtils.getSmsAccountByScriptName(smsParam.getScriptName(), TencentSmsAccount.class); //若账号不存在则使用脚本名称检索

            //初始化client
            SmsClient client = init(tencentSmsAccount);

            //组装发送参数
            SendSmsRequest request = assembleSendReq(smsParam, tencentSmsAccount);
            //组装发送消息的返回值
            SendSmsResponse response = client.SendSms(request);
            return assembleSendSmsRecord(smsParam, response, tencentSmsAccount);
        } catch (Exception e) {
            log.error("TencentSmsScript#send fail:{},params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(smsParam));
            return null;
        }
    }


    /**
     * 初始化 client
     * @param account
     * @return
     */
    private SmsClient init(TencentSmsAccount account) {
        Credential cred = new Credential(account.getSecretId(), account.getSecretKey());

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(account.getUrl());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        SmsClient client = new SmsClient(cred, account.getRegion(), clientProfile);

        return client;
    }

    /**
     * 组装发送短信参数
     * @param smsParam
     * @param account
     * @return
     */
    private SendSmsRequest assembleSendReq(SmsParam smsParam, TencentSmsAccount account) {
        SendSmsRequest request = new SendSmsRequest();
        String[] phoneNumberSet = smsParam.getPhones().toArray(new String[smsParam.getPhones().size() - 1]);

        request.setPhoneNumberSet(phoneNumberSet);
        request.setSmsSdkAppId(account.getSmsSdkAppId());
        request.setSignName(account.getSignName());
        request.setTemplateId(account.getTemplateId());

        String[] templateParamSet = {smsParam.getContent()};
        request.setTemplateParamSet(templateParamSet);
        request.setSessionContext(IdUtil.fastSimpleUUID());

        return request;
    }

    /**
     * 组装发送消息的返回值
     * @param smsParam
     * @param response
     * @param tencentSmsAccount
     * @return
     */
    private List<SmsRecord> assembleSendSmsRecord(SmsParam smsParam, SendSmsResponse response, TencentSmsAccount tencentSmsAccount) {
        if (Objects.isNull(response) || ArrayUtil.isEmpty(response.getSendStatusSet())) {
            return null;
        }

        List<SmsRecord> smsRecordList = new ArrayList<>();
        for (SendStatus sendStatus : response.getSendStatusSet()) {
            // 腾讯返回的电话号有前缀，这里取巧直接翻转获取手机号
            String phone = new StringBuilder(new StringBuilder(sendStatus.getPhoneNumber())
                    .reverse().substring(0, PHONE_NUM)).reverse().toString();

            SmsRecord smsRecord = SmsRecord.builder()
                    .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                    .messageTemplateId(smsParam.getMessageTemplateId())
                    .phone(Long.valueOf(phone))
                    .supplierId(tencentSmsAccount.getSupplierId())
                    .supplierName(tencentSmsAccount.getSupplierName())
                    .msgContent(smsParam.getContent())
                    .seriesId(sendStatus.getSerialNo()) //批次号id
                    .chargingNum(Math.toIntExact(sendStatus.getFee())) //计费条数
                    .status(SmsStatus.SEND_SUCCESS.getCode())
                    .reportContent(sendStatus.getCode()) //回执信息
                    .created(Math.toIntExact(DateUtil.currentSeconds()))
                    .updated(Math.toIntExact(DateUtil.currentSeconds()))
                    .build();

            smsRecordList.add(smsRecord);
        }
        return smsRecordList;
    }

    //************************************************************************************************

    @Override
    public List<SmsRecord> pull(Integer accountId) {
        try {
            TencentSmsAccount account = accountUtils.getAccountById(accountId, TencentSmsAccount.class);
            SmsClient client = init(account);
            PullSmsSendStatusRequest request = assemblePullRequest(account);
            PullSmsSendStatusResponse response = client.PullSmsSendStatus(request);

            return assemblePullSmsRecord(account, response);
        } catch (Exception e) {
            log.error("TencentSmsReceipt#pull fail!{}", Throwables.getStackTraceAsString(e));
            return null;
        }
    }

    /**
     * 组装 拉取回执入参
     * @param account
     * @return
     */
    private PullSmsSendStatusRequest assemblePullRequest(TencentSmsAccount account) {
        PullSmsSendStatusRequest request = new PullSmsSendStatusRequest();

        request.setLimit(10L);
        request.setSmsSdkAppId(account.getSmsSdkAppId());
        return request;
    }

    /**
     * 组装 拉取回执信息
     * @param account
     * @param response
     * @return
     */
    private List<SmsRecord> assemblePullSmsRecord(TencentSmsAccount account, PullSmsSendStatusResponse response) {
        List<SmsRecord> smsRecordList = new ArrayList<>();
        if (Objects.nonNull(response) && Objects.nonNull(response.getPullSmsSendStatusSet()) && response.getPullSmsSendStatusSet().length > 0) {
            for (PullSmsSendStatus pullSmsSendStatus : response.getPullSmsSendStatusSet()) {
                SmsRecord smsRecord = SmsRecord.builder()
                        .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                        .messageTemplateId(0L)
                        .phone(Long.valueOf(pullSmsSendStatus.getSubscriberNumber()))
                        .supplierId(account.getSupplierId())
                        .supplierName(account.getSupplierName())
                        .msgContent("")
                        .seriesId(pullSmsSendStatus.getSerialNo())
                        .chargingNum(0)
                        .status("SUCCESS".equals(pullSmsSendStatus.getReportStatus()) ? SmsStatus.RECEIVE_SUCCESS.getCode() : SmsStatus.RECEIVE_FAIL.getCode())
                        .reportContent(pullSmsSendStatus.getDescription())
                        .updated(Math.toIntExact(pullSmsSendStatus.getUserReceiveTime()))
                        .created(Math.toIntExact(DateUtil.currentSeconds()))
                        .build();
                smsRecordList.add(smsRecord);
            }
        }
        return smsRecordList;
    }
}
