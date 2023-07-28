package com.ltgds.mypush.receiver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ltgds.mypush.common.domain.AnchorInfo;
import com.ltgds.mypush.common.domain.LogParam;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.AnchorState;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.pending.Task;
import com.ltgds.mypush.pending.TaskPendingHolder;
import com.ltgds.mypush.receiver.service.ConsumeService;
import com.ltgds.mypush.utils.GroupIdMappingUtils;
import com.ltgds.mypush.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/25
 * @description
 */
@Service
public class ConsumeServiceImpl implements ConsumeService {

    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TaskPendingHolder taskPendingHolder;

    @Autowired
    private LogUtils logUtils;

    /**
     * 从 MQ 拉取消息进行消费,发送消息
     * @param taskInfoLists
     */
    @Override
    public void consume2Send(List<TaskInfo> taskInfoLists) {
        String topicGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));

        for (TaskInfo taskInfo : taskInfoLists) {
            Task task = context.getBean(Task.class).setTaskInfo(taskInfo);

            //
            logUtils.print(
                    LogParam.builder()  //当前对象信息
                            .bizType(LOG_BIZ_TYPE) //标志日志的业务
                            .object(taskInfo)
                    .build(),
                    AnchorInfo.builder() //埋点信息
                            .businessId(taskInfo.getBusinessId())
                            .ids(taskInfo.getReceiver())
                            .state(AnchorState.RECEIVE.getCode()) //消息接收成功
                    .build()
            );

            //通过topicGroupId获取到对应线程池,并执行task任务
            taskPendingHolder.route(topicGroupId).execute(task);
        }

    }

    /**
     * 从 MQ 拉取消息进行消费,撤回消息
     * @param messageTemplate
     */
    @Override
    public void consume2recall(MessageTemplate messageTemplate) {
        logUtils.print(
                LogParam.builder()
                        .bizType(LOG_BIZ_RECALL_TYPE)
                        .object(messageTemplate)
                        .build()
        );
    }
}
