package com.ltgds.mypush.web;

import com.alibaba.fastjson.JSON;
import com.ltgds.mypush.dao.MessageTemplateDao;
import com.ltgds.mypush.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/5/28
 * @description
 */
@RestController
@Slf4j
public class TestController {
    private final MessageTemplateDao messageTemplateDao;

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
}
