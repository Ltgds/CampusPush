package com.ltgds.mypush.web.controller;

import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.web.annotation.MyPushAspect;
import handler.RefreshDingDingAccessTokenHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description
 */
@MyPushAspect
@Api(tags = {"手动刷新token的接口"})
@RestController
public class RefreshTokenController {

    @Resource
    private RefreshDingDingAccessTokenHandler refreshDingDingAccessTokenHandler;

    /**
     * 按照不同的渠道刷新对应的token,ChannelType取值来源：common.enums.ChannelType
     * @param channelType
     * @return
     */
    @ApiOperation(value = "手动刷新token", notes = "钉钉/个推 token刷新")
    @GetMapping("/refresh")
    public String refresh(Integer channelType) {
        if (ChannelType.DING_DING_WORK_NOTICE.getCode().equals(channelType)) {
            refreshDingDingAccessTokenHandler.execute();
        }
        return "刷新成功";
    }


}
