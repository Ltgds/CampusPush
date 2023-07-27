package com.ltgds.mypush.deduplication;

import com.ltgds.mypush.build.Builder;
import com.ltgds.mypush.service.DeduplicationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description
 */
@Service
public class DeduplicationHolder {

    /**
     * 将去重类型(deduplicationType)与去重参数构建器 进行映射
     */
    private final Map<Integer, Builder> builderHolder = new HashMap<>(4);
    /**
     * 将去重类型 与 去重服务 进行映射
     */
    private final Map<Integer, DeduplicationService> serviceHolder = new HashMap<>(4);

    public Builder selectBuilder(Integer key) {
        return builderHolder.get(key);
    }

    public DeduplicationService selectService(Integer key) {
        return serviceHolder.get(key);
    }

    public void putBuilder(Integer key, Builder builder) {
        builderHolder.put(key, builder);
    }

    public void putService(Integer key, DeduplicationService service) {
        serviceHolder.put(key, service);
    }

}
