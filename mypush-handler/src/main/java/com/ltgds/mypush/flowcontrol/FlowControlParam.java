package com.ltgds.mypush.flowcontrol;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.RateLimiter;
import com.ltgds.mypush.enums.RateLimitStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/9
 * @description 流量控制所需要的参数
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowControlParam {

    /**
     * 限流器
     * 子类初始化时指定
     */
    protected RateLimiter rateLimiter;

    /**
     * 限流器初始限流大小
     * 子类初始化时指定
     */
    protected Double rateInitValue;

    /**
     * 限流的策略
     * 子类初始化时指定
     */
    protected RateLimitStrategy rateLimitStrategy;
}
