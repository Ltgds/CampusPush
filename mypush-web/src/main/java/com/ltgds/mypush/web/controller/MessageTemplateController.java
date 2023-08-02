package com.ltgds.mypush.web.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.mq.SendMqService;
import com.ltgds.mypush.service.api.domain.MessageParam;
import com.ltgds.mypush.service.api.domain.SendRequest;
import com.ltgds.mypush.service.api.domain.SendResponse;
import com.ltgds.mypush.service.api.enmus.BusinessCode;
import com.ltgds.mypush.service.api.service.SendService;
import com.ltgds.mypush.web.amis.CommonAmisVo;
import com.ltgds.mypush.web.exception.CommonException;
import com.ltgds.mypush.web.service.MessageTemplateService;
import com.ltgds.mypush.web.utils.Convert4Amis;
import com.ltgds.mypush.web.vo.MessageTemplateParam;
import com.ltgds.mypush.web.vo.MessageTemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Li Guoteng
 * @data 2023/8/2
 * @description 消息模板管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/messageTemplate")
public class MessageTemplateController {

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private SendService sendService;

    @Value("${austin.business.upload.crowd.path}") //附件下载地址
    private String dataPath;

    /**
     * 若id不存在,则保存
     * 若id存在,则修改
     * @param messageTemplate
     * @return
     */
    @PostMapping("/save")
    public MessageTemplate saveOrUpdate(@RequestBody MessageTemplate messageTemplate) {
        return messageTemplateService.saveOrUpdate(messageTemplate);
    }

    /**
     * 列表数据
     * @param messageTemplateParam
     * @return
     */
    @GetMapping("/list")
    public MessageTemplateVo queryList(@Validated MessageTemplateParam messageTemplateParam) { //@Validated 参数校验
        Page<MessageTemplate> messageTemplates = messageTemplateService.queryList(messageTemplateParam);
        List<Map<String, Object>> result = Convert4Amis.flatListMap(messageTemplates.toList());
        return MessageTemplateVo.builder().count(messageTemplates.getTotalElements()).rows(result).build();
    }

    /**
     * 根据id查找
     * @param id
     * @return
     */
    @GetMapping("query/{id}")
    public Map<String, Object> queryById(@PathVariable("id") Long id) {
        return Convert4Amis.flatSingleMap(messageTemplateService.queryById(id));
    }

    /**
     * 根据Id复制
     * @param id
     */
    @PostMapping("copy/{id}")
    public void copyById(@PathVariable("id") Long id) {
        messageTemplateService.copy(id);
    }

    /**
     * 根据id删除
     * @param id
     */
    @DeleteMapping("delete/{id}")
    public void deleteByIds(@PathVariable("id") String id) {
        if (StrUtil.isNotBlank(id)) {
            //id使用多个 逗号 分隔开来
            List<Long> idList = Arrays.stream(id.split(StrUtil.COMMA)).map(Long::valueOf).collect(Collectors.toList());
            messageTemplateService.deleteByIds(idList);
        }
    }

    /**
     * 测试发送接口
     */
    @PostMapping("test")
    public SendResponse test(@RequestBody MessageTemplateParam messageTemplateParam) {

        //消息参数中的参数信息
        Map<String, String> variables = JSON.parseObject(messageTemplateParam.getMsgContent(), Map.class);
        //填充消息参数
        MessageParam messageParam = MessageParam.builder().receiver(messageTemplateParam.getReceiver()).variables(variables).build();
        //填充发送请求信息
        SendRequest sendRequest = SendRequest.builder().code(BusinessCode.COMMON_SEND.getCode())
                .messageTemplateId(messageTemplateParam.getId())
                .messageParam(messageParam).build();

        //请求返回的响应
        SendResponse response = sendService.send(sendRequest);

        //若发送不成功,抛出响应消息异常
        if (!Objects.equals(response.getCode(), RespStatusEnum.SUCCESS.getCode())) {
            throw new CommonException(response.getMsg());
        }
        return response;
    }

    /**
     * 获取需要测试的模板占位符,透出给Amis
     * @param id
     * @return
     */
    @PostMapping("test/content")
    public CommonAmisVo test(Long id) {
        MessageTemplate messageTemplate = messageTemplateService.queryById(id);

        return Convert4Amis.getTestContent(messageTemplate.getMsgContent());
    }
}
