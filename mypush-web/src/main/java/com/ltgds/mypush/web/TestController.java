package com.ltgds.mypush.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Li Guoteng
 * @data 2023/5/28
 * @description
 */
@RestController
@Slf4j
public class TestController {

    @RequestMapping("/test")
    private String test() {
        System.out.println("测试logback");
        log.info("测试logback11111");
        return "hello";
    }
}
