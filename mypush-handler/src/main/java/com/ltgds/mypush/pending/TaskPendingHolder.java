package com.ltgds.mypush.pending;

import com.dtp.core.thread.DtpExecutor;
import com.ltgds.mypush.config.HandlerThreadPoolConfig;
import com.ltgds.mypush.utils.GroupIdMappingUtils;
import com.ltgds.mypush.utils.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Li Guoteng
 * @data 2023/7/25
 * @description 存储 每种消息类型 与 TaskPending的关系
 */
@Component
public class TaskPendingHolder {

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    //将groupId和线程池executor映射起来
    private Map<String, ExecutorService> taskPendingHolder = new HashMap<>(32);

    /**
     * 获取得到所有的groupId
     */
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroups();

    /**
     * 给每个渠道，每种消息类型初始化一个线程池
     */
    @PostConstruct
    public void init() {

        /**
         * 举例：ThreadPoolName: austin.im.notice
         */
        for (String groupId : groupIds) {
            DtpExecutor executor = HandlerThreadPoolConfig.getExecutor(groupId);
            threadPoolUtils.register(executor);

            taskPendingHolder.put(groupId, executor);
        }
    }


    /**
     * 通过groupId得到对应线程池
     * @param groupId
     * @return
     */
    public ExecutorService route(String groupId) {
        return taskPendingHolder.get(groupId);
    }

}
