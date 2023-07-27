package com.ltgds.mypush.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 去重类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
public enum DeduplicationType {

    /**
     * 相同内容去重
     */
    CONTENT(10, "N分钟相同内容去重"),

    /**
     * 渠道接收消息 频次去重
     */
    FREQUENCY(20, "一天内N次相同渠道去重"),
    ;

    private final Integer code;
    private final String description;

    /**
     * 获取去重渠道 列表
     * @return
     */
    public static List<Integer> getDeduplicationList() {
        ArrayList<Integer> result = new ArrayList<>();
        for (DeduplicationType value : DeduplicationType.values()) {
            result.add(value.getCode());
        }
        return result;
    }

}
