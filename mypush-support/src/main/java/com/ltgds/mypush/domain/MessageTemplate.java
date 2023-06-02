package com.ltgds.mypush.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Li Guoteng
 * @data 2023/5/29
 * @description 消息模板
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity //定义的对象会成为被JPA管理的实体,将映射指定到数据库表
public class MessageTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //唯一标识主键,由数据库生成,自增长
    private Long id;

    /**
     * 模板标题
     */
    private String name;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 工单ID（模板审核走工单）
     */
    private String flowId;

    /**
     * 消息状态
     */
    private Integer msgStatus;

    /**
     * 定时任务Id,由xxl-job返回
     */
    private Integer cronTaskId;

    /**
     * 定时发送的人群的文件路径
     */
    private String cronCrowdPath;

    /**
     * 发送的Id类型
     */
    private Integer idType;

    /**
     * 发送渠道
     */
    private Integer sendChannel;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 屏蔽类型
     */
    private Integer shieldType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 推送消息的时间
     * 0:立即发送
     * else: crontab表达式
     */
    private String expectPushTime;

    /**
     * 消息内容 {$var}为占位符
     */
    private String msgContent;

    /**
     * 发送账号
     * 邮件下有多个发送账号、短信可有多个发送账号
     */
    private Integer sendAccount;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 修改者
     */
    private String updator;

    /**
     * 审核者
     */
    private String auditor;

    /**
     * 业务方团队
     */
    private String team;

    /**
     * 业务方
     */
    private String proposer;

    /**
     * 是否删除
     * 0: 未删除
     * 1: 已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间 单位s
     */
    private Integer created;

    /**
     * 更新时间 单位s
     */
    private Integer updated;

}
