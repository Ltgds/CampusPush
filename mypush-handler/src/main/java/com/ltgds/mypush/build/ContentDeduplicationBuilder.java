package com.ltgds.mypush.build;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.AnchorState;
import com.ltgds.mypush.common.enums.DeduplicationType;
import com.ltgds.mypush.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 内容去重 参数构建器
 */
@Service
public class ContentDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder{

    public ContentDeduplicationBuilder() {
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    /**
     * 构建 内容去重 所需要的参数
     * @param deduplication
     * @param taskInfo
     * @return
     */
    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFormConfig(deduplicationType, deduplication, taskInfo);
        if (Objects.isNull(deduplicationParam)) {
            return null;
        }
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION); //打点
        return deduplicationParam;
    }
}
