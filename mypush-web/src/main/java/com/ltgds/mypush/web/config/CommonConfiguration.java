package com.ltgds.mypush.web.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;

/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description 通用配置
 */
@Configuration
public class CommonConfiguration {

    /**
     * 消息转换器  格式化输出json
     * @return
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        ArrayList<MediaType> supportedMediaTypes = Lists.newArrayList();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);

        return new HttpMessageConverters(fastConverter);
    }
}
