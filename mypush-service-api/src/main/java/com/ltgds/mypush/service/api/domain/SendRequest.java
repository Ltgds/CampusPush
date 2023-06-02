package com.ltgds.mypush.service.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 发送/撤回接口的参数
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendRequest {

    /**
     * 执行业务类型
     * send:发送
     * recall:撤回
     */
    private String code;

    /**
     * 消息模板id
     * 必填
     */
    private Long messageTemplateId;

    /**
     * 消息相关参数
     * 当业务类型为send时,必传
     */
    private MessageParam messageParam;

}
