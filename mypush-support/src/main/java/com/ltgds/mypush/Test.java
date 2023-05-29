package com.ltgds.mypush;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/5/29
 * @description
 */
public class Test {

    public static void main(String[] args) {
        try {
            LombokTest lombok1 = LombokTest.builder().id("1").name("jack").build();
            LombokTest lombok2 = LombokTest.builder().id("2").name("lucy").build();
            List<LombokTest> lombokTests = Arrays.asList(lombok1, lombok2);

            if (CollUtil.isNotEmpty(lombokTests)) {
                System.out.println(JSON.toJSONString(lombokTests));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LombokTest {
        private String name;
        private String id;
    }
}


