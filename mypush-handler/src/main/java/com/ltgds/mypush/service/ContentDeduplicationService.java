package com.ltgds.mypush.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.enums.DeduplicationType;
import com.ltgds.mypush.limit.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Li Guoteng
 * @data 2023/7/26
 * @description 内容去重服务(默认5分钟 相同的文案发给相同的用户,则去重)
 */
@Service
public class ContentDeduplicationService extends AbstractDeduplicationService{

    @Autowired
    public ContentDeduplicationService(@Qualifier("SlideWindowLimitService")LimitService limitService) {
        this.limitService = limitService;
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    /**
     * 内容去重 构建key
     *
     * key: md5(templateId + receiver + content)
     *
     * 相同的内容 相同的模板 短时间内发给同一个人
     * @param taskInfo
     * @param receiver
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return DigestUtil.md5Hex(taskInfo.getMessageTemplateId() + receiver
        + JSON.toJSONString(taskInfo.getContentModel()));
    }
}
