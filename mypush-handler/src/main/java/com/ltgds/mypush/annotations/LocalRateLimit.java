package com.ltgds.mypush.annotations;

import com.ltgds.mypush.enums.RateLimitStrategy;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author Li Guoteng
 * @data 2023/8/9
 * @description 单机限流注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface LocalRateLimit {
    //默认根据真实请求数限流
    RateLimitStrategy rateLimitStrategy() default RateLimitStrategy.REQUEST_RATE_LIMIT;
}
