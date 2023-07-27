package com.ltgds.mypush.handler;

import com.ltgds.mypush.common.domain.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description 发送各个渠道的handler
 */
public abstract class BaseHandler implements Handler{

    @Autowired
    private HandlerHolder handlerHolder;

    /**
     * 标识渠道的code
     * 子类初始化时指定
     */
    protected Integer channelCode;

    /**
     * 初始化 渠道 与 channelCode的映射关系
     */
    @PostConstruct
    private void init() {
        handlerHolder.putHandler(channelCode, this);
    }

    @Override
    public void doHandler(TaskInfo taskInfo) {
        if (handler(taskInfo)) {
            return;
        }
    }

    /**
     * 统一处理的handler接口
     * @param taskInfo
     * @return
     */
    public abstract boolean handler(TaskInfo taskInfo);
}
