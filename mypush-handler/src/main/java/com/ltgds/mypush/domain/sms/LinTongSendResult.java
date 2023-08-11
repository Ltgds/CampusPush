package com.ltgds.mypush.domain.sms;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinTongSendResult {

    Integer code;

    String message;

    @JSONField(name = "data")
    List<DataDTO> dtoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class DataDTO {
        Integer code;
        String message;
        Long msgId;
        String phone;
    }
}
