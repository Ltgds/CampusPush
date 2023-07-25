package com.ltgds.mypush.web;

import com.alibaba.fastjson.JSON;
import com.ltgds.mypush.dao.MessageTemplateDao;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.service.api.domain.MessageParam;
import com.ltgds.mypush.service.api.domain.SendRequest;
import com.ltgds.mypush.service.api.domain.SendResponse;
import com.ltgds.mypush.service.api.enmus.BusinessCode;
import com.ltgds.mypush.service.api.service.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/5/28
 * @description
 */
@RestController
@Slf4j
public class TestController {
    @Autowired
    private final MessageTemplateDao messageTemplateDao;
    @Resource
    private SendService sendService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/redis")
    private String testRedis() {
        redisTemplate.opsForValue().set("hel", "ddd");
        return redisTemplate.opsForValue().get("hel");
    }


    public TestController(MessageTemplateDao messageTemplateDao) {
        this.messageTemplateDao = messageTemplateDao;
    }

    @RequestMapping("/test")
    private String test() {
        System.out.println("测试logback");
        log.info("测试logback11111");
        return "hello";
    }

    @RequestMapping("/database")
    private String testDataBase() {
        List<MessageTemplate> list = messageTemplateDao.findAllByIsDeletedEquals(0, PageRequest.of(0, 10));
        return JSON.toJSONString(list);
    }

    @RequestMapping("/send")
    private String testSend() {
        SendRequest sendRequest = SendRequest.builder()
                .code(BusinessCode.COMMON_SEND.getCode())
                .messageTemplateId(1L)
                .messageParam(MessageParam.builder().receiver("18792838259").build()).build();

        SendResponse response = sendService.send(sendRequest);
        return JSON.toJSONString(response);
    }
}
