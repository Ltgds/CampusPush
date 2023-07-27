package com.ltgds.mypush.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description channel 和 Handler 的映射关系
 */
@Component
public class HandlerHolder {

    private Map<Integer, Handler> handlers = new HashMap<>(128);

    public void putHandler(Integer channelCode, Handler handler) {
        handlers.put(channelCode, handler);
    }

    public Handler route(Integer channelCode) {
        return handlers.get(channelCode);
    }
}
