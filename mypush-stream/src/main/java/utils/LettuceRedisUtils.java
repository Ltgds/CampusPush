package utils;

import callback.RedisPipelineCallBack;
import constants.AustinFlinkConstant;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import org.springframework.data.redis.connection.RedisPipelineException;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Li Guoteng
 * @data 2023/9/6
 * @description 无spring环境下使用Redis,基于Lettuce封装
 */
public class LettuceRedisUtils {

    /**
     * 初始化 redisClient
     */
    private static RedisClient redisClient;

    static {
        RedisURI redisURI = RedisURI.builder()
                .redis(AustinFlinkConstant.REDIS_IP)
                .withPort(Integer.valueOf(AustinFlinkConstant.REDIS_PORT))
                .withPassword(AustinFlinkConstant.REDIS_PASSWORD.toCharArray())
                .build();

        redisClient = RedisClient.create(redisURI);
    }

    /**
     * 封装pipeline操作
     */
    public static void pipeline(RedisPipelineCallBack pipelineCallBack) {
        StatefulRedisConnection<byte[], byte[]> connect = redisClient.connect(new ByteArrayCodec());
        RedisAsyncCommands<byte[], byte[]> commands = connect.async();

        List<RedisFuture<?>> futures = pipelineCallBack.invoke(commands);

        commands.flushCommands();
        LettuceFutures.awaitAll(10, TimeUnit.SECONDS, futures.toArray(new RedisFuture[futures.size()]));
        connect.close();
    }
}
