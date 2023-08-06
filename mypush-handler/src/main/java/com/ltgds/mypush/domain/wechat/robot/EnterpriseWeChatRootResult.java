package com.ltgds.mypush.domain.wechat.robot;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description 企业微信 机器人 返回值
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRootResult {

    @JSONField(name = "errcode")
    private Integer errcode;

    @JSONField(name = "errmsg")
    private String errmsg;

    @JSONField(name = "type")
    private String type;

    @JSONField(name = "media_id")
    private String mediaId;

    @JSONField(name = "created_at")
    private String createdAt;
}
