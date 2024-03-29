package com.ltgds.mypush.service;

import cn.hutool.core.collection.CollUtil;
import com.ltgds.mypush.common.domain.AnchorInfo;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.deduplication.DeduplicationHolder;
import com.ltgds.mypush.deduplication.DeduplicationParam;
import com.ltgds.mypush.limit.LimitService;
import com.ltgds.mypush.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 抽象 去重服务
 */
@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService {

    protected Integer deduplicationType;

    protected LimitService limitService; //去重限制

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    /**
     * 将 去重类型 与 去重服务 进行映射
     */
    @PostConstruct
    private void init() {
        deduplicationHolder.putService(deduplicationType, this);
    }

    @Autowired
    private LogUtils logUtils; //数据链路追踪

    /**
     * 去重
     * @param param
     */
    @Override
    public void deduplication(DeduplicationParam param) {
        TaskInfo taskInfo = param.getTaskInfo();

        //去重限制,筛选符合去重条件的用户
        Set<String> filterReceiver = limitService.limitFilter(this, taskInfo, param);

        //剔除符合去重条件的用户
        if (CollUtil.isNotEmpty(filterReceiver)) {
            taskInfo.getReceiver().removeAll(filterReceiver); //删除taskInfo中符合去重条件的用户

            //进行数据链路追踪-- 记录打点信息
            logUtils.print(AnchorInfo.builder()
                    .businessId(taskInfo.getBusinessId()) //业务id
                            .ids(filterReceiver) //去重对象
                            .state(param.getAnchorState().getCode()) //数据埋点类型
                    .build());
        }
    }

    /**
     * 构建 去重的key
     * @param taskInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(TaskInfo taskInfo, String receiver);
}
