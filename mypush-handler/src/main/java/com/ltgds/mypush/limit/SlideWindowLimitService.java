package com.ltgds.mypush.limit;

import cn.hutool.core.util.IdUtil;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.DeduplicationType;
import com.ltgds.mypush.deduplication.DeduplicationParam;
import com.ltgds.mypush.service.AbstractDeduplicationService;
import com.ltgds.mypush.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description 滑动窗口去重器(内容去重 采用基于redis中zset的滑动窗口去重,可以严格控制单位时间内的频次)
 */
@Service(value = "SlideWindowLimitService")
public class SlideWindowLimitService extends AbstractLimitService{

    private static final String LIMIT_TAG = "SW_";

    @Autowired
    private RedisUtils redisUtils;

    private DefaultRedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }

    /**
     * @param service 去重器对象
     * @param taskInfo
     * @param param 去重参数
     * @return  返回 不符合条件的手机号码
     */
    @Override
    public Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param) {

        Set<String> filterReceiver = new HashSet<>(taskInfo.getReceiver().size());
        long nowTime = System.currentTimeMillis();

        for (String receiver : taskInfo.getReceiver()) {
            //构建key
            String key = LIMIT_TAG + deduplicationSingleKey(service, taskInfo, receiver);
            //生成全局唯一id
            String scoreValue = String.valueOf(IdUtil.getSnowflake().nextId());
            //生成时间戳
            String score = String.valueOf(nowTime);

            /**
             * 使用limit.lua脚本中的滑动窗口算法
             *
             * Zremrangebyscore 用于移除有序集合中,指定分数区间内的所有成员
             *
             * tonumber 将字符串转为数字
             * zadd key score1 member1 向有序集合添加一个或多个成员,或更新已经存在的分数
             * expire 设置key的过期时间,key过期后不再使用,单位为秒
             */
            if (redisUtils.execLimitLua(redisScript, Collections.singletonList(key), //返回包含key的不可变列表
                    String.valueOf(param.getDeduplicationTime() * 1000), //设置的去重时间 ARGV[1]
                    score, //时间戳 作为score ARGV[2]
                    String.valueOf(param.getCountNum()), //阈值 ARGV[3]
                    scoreValue)) { // score对应的唯一value ARGV[4]
                //超过阈值,符合去重条件,放入过滤器中
                filterReceiver.add(receiver);
            }
        }

        return filterReceiver;
    }
}
