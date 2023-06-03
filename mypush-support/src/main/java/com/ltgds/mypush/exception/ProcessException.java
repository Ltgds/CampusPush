package com.ltgds.mypush.exception;

import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.pipeline.ProcessContext;
import com.ltgds.mypush.pipeline.ProcessController;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description
 */
public class ProcessException extends RuntimeException{

    /**
     * 流程处理上下文
     */
    private final ProcessContext processContext;

    public ProcessException(ProcessContext processContext) {
        super();
        this.processContext = processContext;
    }

    public ProcessException(ProcessContext processContext, Throwable cause) {
        super(cause);
        this.processContext = processContext;
    }

    @Override
    public String getMessage() {
        if (this.processContext != null) {
            return this.processContext.getResponse().getMsg();
        } else {
            return RespStatusEnum.CONTEXT_IS_NULL.getMsg();
        }
    }

    public ProcessContext getProcessContext() {
        return processContext;
    }
}
