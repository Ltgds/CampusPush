package com.ltgds.mypush.web.annotation;

import java.lang.annotation.*;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description 接口切面注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyPushAspect {
}
