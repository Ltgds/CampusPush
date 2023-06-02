package com.ltgds.mypush.service.api.service;

import com.ltgds.mypush.service.api.domain.BatchSendRequest;
import com.ltgds.mypush.service.api.domain.SendRequest;
import com.ltgds.mypush.service.api.domain.SendResponse;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 发送接口
 */
public interface SendService {

    /**
     * 单文案发送接口
     * @param sendRequest
     * @return
     */
    SendResponse send(SendRequest sendRequest);


    /**
     * 多文案发送接口
     * @param batchSendRequest
     * @return
     */
    SendResponse batchSend(BatchSendRequest batchSendRequest);
}
