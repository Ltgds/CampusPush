package com.ltgds.mypush.web.controller;

import com.ltgds.mypush.service.api.domain.BatchSendRequest;
import com.ltgds.mypush.service.api.domain.SendRequest;
import com.ltgds.mypush.service.api.domain.SendResponse;
import com.ltgds.mypush.service.api.service.SendService;
import com.ltgds.mypush.web.annotation.MyPushAspect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Li Guoteng
 * @data 2023/8/2
 * @description
 */
@Api(tags = {"发送消息"})
@RestController
@MyPushAspect
public class SendController {

    @Autowired
    private SendService sendService;

    /**
     * 单个文案下发相同的人
     * @param sendRequest
     * @return
     */
    @ApiOperation(value = "下发接口", notes = "多渠道多类型下发消息,目前支持邮件; 类型支持:验证码、通知类、营销类")
    @PostMapping("/send")
    public SendResponse send(@RequestBody SendRequest sendRequest) {
        return sendService.send(sendRequest);
    }

    /**
     * 不同文案发送到不同的人
     * @param batchSendRequest
     * @return
     */
    @ApiOperation(value = "batch下发接口", notes = "多渠道多类型下发消息,目前支持邮件, 类型支持:验证码、通知类、营销类")
    @PostMapping("/batchSend")
    public SendResponse batchSend(@RequestBody BatchSendRequest batchSendRequest) {
        return sendService.batchSend(batchSendRequest);
    }

}
