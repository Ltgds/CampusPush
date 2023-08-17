package com.ltgds.mypush.web.utils;

import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.OfficialAccountParamConstant;
import com.ltgds.mypush.handler.impl.OfficialAccountHandler;
import com.ltgds.mypush.web.config.WeChatLoginConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/8/16
 * @description
 */
@Slf4j
@Component
public class LoginUtils {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 测试环境 使用
     * 获取 WeChatLoginConfig对象
     * @return
     */
    public WeChatLoginConfig getLoginConfig() {
        try {
            return applicationContext.getBean(OfficialAccountParamConstant.WE_CHAT_LOGIN_CONFIG, WeChatLoginConfig.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 测试环境使用
     *
     * 判断是否需要登录
     * @return
     */
    public boolean needLogin() {
        try {
            WeChatLoginConfig bean = applicationContext.getBean(OfficialAccountParamConstant.WE_CHAT_LOGIN_CONFIG, WeChatLoginConfig.class);

            if (CommonConstant.ENV_TEST.equals(env) && Objects.nonNull(bean)) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
}
