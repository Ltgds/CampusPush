package com.ltgds.mypush.service.api.impl.action;

import com.ltgds.mypush.pipeline.BusinessProcess;
import com.ltgds.mypush.pipeline.ProcessContext;
import com.ltgds.mypush.service.api.impl.domain.SendTaskModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    }
}
