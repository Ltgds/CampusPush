package com.ltgds.mypush.limit;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.service.AbstractDeduplicationService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description
 */
public abstract class AbstractLimitService implements LimitService {

    /**
     * 获取得到当前消息模板所有的去重key
     * @param service
     * @param taskInfo
     * @return
     */
    protected List<String> deduplicationAllKey(AbstractDeduplicationService service, TaskInfo taskInfo) {
        List<String> result = new ArrayList<>(taskInfo.getReceiver().size());

        for (String receiver : taskInfo.getReceiver()) {
            //对所有taskInfo中的用户构建 去重key
            String key = deduplicationSingleKey(service, taskInfo, receiver);
            result.add(key);
        }
        return result;
    }

    /**
     * 构建去重Key
     * @param service
     * @param taskInfo
     * @param receiver
     * @return
     */
    protected String deduplicationSingleKey(AbstractDeduplicationService service, TaskInfo taskInfo, String receiver) {
        return service.deduplicationSingleKey(taskInfo, receiver);
    }
}
