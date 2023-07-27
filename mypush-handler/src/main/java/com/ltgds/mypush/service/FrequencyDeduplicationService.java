package com.ltgds.mypush.service;

import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.build.FrequencyDeduplicationBuilder;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.DeduplicationType;
import com.ltgds.mypush.limit.LimitService;
import io.lettuce.core.Limit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Li Guoteng
 * @data 2023/7/27
 * @description 频次去重服务
 */
public class FrequencyDeduplicationService extends AbstractDeduplicationService{

    @Autowired
    public FrequencyDeduplicationService(@Qualifier("SimpleLimitService")LimitService limitService) {
        this.limitService = limitService;
        deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }

    private static final String PREFIX = "FRE";

    /**
     * 频次去重 构建key
     *
     * key: receiver + templateId + sendChannel
     *
     * 一天内 一个用户只能收到某个渠道的消息N次
     * @param taskInfo
     * @param receiver
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return PREFIX + StrUtil.C_UNDERLINE
                + receiver + StrUtil.C_UNDERLINE
                + taskInfo.getMessageTemplateId() + StrUtil.C_UNDERLINE
                + taskInfo.getSendChannel();
    }
}
