package com.ltgds.mypush.common.dto.account.weChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description 企业微信 机器人 账号信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRobotAccount {

    /**
     * 自定义群 机器人中的 webhook
     *
     * webhook: 反向 API，即前端不主动发送请求，完全由后端推送；举个常用例子，比如你的好友发了一条朋友圈，后端将这条消息推送给所有其他好友的客户端
     */
    private String webhook;
}
