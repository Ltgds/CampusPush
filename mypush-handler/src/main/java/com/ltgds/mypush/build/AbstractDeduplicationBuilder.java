package com.ltgds.mypush.build;


import com.alibaba.fastjson.JSONObject;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.deduplication.DeduplicationHolder;
import com.ltgds.mypush.deduplication.DeduplicationParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description  抽象 去重参数 构建器
 */
public abstract class AbstractDeduplicationBuilder implements Builder{

    protected Integer deduplicationType;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    /**
     * 将去重类型 与 对应去重参数构建器 对应起来
     */
    @PostConstruct
    public void init() {
        deduplicationHolder.putBuilder(deduplicationType, this);
    }

    /**
     * 从配置中 获取去重服务参数
     * @param key
     * @param duplicationConfig
     * @param taskInfo
     * @return
     */
    public DeduplicationParam getParamsFormConfig(Integer key, String duplicationConfig, TaskInfo taskInfo) {
        JSONObject object = JSONObject.parseObject(duplicationConfig);
        if (Objects.isNull(object)) {
            return null;
        }

        DeduplicationParam deduplicationParam = JSONObject.parseObject(object.getString(DEDUPLICATION_CONFIG_PRE + key), DeduplicationParam.class);

        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setTaskInfo(taskInfo);
        return deduplicationParam;
    }

}
