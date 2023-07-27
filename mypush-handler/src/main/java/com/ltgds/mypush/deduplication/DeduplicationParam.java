package com.ltgds.mypush.deduplication;

import com.alibaba.fastjson.annotation.JSONField;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.AnchorState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 去重服务所需要的参数
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeduplicationParam {

    /**
     * TaskInfo信息
     */
    private TaskInfo taskInfo;

    /**
     * 去重时间
     * 单位：秒
     */
    @JSONField(name = "time")
    private Long deduplicationTime;

    /**
     * 需要达到的次数去重
     */
    @JSONField(name = "num")
    private Integer countNum;

    /**
     * 标识数据哪种去重(数据埋点)
     */
    private AnchorState anchorState;

}
