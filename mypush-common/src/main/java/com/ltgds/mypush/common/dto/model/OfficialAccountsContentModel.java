package com.ltgds.mypush.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/7/22
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfficialAccountsContentModel extends ContentModel {

    /**
     * 消息模板发送的数据
     */
    private Map<String, String> officialAccountParam;

    /**
     * 消息模板跳转的url
     */
    private String url;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 模板消息跳转小程序的appId
     */
    private String miniProgramId;

    /**
     * 消息模板跳转小程序的页面路径
     */
    private String path;
}
