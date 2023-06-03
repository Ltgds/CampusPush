package com.ltgds.mypush.service.api.impl.action;

import com.ltgds.mypush.pipeline.BusinessProcess;
import com.ltgds.mypush.pipeline.ProcessContext;
import com.ltgds.mypush.service.api.impl.domain.SendTaskModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Li Guoteng
 * @data 2023/6/4
 * @description 2.拼装参数
 */
@Slf4j
@Service
public class AssembleAction implements BusinessProcess<SendTaskModel> {

    @Override
    public void process(ProcessContext<SendTaskModel> context) {

    }
}
