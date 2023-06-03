package com.ltgds.mypush.pipeline;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 业务执行器
 *
 * 业务抽象的接口
 */
public interface BusinessProcess<T extends ProcessModel>{

    /**
     * 真正处理逻辑
     * @param context
     */
    void process(ProcessContext<T> context);

}
