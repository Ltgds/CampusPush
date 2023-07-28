package com.ltgds.mypush.utils;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.CustomLogListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Li Guoteng
 * @data 2023/7/28
 * @description
 */
@Slf4j
@Component
public class LogUtils extends CustomLogListener {

    /**
     * 方法切面的日志 @OperationLog 所产生
     * @param logDTO
     * @throws Exception
     */
    @Override
    public void createLog(LogDTO logDTO) throws Exception {
        log.info(JSON.toJSONString(logDTO));
    }
}
