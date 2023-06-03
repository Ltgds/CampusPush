package com.ltgds.mypush.common.domain;

import com.ltgds.mypush.common.dto.model.ContentModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 发送任务信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskInfo {

    /**
     * 消息模板id
     */
    private Long messageTemplateId;

    /**
     * 业务id (追踪数据使用)
     * 生成逻辑 TaskInfoUtils
     */
    private Long businessId;

    /**
     * 接收者
     */
    private Set<String> receiver;

    /**
     * 发送的Id类型
     */
    private Integer idType;

    /**
     * 发送渠道
     */
    private Integer sendChannel;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 屏蔽类型
     */
    private Integer shieldType;

    /**
     * 发送文案模型
     * message_template表存储的content是JSON （所有内容都塞进去）
     * 不同的渠道要发送的内容不同（比如push会有img，而短信没有）
     */
    private ContentModel contentModel;

    /**
     * 发送账号（邮件下可能有多个发送账号、短信可有多个发送账号）
     */
    private Integer sendAccount;

}
