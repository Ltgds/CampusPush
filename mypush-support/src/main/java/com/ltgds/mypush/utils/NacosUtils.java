package com.ltgds.mypush.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description
 */
@Slf4j
@Component
public class NacosUtils {

    @NacosInjected //
    private ConfigService configService;

    @Value("${nacos.group}")
    private String nacosGroup;

    @Value("${nacos.data-id}")
    private String nacosDataId;

    private final Properties properties = new Properties();

    public String getProperty(String key, String defaultValue) {

        try {
            String property = this.getContext();
            if (StringUtils.hasText(property)) {
                properties.load(new StringReader(property));
            }
        } catch (IOException e) {
            log.error("Nacos error:{}", ExceptionUtils.getStackTrace(e));
        }

        String property = properties.getProperty(key);
        return StrUtil.isBlank(property) ? defaultValue : property;
    }

    private String getContext() {

        String context = null;

        try {
            context = configService.getConfig(nacosDataId, nacosGroup, 5000);
        } catch (NacosException e) {
            log.error("Nacos error:{}", ExceptionUtils.getStackTrace(e));
        }
        return context;
    }


}
