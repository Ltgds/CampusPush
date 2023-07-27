package com.ltgds.mypush.service;

import com.ltgds.mypush.deduplication.DeduplicationParam;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 去重服务
 */
public interface DeduplicationService {

    /**
     * 去重
     * @param param
     */
    void deduplication(DeduplicationParam param);
}
