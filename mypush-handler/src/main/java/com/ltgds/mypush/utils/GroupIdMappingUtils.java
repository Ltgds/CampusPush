package com.ltgds.mypush.utils;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/23
 * @description 使用GroupId 标识每一个消费者组
 */
public class GroupIdMappingUtils {

    /**
     * 获取所有的groups
     * 不同的渠道,不同的消息类型 拥有自己的groupId
     * @return
     */
    public static List<String> getAllGroups() {
        List<String> groupIds = new ArrayList<>();

        for (ChannelType channelType : ChannelType.values()) {
            for (MessageType messageType : MessageType.values()) {
                groupIds.add(channelType.getCodeEn() + "." + messageType.getCodeEn());
            }
        }
        return groupIds;
    }

    /**
     * 根据TaskInfo获取当前消息的groupId
     * @param taskInfo
     * @return
     */
    public static String getGroupIdByTaskInfo(TaskInfo taskInfo) {
        //通过taskInfo分别找出对应的channel和msg的codeEn,将他们拼起来
        String channelCodeEn = ChannelType.getEnumByCode(taskInfo.getSendChannel()).getCodeEn();
        String msgCodeEn = MessageType.getEnumByCode(taskInfo.getMsgType()).getCodeEn();
        return channelCodeEn + "." + msgCodeEn;
    }

}
