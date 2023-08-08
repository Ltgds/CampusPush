package com.ltgds.mypush.common.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/7
 * @description 钉钉工作消息 账号信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingWorkNoticeAccount {

    /**
     * 应用的唯一标识key
     */
    private String appKey;

    /**
     * 应用的密码
     */
    private String appSecret;

    /**
     * 发送消息时使用的微应用的AgentId
     */
    private String agentId;
}
