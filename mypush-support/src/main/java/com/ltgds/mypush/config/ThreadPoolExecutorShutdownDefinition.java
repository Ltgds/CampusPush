package com.ltgds.mypush.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Li Guoteng
 * @data 2023/7/25
 * @description  优雅关闭线程池
 */
@Component
@Slf4j
public class ThreadPoolExecutorShutdownDefinition implements ApplicationListener<ContextClosedEvent> {

    private final List<ExecutorService> POOLS = Collections.synchronizedList(new ArrayList<>(12));

    /**
     * 线程池中的任务在接收到应用程序关闭信号后最多等待多久就强行终止
     *  -- 即给剩余任务预留的时间,到时间后线程必须销毁
     */
    private final long AWAIT_TERMINATION = 20;

    /**
     * AWAIT_TERMINATION的单位
     */
    private final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 将线程添加到线程池中
     * @param executor
     */
    public void registryExecutor(ExecutorService executor) {
        POOLS.add(executor);
    }

    /**
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("容器关闭前处理线程池--优雅关闭线程池开始,当前要处理的线程池数量为:{} >>>>>>>>>>>>>>", POOLS.size());
        if (CollectionUtils.isEmpty(POOLS)) {
            return;
        }

        for (ExecutorService pool : POOLS) {
            //关闭线程池,不接收新的任务,但可以处理阻塞队列中已保存的业务
            pool.shutdown();

            try {
                //进入阻塞状态
                if (!pool.awaitTermination(AWAIT_TERMINATION, TIME_UNIT)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                    }
                }
            } catch (InterruptedException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}
