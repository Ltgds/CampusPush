package com.ltgds.mypush.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.common.enums.AuditStatus;
import com.ltgds.mypush.common.enums.MessageStatus;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.enums.TemplateType;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.dao.MessageTemplateDao;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.web.service.MessageTemplateService;
import com.ltgds.mypush.web.vo.MessageTemplateParam;
import entity.XxlJobInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import service.CronTaskService;
import utils.XxlJobUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author Li Guoteng
 * @data 2023/8/1
 * @description
 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Resource
    private CronTaskService cronTaskService;

    @Resource
    private XxlJobUtils xxlJobUtils;



    /**
     * 查询未删除的模板列表(分页)
     * @param param
     * @return
     */
    @Override
    public Page<MessageTemplate> queryList(MessageTemplateParam param) {
        //分页请求
        PageRequest pageRequest = PageRequest.of(param.getPage() - 1, param.getPerPage());

        String creator = StrUtil.isBlank(param.getCreator()) ? PushConstant.DEFAULT_CREATOR : param.getCreator();

        return messageTemplateDao.findAll((Specification<MessageTemplate>) (root, query, cb) -> {
            ArrayList<Predicate> predicateList = new ArrayList<>();
            //加搜索条件
            if (StrUtil.isNotBlank(param.getKeywords())) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + param.getKeywords() + "%"));
            }

            predicateList.add(cb.equal(root.get("isDeleted").as(Integer.class), CommonConstant.FALSE));
            predicateList.add(cb.equal(root.get("creator").as(String.class), creator));

            Predicate[] p = new Predicate[predicateList.size()];

            //查询
            query.where(cb.and(predicateList.toArray(p)));
            //排序
            query.orderBy(cb.desc(root.get("updated")));
            return query.getRestriction();
        }, pageRequest);
    }

    /**
     * 统计未删除的条数
     * @return
     */
    @Override
    public Long count() {
        return messageTemplateDao.countByIsDeletedEquals(CommonConstant.FALSE);
    }

    /**
     * 单个保存或更新 存在ID 更新 不存在ID 保存
     * @param messageTemplate
     * @return
     */
    @Override
    public MessageTemplate saveOrUpdate(MessageTemplate messageTemplate) {
        if (Objects.isNull(messageTemplate.getId())) {
            initStatus(messageTemplate); //若模板不存在,则初始化
        } else {
            resetStatus(messageTemplate); //若模板存在,则更新
        }

        messageTemplate.setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
        return messageTemplateDao.save(messageTemplate);
    }

    /**
     * 1.重置模板的状态
     * 2.修改定时任务信息(如果存在)
     * @param messageTemplate
     */
    private void resetStatus(MessageTemplate messageTemplate) {
        messageTemplate.setUpdator(messageTemplate.getUpdator())
                .setMsgStatus(MessageStatus.INIT.getCode())
                .setAuditStatus(AuditStatus.WAIT_AUDIT.getCode());

        //从数据库查询并注入 定时任务id
        MessageTemplate dbMsg = queryById(messageTemplate.getId());
        if (Objects.nonNull(dbMsg) && Objects.nonNull(dbMsg.getCronCrowdPath())) {
            messageTemplate.setCronTaskId(dbMsg.getCronTaskId());
        }

        if (Objects.nonNull(messageTemplate.getCronTaskId()) && TemplateType.CLOCKING.getCode().equals(messageTemplate.getTemplateType())) {

        }
    }

    /**
     * 初始化状态信息
     * @param messageTemplate
     */
    private void initStatus(MessageTemplate messageTemplate) {
        messageTemplate.setFlowId(StrUtil.EMPTY)
                .setMsgStatus(MessageStatus.INIT.getCode()) //初始化状态
                .setAuditStatus(AuditStatus.WAIT_AUDIT.getCode()) //等待审核
                .setCreator(StrUtil.isBlank(messageTemplate.getCreator()) ? PushConstant.DEFAULT_CREATOR : messageTemplate.getCreator())
                .setUpdator(StrUtil.isBlank(messageTemplate.getUpdator()) ? PushConstant.DEFAULT_UPDATOR : messageTemplate.getUpdator())
                .setTeam(StrUtil.isBlank(messageTemplate.getTeam()) ? PushConstant.DEFAULT_TEAM : messageTemplate.getTeam())
                .setAuditor(StrUtil.isBlank(messageTemplate.getAuditor()) ? PushConstant.DEFAULT_AUDITOR : messageTemplate.getAuditor())
                .setCreated(Math.toIntExact(DateUtil.currentSeconds()))
                .setIsDeleted(CommonConstant.FALSE);

        //初始化定时任务相关信息
        XxlJobInfo xxlJobInfo = xxlJobUtils.buildXxlJobInfo(messageTemplate);
        cronTaskService.saveCronTask(xxlJobInfo);
        cronTaskService.stopCronTask(messageTemplate.getCronTaskId());
    }

    /**
     * 软删除
     * @param ids
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        List<MessageTemplate> messageTemplates = messageTemplateDao.findAllById(ids); //通过id拿到所有模板
        messageTemplates.forEach(messageTemplate -> messageTemplate.setIsDeleted(CommonConstant.TRUE)); //所有isDeleted = 1

        //删除定时任务
        for (MessageTemplate messageTemplate : messageTemplates) {
            if (Objects.nonNull(messageTemplate.getCronTaskId()) && messageTemplate.getCronTaskId() > 0) {
                cronTaskService.deleteCronTask(messageTemplate.getCronTaskId());
            }
        }
        messageTemplateDao.saveAll(messageTemplates);
    }

    /**
     * 根据id查询模板消息
     * @param id
     * @return
     */
    @Override
    public MessageTemplate queryById(Long id) {
        return messageTemplateDao.findById(id).orElse(null);
    }

    /**
     * 复制模板
     * @param id
     */
    @Override
    public void copy(Long id) {
        MessageTemplate messageTemplate = queryById(id); //根据id查询模板

        if (Objects.nonNull(messageTemplate)) {
            MessageTemplate clone = ObjectUtil.clone(messageTemplate).setId(null).setCronTaskId(null); //克隆
            messageTemplateDao.save(clone);
        }
    }

    /**
     * 启动模板定时任务
     * @param id
     * @return
     */
    @Override
    public BasicResultVO startCronTask(Long id) {
        //1.获取消息模板的信息
        MessageTemplate messageTemplate = messageTemplateDao.findById(id).orElse(null);
        if (Objects.isNull(messageTemplate)) {
            return BasicResultVO.fail();
        }

        //2.动态创建或更新定时任务
        XxlJobInfo xxlJobInfo = xxlJobUtils.buildXxlJobInfo(messageTemplate);

        //3.获取taskId(如果本身有则复用,若不存在则得到新建后任务Id)
        Integer taskId = messageTemplate.getCronTaskId();
        BasicResultVO basicResultVO = cronTaskService.saveCronTask(xxlJobInfo);

        if (Objects.isNull(taskId) && RespStatusEnum.SUCCESS.getCode().equals(basicResultVO.getStatus())
                            && Objects.nonNull(basicResultVO.getData())) {
            taskId = Integer.valueOf(String.valueOf(basicResultVO.getData()));
        }

        //4.启动定时任务
        if (Objects.nonNull(taskId)) {

            cronTaskService.startCronTask(taskId);

            MessageTemplate clone = ObjectUtil.clone(messageTemplate)
                    .setMsgStatus(MessageStatus.RUN.getCode())
                    .setCronTaskId(taskId)
                    .setUpdated(Math.toIntExact(DateUtil.currentSeconds()));
            messageTemplateDao.save(clone);

            return BasicResultVO.success();
        }
        return BasicResultVO.fail();
    }

    /**
     * 暂停模板的定时任务
     * @param id
     * @return
     */
    @Override
    public BasicResultVO stopCronTask(Long id) {
        //1.修改模板状态
        MessageTemplate messageTemplate = messageTemplateDao.findById(id).orElse(null);

        if (Objects.isNull(messageTemplate)) {
            return BasicResultVO.fail();
        }

        MessageTemplate clone = ObjectUtil.clone(messageTemplate)
                .setMsgStatus(MessageStatus.STOP.getCode()) //设置消息状态
                .setUpdated(Math.toIntExact(DateUtil.currentSeconds())); //更新 更改时间
        messageTemplateDao.save(clone);

        //2，暂停定时任务
        return cronTaskService.stopCronTask(clone.getCronTaskId());
    }
}
