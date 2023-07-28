package com.ltgds.mypush.discard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.domain.AnchorInfo;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.AnchorState;
import com.ltgds.mypush.service.ConfigService;
import com.ltgds.mypush.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description
 */
@Service
public class DiscardMessageService {

    private static final String DISCARD_MESSAGE_KEY = "discardMsgIds";

    @Autowired
    private ConfigService configService;

    @Autowired
    private LogUtils logUtils;

    /**
     * 丢弃消息,配置在apollo
     * @param taskInfo
     * @return
     */
    public boolean isDiscard(TaskInfo taskInfo) {
        //配置示例: ["1", "2"]
        JSONArray array = JSON.parseArray(configService.getProperty(DISCARD_MESSAGE_KEY, CommonConstant.EMPTY_JSON_OBJECT));

        if (array.contains(String.valueOf(taskInfo.getMessageTemplateId()))) {
            //进行链路追踪 打点
            logUtils.print(AnchorInfo.builder()
                            .businessId(taskInfo.getBusinessId())
                            .ids(taskInfo.getReceiver())
                            .state(AnchorState.DISCARD.getCode())
                    .build());
            return true;
        }
        return false;
    }
}
