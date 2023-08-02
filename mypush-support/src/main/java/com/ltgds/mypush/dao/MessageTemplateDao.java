package com.ltgds.mypush.dao;

import com.ltgds.mypush.domain.MessageTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/5/29
 * @description 消息模板Dao
 */
public interface MessageTemplateDao extends JpaRepository<MessageTemplate, Long>, JpaSpecificationExecutor<MessageTemplate> {

    /**
     * 查询 列表分页
     *
     * @param deleted  0,未删除 1,已删除
     * @param pageable 分页对象
     * @return
     */
    List<MessageTemplate> findAllByIsDeletedEqualsOrderByUpdatedDesc(Integer deleted, Pageable pageable);

    /**
     * 统计未删除的条数
     *
     * @param deleted
     * @return
     */
    Long countByIsDeletedEquals(Integer deleted);

}
