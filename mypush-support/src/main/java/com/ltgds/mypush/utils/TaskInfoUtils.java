package com.ltgds.mypush.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 生成 消息推送URL 工具类
 */
public class TaskInfoUtils {

    private static final int TYPE_FLAG = 1000000;
    private static final char PARAM = '?';

    /**
     * 生成BusinessId
     * <p>
     * 模板类型 + 模板Id + 当天日期（固定16位）
     *
     * @param templateId
     * @param templateType
     * @return
     */
    public static Long generateBusinessId(Long templateId, Integer templateType) {
        Integer today = Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN));
        return Long.valueOf(String.format("%d%s", templateType * TYPE_FLAG + templateId, today));
    }

    /**
     * 对url添加平台参数（用于追踪数据）
     * @param url
     * @param templateId
     * @param templateType
     * @return
     */
    public static String generateUrl(String url, Long templateId, Integer templateType) {
        url = url.trim();
        Long businessId = generateBusinessId(templateId, templateType);
        if (url.indexOf(PARAM) == -1) {
            return url + "?track_code_bid=" + businessId;
        } else {
            return url + "&track_code_bid=" + businessId;
        }
    }

}
