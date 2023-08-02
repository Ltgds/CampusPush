package com.ltgds.mypush.web.amis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsTimeLineVo {

    /**
     * items
     */
    private List<ItemsVO> items;

    /**
     * ItemsVO
     */
    @Data
    @Builder
    public static class ItemsVO {
        /**
         * 业务ID
         */
        private String businessId;
        /**
         * detail 发送内容
         */
        private String content;

        /**
         * 发送状态
         */
        private String sendType;

        /**
         * 回执状态
         */
        private String receiveType;

        /**
         * 回执报告
         */
        private String receiveContent;

        /**
         * 发送时间
         */
        private String sendTime;

        /**
         * 回执时间
         */
        private String receiveTime;


    }

}
