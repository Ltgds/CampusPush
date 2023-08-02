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
public class UserTimeLineVo {

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
         * title 模板名称
         */
        private String title;
        /**
         * detail 发送细节
         */
        private String detail;

        /**
         * 发送类型
         */
        private String sendType;

        /**
         * 模板创建者
         */
        private String creator;

    }
}
