package com.ltgds.mypush.web.annotation;

import java.lang.annotation.*;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description 统一返回注解
 *
 * SOURCE: 注解只保留在源文件,编译成class文件时被遗弃
 * CLASS:注解被保存在class文件中,jvm加载后被遗弃;
 * RUNTIME: 注解不仅被保存到class文件中,jvm加载class文件之后仍然存在;
 *
 * @Retention 生命周期为: Java源文件(.java文件) --> .class文件 --> 内存中的字节码
 *              SOURCE < CLASS < RUNTIME
 *
 * @Target 说明了Annotation所修饰的对象范围
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //用于类、接口、enum和方法
@Retention(RetentionPolicy.RUNTIME) //注解不仅被保存到class文件中,jvm加载class文件之后仍然存在; CLASS:注解被保存在class文件中,jvm加载后被遗弃; SOURCE: 注解只保留在源文件,编译成class文件时被遗弃
@Documented //元注解,修饰其他注解
public @interface MyPushResult {
}
