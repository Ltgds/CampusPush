package com.ltgds.mypush.pending;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Li Guoteng
 * @data 2023/8/4
 * @description pending初始化参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PendingParam<T> {

    /**
     * 阻塞队列实现类
     */
    private BlockingQueue<T> queue;

    /**
     * batch触发执行的数量阈值
     */
    private Integer numThreshold;

    /**
     * batch触发执行的时间阈值,单位毫秒
     */
    private Long timeThreshold;

    /**
     * 消费线程实例
     */
    protected ExecutorService executorService;
}
