package com.ltgds.mypush.pipeline;

import com.ltgds.mypush.common.vo.BasicResultVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Li Guoteng
 * @data 2023/6/3
 * @description 责任链上下文
 *
 * 执行过程中的上下文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ProcessContext<T extends ProcessModel> {

    /**
     * 标识责任链的code
     */
    private String code;

    /**
     * 存储责任链上下文的数据模型
     */
    private T processModel;

    /**
     * 责任链中断标识
     */
    private Boolean needBreak;

    /**
     * 流程处理结果
     */
    BasicResultVO response;
}
