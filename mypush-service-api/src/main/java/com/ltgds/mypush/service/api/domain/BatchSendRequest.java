package com.ltgds.mypush.service.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 发送接口的参数
 *          必传
 */
@Data
@Accessors(chain = true) //在生成getter和setter时,对应的setter方法调用后,会返回当前对象
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchSendRequest {

    /**
     * 执行业务类型
     *  参考BusinessCode
     */
    private String code;

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 消息相关参数
     */
    private List<MessageParam> messageParamList;

}
