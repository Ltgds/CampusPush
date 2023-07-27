package com.ltgds.mypush.common.constant;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description
 */
public class CommonConstant {

    public static final String PERIOD = ".";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String POUND = "#";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String EMPTY_STRING = "";
    public static final String RADICAL = "|";

    public static final String QM_STRING = "?";
    public static final String EQUAL_STRING = "=";
    public static final String AND_STRING = "&";

    public static final String ONE = "1";
    public static final String ZERO = "0";
    public static final String MINUS_ONE = "-1";
    public static final String YES = "Y";
    public static final String NO = "N";

    public static final char QM = '?';

    /**
     * boolean转换
     */
    public static final Integer TRUE = 1;
    public static final Integer FALSE = 0;

    /**
     * 加密算法
     */
    public static final String HMAC_SHA256_ENCRYPTION_ALGO = "HmacSHA256";

    /**
     * 编码格式
     */
    public static final String CHARSET_NAME = "UTF-8";

    /**
     * HTTP请求内容格式
     */
    public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    public static final String CONTENT_TYPE_TEXT = "text/html;charset=utf-8";
    public static final String CONTENT_TYPE_XML = "application/xml; charset=utf-8";
    public static final String CONTENT_TYPE_FORM_URL_ENCODE = "application/x-www-form-urlencoded;charset=utf-8";
    public static final String CONTENT_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * HTTP请求方法
     */
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";

    /**
     * JSON默认值
     */
    public static final String EMPTY_JSON_OBJECT = "{}";
    public static final String EMPTY_VALUE_JSON_ARRAY = "[]";

    /**
     * cron时间格式
     */
    public static final String CRON_FORMAT = "ss mm HH dd MM ? yyyy-yyyy";

    /**
     * 环境常量
     */
    public static final String ENV_DEV = "dev";
    public static final String ENV_TEST = "test";
}
