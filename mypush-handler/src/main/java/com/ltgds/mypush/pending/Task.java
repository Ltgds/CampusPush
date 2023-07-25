package com.ltgds.mypush.pending;

import com.ltgds.mypush.common.domain.TaskInfo;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Li Guoteng
 * @data 2023/7/25
 * @description Task执行器
 * 1. 丢弃消息
 * 2. 屏蔽消息
 * 3. 通用去重功能
 * 4. 发送消息
 */
@Data
@Accessors(chain = true)
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Task implements Runnable{

    private TaskInfo taskInfo;

    @Override
    public void run() {
        //1. 丢弃消息

        //2. 屏蔽消息

        //3. 通用去重功能

        //4. 发送消息
    }
}