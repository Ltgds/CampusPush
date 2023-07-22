package com.ltgds.mypush.service.api.impl.service;

import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.pipeline.ProcessContext;
import com.ltgds.mypush.pipeline.ProcessController;
import com.ltgds.mypush.pipeline.ProcessModel;
import com.ltgds.mypush.service.api.domain.BatchSendRequest;
import com.ltgds.mypush.service.api.domain.SendRequest;
import com.ltgds.mypush.service.api.domain.SendResponse;
import com.ltgds.mypush.service.api.impl.domain.SendTaskModel;
import com.ltgds.mypush.service.api.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Li Guoteng
 * @data 2023/6/10
 * @description 发送接口
 */
@Service
public class SendServiceImpl implements SendService {

    @Autowired
    private ProcessController processController;

    /**
     * 单文案发送
     * @param sendRequest
     * @return
     */
    @Override
    public SendResponse send(SendRequest sendRequest) {

        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParamList(Collections.singletonList(sendRequest.getMessageParam()))
                .build();

        ProcessContext content = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success())
                .build();

        ProcessContext process = processController.process(content);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }

    /**
     * 批量发送
     * @param batchSendRequest
     * @return
     */
    @Override
    public SendResponse batchSend(BatchSendRequest batchSendRequest) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(batchSendRequest.getMessageTemplateId())
                .messageParamList(batchSendRequest.getMessageParamList())
                .build();

        ProcessContext<ProcessModel> content = ProcessContext.builder()
                .code(batchSendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success())
                .build();

        ProcessContext process = processController.process(content);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }
}
