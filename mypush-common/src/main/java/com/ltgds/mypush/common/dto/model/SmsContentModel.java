package com.ltgds.mypush.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 短信内容模型
 * <p>
 * 前端填写时分开,最后处理时会将url拼接在content上
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsContentModel extends ContentModel {

    /**
     * 短信发送内容
     */
    private String content;

    /**
     * 短信发送链接
     */
    private String url;
}
