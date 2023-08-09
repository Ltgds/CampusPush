package com.ltgds.mypush.flowcontrol;

import com.ltgds.mypush.common.domain.TaskInfo;

/**
 * @author Li Guoteng
 * @data 2023/8/9
 * @description 流量控制服务
 */
public interface FlowControlService {

    /**
     * 根据渠道进行流量控制
     * @param taskInfo
     * @param flowControlParam
     * @return 耗费的时间
     */
    Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam);
}
