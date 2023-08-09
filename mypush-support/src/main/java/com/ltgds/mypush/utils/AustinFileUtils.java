package com.ltgds.mypush.utils;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.nacos.common.utils.IoUtils;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description
 */
@Slf4j
public class AustinFileUtils {

    /**
     * 读取 远程链接 返回File对象
     * @param path          文件路径
     * @param remoteUrl     文件访问链接
     * @return
     */
    public static File getRemoteUrl2File(String path, String remoteUrl) {
        try {
            URL url = new URL(remoteUrl);
            File file = new File(path, url.getPath());
            if (!file.exists()) {
                file.getParentFile().mkdirs(); //生成层级文件夹
                IoUtil.copy(url.openStream(), new FileOutputStream(file));
            }
            return file;
        } catch (IOException e) {
            log.error("AustinFileUtils#getRemoteUrl2File fail:{},remoteUrl:{}", Throwables.getStackTraceAsString(e), remoteUrl);
        }
        return null;
    }

    /**
     * 读取 远程链接集合 返回有效的File对象集合
     * @param path
     * @param remoteUrls
     * @return
     */
    public static List<File> getRemoteUrl2File(String path, Collection<String> remoteUrls) {
        List<File> files = new ArrayList<>();

        remoteUrls.forEach(remoteUrl -> {
            File file = getRemoteUrl2File(path, remoteUrl);
            if (file != null) {
                files.add(file);
            }
        });
        return files;
    }
}
