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
    Map<String, String> map;

    /**
     * 消息模板跳转的url
     */
    String url;
}
