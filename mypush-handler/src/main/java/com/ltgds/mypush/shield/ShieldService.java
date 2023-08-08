package com.ltgds.mypush.shield;

import com.ltgds.mypush.common.domain.TaskInfo;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description 屏蔽服务
 */
public interface ShieldService {

    /**
     * 屏蔽消息
     * @param taskInfo
     */
    void shield(TaskInfo taskInfo);
}
