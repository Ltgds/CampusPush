package com.ltgds.mypush.domain.push.getui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/14
 * @description 发送消息后的返回值
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendPushResult {

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "data")
    private JSONObject data;
}
