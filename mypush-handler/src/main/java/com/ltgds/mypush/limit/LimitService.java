package com.ltgds.mypush.limit;

import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.deduplication.DeduplicationParam;
import com.ltgds.mypush.service.AbstractDeduplicationService;

import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 去重限制
 */
public interface LimitService {

    /**
     * 去重限制, 筛选符合去重限制的对象
     * @param service 去重器对象
     * @param taskInfo
     * @param param 去重参数
     * @return 返回不符合条件的手机号码
     */
    Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param);
}