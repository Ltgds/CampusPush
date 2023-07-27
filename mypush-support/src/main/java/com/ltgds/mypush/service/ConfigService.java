package com.ltgds.mypush.service;

import org.springframework.stereotype.Service;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description 读取配置服务
 */
public interface ConfigService {

    /**
     * 读取配置
     * 1. 当启动使用了apollo或nacos,有限读取远程配置
     * 2. 当没有启动远程配置,读取本地 local.properties配置文件的内容
     * @param key
     * @param defaultValue
     * @return
     */
    String getProperty(String key, String defaultValue);

}
