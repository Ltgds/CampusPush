package com.ltgds.mypush.web.aspect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description 切面类
 */
@Slf4j
@Aspect
@Component
public class MyPushAspect {

    @Autowired
    private HttpServletRequest request;

    /**
     * 同一个请求的key
     */
    private final String REQUEST_ID_KEY = "request_unique_id";

    /**
     * 只切MyPushAspect注解
     */
    @Pointcut("@within(com.ltgds.mypush.web.annotation.MyPushAspect) || @@annotation(com.ltgds.mypush.web.annotation.MyPushAspect)")
    public void executeService() {
    }

    /**
     * 前置通知,方法调用前被调用
     * @param joinPoint
     */
    @Before("executeService()")
    public void doBeforeAdvice(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //打印请求日志
        this.printRequestLog(methodSignature, joinPoint.getArgs());
    }

    /**
     * 异常通知
     * @param ex
     */
    @AfterThrowing(value = "executeService()", throwing = "ex")
    public void doAfterThrowingAdvice(Throwable ex) {
        printExceptionLog(ex);
    }

    /**
     * 打印异常日志
     * @param ex
     */
    private void printExceptionLog(Throwable ex) {
        JSONObject logVo = new JSONObject();
        logVo.put("id", request.getAttribute(REQUEST_ID_KEY));
        log.error(JSON.toJSONString(logVo), ex);
    }

    /**
     * 打印请求日志
     * @param methodSignature
     * @param argObs
     */
    private void printRequestLog(MethodSignature methodSignature, Object[] argObs) {

    }

}
