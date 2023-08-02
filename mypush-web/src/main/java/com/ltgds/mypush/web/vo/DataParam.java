package com.ltgds.mypush.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/7/31
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataParam {

    /**
     * 查看用户的链路信息
     */
    private String receiver;

    /**
     * 业务id(用于追踪数据)
     * 生成逻辑: TaskInfoUtils
     * 如果传入的是模板id,则生成当天的业务id
     */
    private String businessId;

    /**
     * 日期时间(检索短信的条件使用)
     */
    private Long dateTime;
}
