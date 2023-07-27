package com.ltgds.mypush.utils;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 对Redis操作进行二次封装
 */
@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * mGet 将结果封装为Map
     * @param keys
     * @return
     */
    public Map<String, String> mGet(List<String> keys) {
        HashMap<String, String> result = new HashMap<>(keys.size());

        try {
            List<String> value = redisTemplate.opsForValue().multiGet(keys); //批量获取值
            if (CollUtil.isNotEmpty(value)) {
                for (int i = 0; i < keys.size(); i++) {
                    result.put(keys.get(i), value.get(i));
                }
            }
        } catch (Exception e) {
            log.error("RedisUtils#mGet fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return result;
    }

    /**
     * hGetAll
     * -- 获取变量中的键值对
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            return entries;
        } catch (Exception e) {
            log.error("RedisUtils#hGetAll fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }

    /**
     * lRange
     * -- 获取列表指定范围内的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lRange(String key, long start, long end) {
        try {
            //获取列表指定范围内的元素
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtils#lRange fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return null;
    }

    /**
     * pipeline 设置 key-value 并设置过期时间
     * @param keyValues
     * @param seconds
     */
    public void pipelineSetEx(Map<String, String> keyValues, Long seconds) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    connection.setEx(entry.getKey().getBytes(), seconds, entry.getValue().getBytes());
                }
                return null;
            });
        } catch (Exception e) {
            log.error("RedisUtils#pipelineSetEx fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * lpush方法 并指定过期时间
     * @param key
     * @param value
     * @param seconds
     */
    public void lPush(String key, String value, Long seconds) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                //存储在list头部,添加时放在最前面索引处
                connection.lPush(key.getBytes(), value.getBytes());
                //设置过期时间（密钥,日期）
                connection.expire(key.getBytes(), seconds);
                return null;
            });
        } catch (Exception e) {
            log.error("RedisUtils#lPush fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * lLen方法
     * -- 获取当前key的List列表长度
     * @param key
     * @return
     */
    public Long lLen(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("RedisUtils#lLen fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return 0L;
    }

    /**
     * lPop 方法
     * -- 移出并获取列表中第一个元素
     */
    public String lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("RedisUtils#pipelineSetEx fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return "";
    }

    /**
     * pipeline 设置 key-value 并设置过期时间
     *
     * @param seconds 过期时间
     * @param delta   自增的步长
     */
    public void pipelineHashIncrByEx(Map<String, String> keyValues, Long seconds, Long delta) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    connection.hIncrBy(entry.getKey().getBytes(), entry.getValue().getBytes(), delta);
                    connection.expire(entry.getKey().getBytes(), seconds);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("redis pipelineSetEX fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 执行Lua脚本并返回执行结果
     * --KEYS[1]：限流 key
     * --ARGV[1]：限流窗口
     * --ARGV[2]：当前时间戳（作为score）
     * --ARGV[3]：阈值
     * --ARGV[4]：score对应的唯一value
     * @param redisScript
     * @param keys
     * @param args
     * @return
     */
    public Boolean execLimitLua(RedisScript<Long> redisScript, List<String> keys, String... args) {

        try {
            //超过阈值返回1, 未超过阈值返回0
            Long execute = redisTemplate.execute(redisScript, keys, args);
            if (Objects.isNull(execute)) {
                return false;
            }
            //超过阈值,则值为1
            return CommonConstant.TRUE.equals(execute.intValue());
        } catch (Exception e) {
            log.error("redis execLimitLua fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return false;
    }
}
