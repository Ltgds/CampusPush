package com.ltgds.mypush.web.service;

import com.ltgds.mypush.domain.ChannelAccount;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description 渠道账号接口
 */
public interface ChannelAccountService {

    /**
     * 保存/修改 渠道账号信息
     * @param channelAccount
     * @return
     */
    ChannelAccount save(ChannelAccount channelAccount);

    /**
     * 根据渠道标识 查询账号信息
     * @param channelType
     * @param creator
     * @return
     */
    List<ChannelAccount> queryByChannelType(Integer channelType, String creator);

    /**
     * 列表信息
     * @param creator
     * @return
     */
    List<ChannelAccount> list(String creator);

    /**
     * 软删除(deleted=1)
     * @param ids
     */
    void deleteByIds(List<Long> ids);
}
