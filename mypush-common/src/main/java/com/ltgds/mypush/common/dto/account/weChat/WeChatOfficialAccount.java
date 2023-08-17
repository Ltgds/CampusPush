package com.ltgds.mypush.common.dto.account.weChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/16
 * @description 模板消息参数
 *
 * 参数示例：
 * https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatOfficialAccount {

    /**
     * 账号相关
     */
    private String appId;
    private String secret;
    private String token;
}
