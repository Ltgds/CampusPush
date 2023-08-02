package com.ltgds.mypush.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import com.ltgds.mypush.web.service.ChannelAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description
 */
@Service
public class ChannelAccountServiceImpl implements ChannelAccountService {

    @Autowired
    private ChannelAccountDao channelAccountDao;

    /**
     * 保存/修改 渠道账号信息
     * @param channelAccount
     * @return
     */
    @Override
    public ChannelAccount save(ChannelAccount channelAccount) {
        //若不存在,则创建
        if (Objects.isNull(channelAccount.getId())) {
            channelAccount.setCreated(Math.toIntExact(DateUtil.currentSeconds())); //创建时间
            channelAccount.setUpdated(CommonConstant.FALSE);
        }

        //若存在,则更新
        channelAccount.setCreator(StrUtil.isBlank(channelAccount.getCreator()) ? PushConstant.DEFAULT_CREATOR : channelAccount.getCreator());
        channelAccount.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        return channelAccountDao.save(channelAccount);
    }

    /**
     * 根据渠道标识 查询账号信息
     * @param channelType
     * @param creator
     * @return
     */
    @Override
    public List<ChannelAccount> queryByChannelType(Integer channelType, String creator) {
        return channelAccountDao.findAllByIsDeletedEqualsAndCreatedEqualsAndSendChannelEquals(CommonConstant.FALSE, creator, channelType);
    }

    /**
     * 列表信息
     * @param creator
     * @return
     */
    @Override
    public List<ChannelAccount> list(String creator) {
        return channelAccountDao.findAllByCreatorEquals(creator);
    }

    /**
     * 软删除(deleted=1)
     * @param ids
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        channelAccountDao.deleteAllById(ids);
    }
}
