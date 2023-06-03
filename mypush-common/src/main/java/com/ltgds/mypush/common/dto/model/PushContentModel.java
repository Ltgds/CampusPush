package com.ltgds.mypush.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 通知栏消息推送
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushContentModel extends ContentModel{

    private String title;
    private String content;
    private String url;
}
