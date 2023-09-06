package com.ltgds.mypush.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Li Guoteng
 * @data 2023/9/6
 * @description 简单埋点信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleAnchorInfo {

    /**
     * 具体点位
     */
    private int state;

    /**
     * 业务id(数据追踪使用)
     * 使用 TaskInfoUtils#generateBusinessId生成
     */
    private Long businessId;

    /**
     * 生成时间
     */
    private long timestamp;
}
