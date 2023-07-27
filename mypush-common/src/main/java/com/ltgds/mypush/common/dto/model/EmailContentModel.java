package com.ltgds.mypush.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailContentModel extends ContentModel{

    /**
     * 标题
     */
    private String title;

    /**
     * 内容  可写入html
     */
    private String content;

    /**
     * 邮件附件链接
     */
    private String url;
}
