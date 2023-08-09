package com.ltgds.mypush.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.security.PrivateKey;

/**
 * @author Li Guoteng
 * @data 2023/8/9
 * @description 限流枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum RateLimitStrategy {

    /**
     * 根据真实请求数限流（实际意义上的QPS）
     */
    REQUEST_RATE_LIMIT(10, "真实请求数限流"),

    /**
     * 根据发送用户数限流（人数限流）
     */
    SEND_USER_NUM_RATE_LIMIT(20, "发送用户数限流"),

    ;

    private final Integer code;

    private final String description;
}
