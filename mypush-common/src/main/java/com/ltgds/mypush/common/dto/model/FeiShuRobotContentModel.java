package com.ltgds.mypush.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description 飞书群 机器人
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuRobotContentModel extends ContentModel{

    /**
     * 发送类型
     */
    private String sendType;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 发送标题
     */
    private String title;

    /**
     * 媒体Id
     */
    private String mediaId;


    /**
     * 富文本内容：[[{"tag":"text","text":"项目有更新: "},{"tag":"a","text":"请查看","href":"http://www.example.com/"},{"tag":"at","user_id":"ou_18eac8********17ad4f02e8bbbb"}]]
     */
    private String postContent;
}
