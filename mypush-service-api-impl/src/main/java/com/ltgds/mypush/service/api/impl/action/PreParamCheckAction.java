package com.ltgds.mypush.service.api.impl.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.pipeline.BusinessProcess;
import com.ltgds.mypush.pipeline.ProcessContext;
import com.ltgds.mypush.service.api.domain.MessageParam;
import com.ltgds.mypush.service.api.impl.domain.SendTaskModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Li Guoteng
 * @data 2023/6/4
 * @description 1.前置参数校验
 */
@Slf4j
@Service
public class PreParamCheckAction implements BusinessProcess<SendTaskModel> {

    /**
     * 最大的人数
     */
    public static final Integer BATCH_RECEIVER_SIZE = 100;

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel(); //拿到责任链上下文的数据

        Long messageTemplateId = sendTaskModel.getMessageTemplateId(); //发送消息模板id
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();

        //1. 没有传入 消息模板id 或 messageParam
        if (messageTemplateId == null || CollUtil.isEmpty(messageParamList)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }

        //2. 过滤 receiver == null 的 messageParam
        List<MessageParam> resultMessageParamList = messageParamList.stream()
                .filter(messageParam -> !StrUtil.isBlank(messageParam.getReceiver())) //将receiver不为空的放到result中
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(resultMessageParamList)) { //若result为空,说明都被过滤了
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }

        sendTaskModel.setMessageParamList(resultMessageParamList); //将receiver不为空的设置给 发送任务模型

        //3. 过滤receiver大于100的请求
        //anyMatch,匹配一个元素,只要一个满足即可
        if (messageParamList.stream().anyMatch(messageParam -> messageParam.getReceiver().split(StrUtil.COMMA).length > BATCH_RECEIVER_SIZE)) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TOO_MANY_RECEIVER));
            return;
        }
    }
}
