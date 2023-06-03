package com.ltgds.mypush.service.api.impl.domain;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.pipeline.ProcessModel;
import com.ltgds.mypush.service.api.domain.MessageParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/6/4
 * @description 发送消息任务模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaskModel implements ProcessModel {

    /**
     * 消息模板id
     */
    private Long messageTemplateId;

    /**
     * 请求参数
     */
    private List<MessageParam> messageParamList;

    /**
     * 发送任务的信息
     */
    private List<TaskInfo> taskInfo;

    /**
     * 撤回任务的信息
     */
    private MessageTemplate messageTemplate;
}
