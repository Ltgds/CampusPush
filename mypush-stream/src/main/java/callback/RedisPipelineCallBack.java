package callback;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/9/6
 * @description pipeline接口定义
 */
public interface RedisPipelineCallBack {

    List<RedisFuture<?>> invoke(RedisAsyncCommands redisAsyncCommands);
}
