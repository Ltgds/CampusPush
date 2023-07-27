package com.ltgds.mypush.limit;

import cn.hutool.core.collection.CollUtil;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.deduplication.DeduplicationParam;
import com.ltgds.mypush.service.AbstractDeduplicationService;
import com.ltgds.mypush.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 采用普通的计数去重方法,限制每天发送的条数
 */
@Service(value = "SimpleLimitService")
public class SimpleLimitService extends AbstractLimitService{

    private static final String LIMIT_TAG = "SP_";

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {
        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());

        //获取redis记录, 存储receiver和对应的key
        Map<String, String> readyPutRedisReceiver = new HashMap<>(taskInfo.getReceiver().size());
        //redis数据隔离,给所有去重key前 + SP_
        List<String> keys = deduplicationAllKey(service, taskInfo).stream()
                .map(key -> LIMIT_TAG + key)
                .collect(Collectors.toList());
        //将keys封装成Map---封装key和重复的次数
        Map<String, String> inRedisValue = redisUtils.mGet(keys);

        //开始判断 是否符合去重限制
        for (String receiver : taskInfo.getReceiver()) {
            //拼装key
            String key = LIMIT_TAG + deduplicationSingleKey(service, taskInfo, receiver);
            String value = inRedisValue.get(key); //重复次数

            //符合条件的用户: value != null && 重复的次数 >= 需要达到的去重次数
            if (Objects.nonNull(value) && Integer.parseInt(value) >= param.getCountNum()) {
                filterReceiver.add(receiver); //将符合去重条件的receiver,放入filterReceiver中
            } else {
                //若不需要去重,就存储receiver对应的key
                readyPutRedisReceiver.put(receiver, key);
            }
        }

        //不符合去重条件的用户: 需要更新Redis(无记录则添加; 有记录则累加次数)
        putInRedis(readyPutRedisReceiver, inRedisValue, param.getDeduplicationTime());

        return null;
    }

    /**
     * 存入redis 实现去重
     * @param readyPutRedisReceiver 不需要去重的用户,存入redis中
     * @param inRedisValue  存储key以及对应的次数
     * @param deduplicationTime 去重时间
     */
    private void putInRedis(Map<String, String> readyPutRedisReceiver,
                            Map<String, String> inRedisValue, Long deduplicationTime) {
        //keyValues 存储key和对应的次数
        Map<String, String> keyValues = new HashMap<>(readyPutRedisReceiver.size());

        for (Map.Entry<String, String> entry : readyPutRedisReceiver.entrySet()) {
            String key = entry.getValue();
            //若redis中已经有key的记录,则key对应的value次数进行累加
            if (Objects.nonNull(inRedisValue.get(key))) {
                //重复次数 +1
                keyValues.put(key, String.valueOf(Integer.parseInt(inRedisValue.get(key)) + 1));
            } else {
                //若redis中没有key的记录,则设置value的次数为1
                keyValues.put(key, String.valueOf(CommonConstant.TRUE));
            }
        }

        //设置过期时间
        if (CollUtil.isNotEmpty(keyValues)) {
            redisUtils.pipelineSetEx(keyValues, deduplicationTime);
        }
    }
}
