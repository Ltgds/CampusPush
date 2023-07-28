package com.ltgds.mypush.handler.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.model.EmailContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.utils.AustinFileUtils;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/7/28
 * @description 邮件发送处理
 */
@Component
@Slf4j
public class EmailHandler extends BaseHandler implements Handler {

    @Value("${austin.business.upload.crowd.path}")
    private String dataPath; //

    public EmailHandler() {
        channelCode = ChannelType.EMAIL.getCode();
    }

    /**
     * 邮件发送处理
     * @param taskInfo
     * @return
     */
    @Override
    public boolean handler(TaskInfo taskInfo) {
        //获取 发送文案模型
        EmailContentModel emailContentModel = (EmailContentModel) taskInfo.getContentModel();
        //获取账号信息和配置
        MailAccount account = getAccountConfig(taskInfo.getSendAccount());

        try {
            //文件
            File file = StrUtil.isNotBlank(emailContentModel.getUrl()) ? AustinFileUtils.getRemoteUrl2File(dataPath, emailContentModel.getUrl()) : null;
            //真正发送
            String result = Objects.isNull(file)
                    ? MailUtil.send(account, taskInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true)
                    : MailUtil.send(account, taskInfo.getReceiver(), emailContentModel.getTitle(), emailContentModel.getContent(), true, file);
        } catch (Exception e) {
            log.error("EmailHandler#handler fail{}, params:{}", Throwables.getStackTraceAsString(e), taskInfo);
            return false;
        }
        return true;
    }

    /**
     * 获取账号信息和配置
     * @param sendAccount
     * @return
     */
    private MailAccount getAccountConfig(Integer sendAccount) {
        /**
         * 修改 user/from/pass
         */
        String defaultConfig = "{\"host\":\"smtp.qq.com\",\"port\":465,\"user\":\"403686131@qq.com\"," +
                "\"pass\":\"123123123\",\"from\":\"403686131@qq.com\",\"starttlsEnable\":\"true\",\"auth\":true,\"sslEnable\":true}";

        MailAccount account = JSON.parseObject(defaultConfig, MailAccount.class);

        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            account.setAuth(account.isAuth())
                    .setStarttlsEnable(account.isStarttlsEnable())
                    .setSslEnable(account.isSslEnable())
                    .setCustomProperty("mail.smtp.ssl.socketFactory", sf);
            account.setTimeout(25000)
                    .setConnectionTimeout(25000);
        } catch (GeneralSecurityException e) {
            log.error("EmailHandler#getAccountConfig fail{}", Throwables.getStackTraceAsString(e));
        }
        return account;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}