package com.ltgds.mypush.dao;

import com.ltgds.mypush.domain.ChannelAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/7/29
 * @description
 */
public interface ChannelAccountDao extends JpaRepository<ChannelAccount, Long> {

    /**
     * 查询列表
     *
     * @param deleted     0:未删除 1:已删除
     * @param creator     创建者
     * @param channelType 渠道类型
     * @return
     */
    List<ChannelAccount> findAllByIsDeletedEqualsAndCreatedEqualsAndSendChannelEquals(Integer deleted, String creator, Integer channelType);

    /**
     * 查询列表
     * @param deleted 0:未删除 1:已删除
     * @param channelType 渠道类型
     * @return
     */
    List<ChannelAccount> findAllByIsDeletedEqualsAndSendChannelEquals(Integer deleted, Integer channelType);

    /**
     * 根据创建者 搜索相关内容
     * @param creator
     * @return
     */
    List<ChannelAccount> findAllByCreatorEquals(String creator);

    /**
     * 统计未删除条目
     * @param deleted
     * @return
     */
    Long countByIsDeletedEquals(Integer deleted);
}
