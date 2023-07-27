package com.ltgds.mypush.deduplication;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.DeduplicationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 去重服务
 */
@Service
public class DeduplicationRuleService {

    public static final String DEDUPLICATION_RULE_KEY = "deduplicationRule";

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    public void duplication(TaskInfo taskInfo) {

        /**
         * 配置样例:
         * {"deduplication_10":{"num":1,"time":300},"deduplication_20":{"num":5}}
         * 可通过分布式配置中心来更新配置
         */
        String deduplicationConfig = "{\"deduplication_10\":{\"num\":1,\"time\":300},\"deduplication_20\":{\"num\":5}}";

        //获取去重渠道列表 10:相同内容去重 20:频次去重
        List<Integer> deduplicationList = DeduplicationType.getDeduplicationList();

        for (Integer deduplicationType : deduplicationList) {
            //通过去重渠道选择对应的去重参数构建器
            //通过配置 构建去重所需要的参数
            DeduplicationParam deduplicationParam = deduplicationHolder.selectBuilder(deduplicationType).build(deduplicationConfig, taskInfo);
            if (Objects.nonNull(deduplicationParam)) {
                //通过去重渠道选择对应的去重服务
                //通过去重参数 构建合适的key, 并进行去重
                deduplicationHolder.selectService(deduplicationType).deduplication(deduplicationParam);
            }
        }

        /**
         * 1. 构建去重参数param(根据去重渠道,选择对应的参数构建器(Builder))
         * 2. 构建key(根据param选择对应的Service)
         * 3. 判断receiver是否满足 去重条件(LimitService),若满足则加入filterReceiver中
         * 4. 删除taskInfo中满足去重条件的用户
         */
    }

}
