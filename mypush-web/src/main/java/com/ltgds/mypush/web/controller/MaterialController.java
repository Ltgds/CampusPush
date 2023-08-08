package com.ltgds.mypush.web.controller;

import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.web.annotation.MyPushAspect;
import com.ltgds.mypush.web.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description 素材管理接口
 */
@Slf4j
@MyPushAspect
@RestController
@RequestMapping("/material")
@Api("素材管理接口")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    /**
     * 素材上传接口
     * @param file
     * @param sendAccount
     * @param sendChannel
     * @param fileType
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("/素材上传接口")
    public BasicResultVO uploadMaterial(@RequestParam("file")MultipartFile file, String sendAccount, Integer sendChannel, String fileType) {
        if (ChannelType.DING_DING_WORK_NOTICE.getCode().equals(sendChannel)) {
            return materialService.dingDingMaterialUpload(file, sendAccount, fileType);
        } else if (ChannelType.ENTERPRISE_WE_CHAT_ROBOT.getCode().equals(sendChannel)) {
            return materialService.enterpriseWeChatRootMaterialUpload(file, sendAccount, fileType);
        } else if (ChannelType.ENTERPRISE_WE_CHAT.getCode().equals(sendChannel)) {
            return materialService.enterpriseWeChatMaterialUpload(file, sendAccount, fileType);
        }
        return BasicResultVO.success();
    }
}
