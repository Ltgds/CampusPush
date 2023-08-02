package com.ltgds.mypush.web.exception;

import com.google.common.base.Throwables;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.vo.BasicResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description
 */
@ControllerAdvice(basePackages = "com.ltgds.mypush.web.controller")
@ResponseBody
public class ExceptionHandlerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    public ExceptionHandlerAdvice() {

    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public BasicResultVO exceptionResponse(Exception e) {
        BasicResultVO result = BasicResultVO.fail(RespStatusEnum.ERROR_500, "\r\n" + Throwables.getStackTraceAsString(e) + "\r\n");
        log.error(Throwables.getStackTraceAsString(e));
        return result;
    }

    @ExceptionHandler({CommonException.class})
    @ResponseStatus(HttpStatus.OK)
    public BasicResultVO commonResponse(CommonException ce) {
        log.error(Throwables.getStackTraceAsString(ce));
        return new BasicResultVO(ce.getCode(), ce.getMessage(), ce.getRespStatusEnum());
    }
}
