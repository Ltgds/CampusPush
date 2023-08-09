package com.ltgds.mypush.handler;

import com.ltgds.mypush.common.domain.AnchorInfo;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.AnchorState;
import com.ltgds.mypush.flowcontrol.FlowControlFactory;
import com.ltgds.mypush.flowcontrol.FlowControlParam;
import com.ltgds.mypush.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description 发送各个渠道的handler
 */
public abstract class BaseHandler implements Handler{

    @Autowired
    private HandlerHolder handlerHolder;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private FlowControlFactory flowControlFactory;

    /**
     * 标识渠道的code
     * 子类初始化时指定
     */
    protected Integer channelCode;

    /**
     * 限流相关参数
     * 子类初始化时指定
     */
    protected FlowControlParam flowControlParam;

    /**
     * 初始化 渠道 与 channelCode的映射关系
     */
    @PostConstruct
    private void init() {
        handlerHolder.putHandler(channelCode, this);
    }

    /**
     * 流量控制
     * @param taskInfo
     */
    public void flowControl(TaskInfo taskInfo) {
        //只有子类制定了限流参数,才需要限流
        if (Objects.nonNull(flowControlParam)) {
            flowControlFactory.flowControl(taskInfo, flowControlParam);
        }
    }

    @Override
    public void doHandler(TaskInfo taskInfo) {
        //进行流量控制
        flowControl(taskInfo);

        // 执行各个渠道的发送
        if (handler(taskInfo)) {

            //发送成功,打点
            logUtils.print(AnchorInfo.builder()
                            .bizId(taskInfo.getBizId())
                            .messageId(taskInfo.getMessageId())
                            .businessId(taskInfo.getBusinessId())
                            .ids(taskInfo.getReceiver())
                            .state(AnchorState.SEND_SUCCESS.getCode()) //发送成功
                    .build());
            return;
        }

        //发送失败,打点
        logUtils.print(AnchorInfo.builder()
                        .bizId(taskInfo.getBizId())
                        .messageId(taskInfo.getMessageId())
                        .businessId(taskInfo.getBusinessId())
                        .ids(taskInfo.getReceiver())
                        .state(AnchorState.SEND_FAIL.getCode()) //消息发送失败
                .build());
    }

    /**
     * 统一处理的handler接口
     * @param taskInfo
     * @return
     */
    public abstract boolean handler(TaskInfo taskInfo);
}
