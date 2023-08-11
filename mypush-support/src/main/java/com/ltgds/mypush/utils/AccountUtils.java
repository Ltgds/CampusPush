package com.ltgds.mypush.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.dto.account.sms.SmsAccount;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description 获取账号信息工具类
 */
@Slf4j
@Configuration
public class AccountUtils {

    @Autowired
    private ChannelAccountDao channelAccountDao;

    /**
     * 微信小程序：返回WxMaService
     * 微信服务号：返回WxMpService
     * 其他渠道：返回XXXAccount账号对象
     *
     * @param sendAccountId
     * @param clazz
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked") //告诉编译器忽略 unchecked 警告信息，如使用List，ArrayList等未进行参数化产生的警告信息。
    public <T> T getAccountById(Integer sendAccountId, Class<T> clazz) {
        try {
            Optional<ChannelAccount> optionalChannelAccount = channelAccountDao.findById(Long.valueOf(sendAccountId));
            if (optionalChannelAccount.isPresent()) {
                ChannelAccount channelAccount = optionalChannelAccount.get();
                return JSON.parseObject(channelAccount.getAccountConfig(), clazz);
            }
        } catch (Exception e) {
            log.error("AccountUtils#getAccount fail! e:{}", Throwables.getStackTraceAsString(e));
        }

        return null;
    }

    /**
     * 通过脚本名 匹配到对应的账号信息
     * @param scriptName 脚本名
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> T getSmsAccountByScriptName(String scriptName, Class<T> clazz) {
        try {
            //获得SMS渠道账号列表
            List<ChannelAccount> channelAccountList = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE, ChannelType.SMS.getCode());

            for (ChannelAccount channelAccount : channelAccountList) {

                try {
                    //拿到sms账号配置
                    SmsAccount smsAccount = JSON.parseObject(channelAccount.getAccountConfig(), SmsAccount.class);
                    //通过脚本名 匹配到对应的账号信息
                    if (smsAccount.getScriptName().equals(scriptName)) {
                        return JSON.parseObject(channelAccount.getAccountConfig(), clazz);
                    }
                } catch (Exception e) {
                    log.error("AccountUtils#getSmsAccount parse fail! e:{},account:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(channelAccount));
                }
            }
        } catch (Exception e) {
            log.error("AccountUtils#getSmsAccount not found!:{}", scriptName);
        }

        return null;
    }
}
