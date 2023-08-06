package com.ltgds.mypush.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description 小程序
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniProgramContentModel extends ContentModel{

    /**
     * 模板消息发送的数据
     */
    Map<String, String> map;
}
