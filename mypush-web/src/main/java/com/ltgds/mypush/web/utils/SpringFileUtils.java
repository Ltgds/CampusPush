package com.ltgds.mypush.web.utils;

import groovyjarjarantlr4.v4.runtime.dfa.DFASerializer;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description multipartFile 转成 File 对象
 */
public class SpringFileUtils {

    /**
     * multipartFile 转成 File对象
     *
     * @param multipartFile 以表单形式实现文件的上传功能
     * @return
     */
    public static File getFile(MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            //将multipartFile文件
            byte[] ss = multipartFile.getBytes();
            for (int i = 0; i < ss.length; i++) {
                //通过文件输出流写到file中
                out.write(ss[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(out)) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }
}
