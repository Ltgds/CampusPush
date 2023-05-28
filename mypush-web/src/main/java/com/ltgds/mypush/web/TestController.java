package com.ltgds.mypush.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Li Guoteng
 * @data 2023/5/28
 * @description
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    private String test() {
        return "hello";
    }
}
