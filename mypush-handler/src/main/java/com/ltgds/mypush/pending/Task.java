package com.ltgds.mypush.pending;

import cn.hutool.core.collection.CollUtil;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.deduplication.DeduplicationRuleService;
import com.ltgds.mypush.discard.DiscardMessageService;
import com.ltgds.mypush.handler.HandlerHolder;
import com.ltgds.mypush.shield.ShieldService;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ShieldService shieldService;

    @Autowired
    DeduplicationRuleService deduplicationRuleService; //去重服务

    @Autowired
    private DiscardMessageService discardMessageService;

    @Autowired
    private HandlerHolder handlerHolder;

    private TaskInfo taskInfo;

    @Override
    public void run() {
        //1. 丢弃消息
        if (discardMessageService.isDiscard(taskInfo)) {
            return;
        }

        //2. 屏蔽消息
        shieldService.shield(taskInfo);

        //3. 通用去重功能
        if (CollUtil.isNotEmpty(taskInfo.getReceiver())) {
            deduplicationRuleService.duplication(taskInfo);
        }

        //4. 发送消息
        if (CollUtil.isNotEmpty(taskInfo.getReceiver())) {
            handlerHolder.route(taskInfo.getSendChannel()).doHandler(taskInfo);
        }
    }
}
