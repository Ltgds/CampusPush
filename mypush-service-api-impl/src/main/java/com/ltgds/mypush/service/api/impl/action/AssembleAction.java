package com.ltgds.mypush.service.api.impl.action;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.model.ContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.dao.MessageTemplateDao;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.pipeline.BusinessProcess;
import com.ltgds.mypush.pipeline.ProcessContext;
import com.ltgds.mypush.service.api.domain.MessageParam;
import com.ltgds.mypush.service.api.enmus.BusinessCode;
import com.ltgds.mypush.service.api.impl.domain.SendTaskModel;
import com.ltgds.mypush.utils.ContentHolderUtil;
import com.ltgds.mypush.utils.TaskInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Li Guoteng
 * @data 2023/6/4
 * @description 2.拼装参数
 */
@Slf4j
@Service
public class AssembleAction implements BusinessProcess<SendTaskModel> {

    @Resource
    private MessageTemplateDao messageTemplateDao; //消息模板dao,分页查询;统计未删除条数

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();

        try {
            //1. 未找到对应的消息模板
            //通过消息模板id查询数据库中对应的消息模板
            Optional<MessageTemplate> messageTemplate = messageTemplateDao.findById(messageTemplateId);
            //messageTemplate == null || 其值已经标记删除(isDeleted == 1)
            if (!messageTemplate.isPresent() || messageTemplate.get().getIsDeleted().equals(PushConstant.TRUE)) {
                context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.TEMPLATE_NOT_FOUNT));
                return;
            }

            //2. 若上下文code是
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) { //普通发送,则组装消息参数
                List<TaskInfo> taskInfos = assembleTaskInfo(sendTaskModel, messageTemplate.get());
                sendTaskModel.setTaskInfo(taskInfos);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) { //撤回消息
                sendTaskModel.setMessageTemplate(messageTemplate.get());
            }
        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("assemble task fail! templateId:{}, e:{}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }

    }

    /**
     * 组装TaskInfo任务消息
     * @param sendTaskModel  //发送任务模型
     * @param messageTemplate //消息模板内容
     * @return 发送任务消息列表
     */
    private List<TaskInfo> assembleTaskInfo(SendTaskModel sendTaskModel, MessageTemplate messageTemplate) {
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList(); //获得消息参数列表
        ArrayList<TaskInfo> taskInfoList = new ArrayList<>();

        for (MessageParam messageParam : messageParamList) { //对每个消息参数列表

            TaskInfo taskInfo = TaskInfo.builder()
                    .messageTemplateId(messageTemplate.getId())
                    .businessId(TaskInfoUtils.generateBusinessId(messageTemplate.getId(), messageTemplate.getTemplateType()))
                    .receiver(new HashSet<>(Arrays.asList(messageParam.getReceiver().split(String.valueOf(StrUtil.C_COMMA)))))
                    .idType(messageTemplate.getIdType())
                    .sendChannel(messageTemplate.getSendChannel())
                    .templateType(messageTemplate.getTemplateType())
                    .msgType(messageTemplate.getMsgType())
                    .shieldType(messageTemplate.getShieldType())
                    .sendAccount(messageTemplate.getSendAccount())
                    .contentModel(getContentModelValue(messageTemplate, messageParam))
                    .build();

            taskInfoList.add(taskInfo);

        }
        return taskInfoList;
    }

    /**
     * 获取 contentModel,替换模板msgContent中的占位符信息
     * @param messageTemplate
     * @param messageParam
     * @return
     */
    private static ContentModel getContentModelValue(MessageTemplate messageTemplate, MessageParam messageParam) {

        //获得真正的ContentModel类型
        Integer sendChannel = messageTemplate.getSendChannel(); //SMS code:30
        Class contentModelClass = ChannelType.getChanelModelClassByCode(sendChannel); //SmsContentModel.class

        //得到模板的 msgContent和入参
        Map<String, String> variables = messageParam.getVariables(); //消息参数中的可变部分
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent()); //将真正的消息转为JSON格式

        //通过反射组装出 contentModel
        Field[] fields = ReflectUtil.getFields(contentModelClass); //获取所有字段
        ContentModel contentModel = (ContentModel) ReflectUtil.newInstance(contentModelClass);
        for (Field field : fields) {
            String originValue = jsonObject.getString(field.getName());

            if (StrUtil.isNotBlank(originValue)) {
                String resultValue = ContentHolderUtil.replacePlaceHolder(originValue, variables);
                Object resultObj = JSONUtil.isJsonObj(resultValue) ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;
                ReflectUtil.setFieldValue(contentModel, field, resultObj);
            }
        }

        //若url字段存在，则在url拼接对应的埋点参数
        String url = (String) ReflectUtil.getFieldValue(contentModel, "url");
        if (StrUtil.isNotBlank(url)) {
            String resultUrl = TaskInfoUtils.generateUrl(url, messageTemplate.getId(), messageTemplate.getTemplateType());
            ReflectUtil.setFieldValue(contentModel, "url", resultUrl);
        }
        return contentModel;
    }
}
