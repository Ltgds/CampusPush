package com.ltgds.mypush.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.OfficialAccountParamConstant;
import com.ltgds.mypush.common.constant.SendAccountConstant;
import com.ltgds.mypush.common.dto.account.sms.SmsAccount;
import com.ltgds.mypush.common.dto.account.weChat.WeChatOfficialAccount;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ConcurrentMap<ChannelAccount, WxMpService> officialAccountServiceMap = new ConcurrentHashMap<>();

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
                // return JSON.parseObject(channelAccount.getAccountConfig(), clazz);

                if (clazz.equals(WxMpService.class)) {
                    return (T) ConcurrentHashMapUtils.computeIfAbsent(officialAccountServiceMap, channelAccount,
                            account -> initOfficialAccountService(JSON.parseObject(account.getAccountConfig(), WeChatOfficialAccount.class)));
                } else {
                    return JSON.parseObject(channelAccount.getAccountConfig(), clazz);
                }
            }
        } catch (Exception e) {
            log.error("AccountUtils#getAccount fail! e:{}", Throwables.getStackTraceAsString(e));
        }

        return null;
    }

    /**
     * 初始化微信服务号
     * access_token 用redis存储
     *
     * @param officialAccount
     * @return
     */
    public WxMpService initOfficialAccountService(WeChatOfficialAccount officialAccount) {
        WxMpService wxMpService = new WxMpServiceImpl();
        WxMpRedisConfigImpl config = new WxMpRedisConfigImpl(redisTemplateWxRedisOps(),
                SendAccountConstant.OFFICIAL_ACCOUNT_ACCESS_TOKEN_PREFIX);

        config.setAppId(officialAccount.getAppId());
        config.setSecret(officialAccount.getSecret());
        config.setToken(officialAccount.getToken());
        config.useStableAccessToken(true);
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }

    @Bean
    public RedisTemplateWxRedisOps redisTemplateWxRedisOps() {
        return new RedisTemplateWxRedisOps(redisTemplate);
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
