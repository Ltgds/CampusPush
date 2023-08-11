package com.ltgds.mypush.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.account.sms.SmsAccount;
import com.ltgds.mypush.common.dto.model.SmsContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.dao.SmsRecordDao;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.domain.SmsRecord;
import com.ltgds.mypush.domain.sms.MessageTypeSmsConfig;
import com.ltgds.mypush.domain.sms.SmsParam;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.script.SmsScript;
import com.ltgds.mypush.service.ConfigService;
import com.ltgds.mypush.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 短信发送处理
 */
@Slf4j
@Component
public class SmsHandler extends BaseHandler implements Handler {

    public SmsHandler() {
        channelCode = ChannelType.SMS.getCode();
    }

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Autowired
    private ConfigService config;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AccountUtils accountUtils;

    /**
     * 流量自动分配策略
     */
    private static final Integer AUTO_FLOW_RULE = 0;

    private static final String FLOW_KEY = "msgTypeSmsConfig";
    private static final String FLOW_KEY_PREFIX = "message_type_";

    @Override
    public boolean handler(TaskInfo taskInfo) {
        //拼装参数
        SmsParam smsParam = SmsParam.builder()
                .phones(taskInfo.getReceiver())
                .content(getSmsContent(taskInfo))
                .messageTemplateId(taskInfo.getMessageTemplateId())
                .build();

        try {
            /**
             * 1.动态配置做流量负载
             * 2.发送短信
             */
            MessageTypeSmsConfig[] messageTypeSmsConfigs = loadBalance(getMessageTypeSmsConfig(taskInfo));

            for (MessageTypeSmsConfig messageTypeSmsConfig : messageTypeSmsConfigs) {
                smsParam.setScriptName(messageTypeSmsConfig.getScriptName());
                smsParam.setSendAccountId(messageTypeSmsConfig.getSendAccount());

                //通过短信脚本选择 短信供应商
                //发送,获得对应的渠道商发送方接口返回值
                List<SmsRecord> recordList = applicationContext.getBean(messageTypeSmsConfig.getScriptName(), SmsScript.class)
                        .send(smsParam);

                if (CollUtil.isNotEmpty(recordList)) {
                    smsRecordDao.saveAll(recordList);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("SmsHandler#handler fail:{},params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(smsParam));
        }
        return false;
    }

    /**
     * 流量负载
     * 根据配置的权重优先走某个账号,并取出一个备份
     *
     * 优先级高的在数组最前面
     *
     * @param messageTypeSmsConfigs
     * @return
     */
    private MessageTypeSmsConfig[] loadBalance(List<MessageTypeSmsConfig> messageTypeSmsConfigs) {

        int total = 0;
        for (MessageTypeSmsConfig channelConfig : messageTypeSmsConfigs) {
            total += channelConfig.getWeight();
        }

        //生成一个随机数[1,total],看落到哪个区间
        Random random = new Random();
        int index = random.nextInt(total) + 1;

        MessageTypeSmsConfig supplier = null;
        MessageTypeSmsConfig supplierBack = null;

        for (int i = 0; i < messageTypeSmsConfigs.size(); i++) {
            if (index <= messageTypeSmsConfigs.get(i).getWeight()) {
                supplier = messageTypeSmsConfigs.get(i);

                //取下一个供应商
                int j = (i + 1) % messageTypeSmsConfigs.size();
                if (i == j) {
                    return new MessageTypeSmsConfig[]{supplier};
                }

                supplierBack = messageTypeSmsConfigs.get(j);
                return new MessageTypeSmsConfig[]{supplier, supplierBack};
            }
            index -= messageTypeSmsConfigs.get(i).getWeight();
        }
        return null;
    }

    /**
     * 根据消息类型获取流量配置
     *
     * 如果模板指定具体的明确账号,则优先发其账号,否则走到流量配置
     * <p>
     * 流量配置每种类型都会有其下发渠道账号的配置(包括流量占比也会在配置中)
     * <p>
     * 样例:
     * key: msgTypeSmsConfig
     * value:[  {"message_type_10":[{"weights":80,"scriptName":"TencentSmsScript"},{"weights":20,"scriptName":"YunPianSmsScript"}]},
     * {"message_type_20":[{"weights":20,"scriptName":"YunPianSmsScript"}]},
     * {"message_type_30":[{"weights":20,"scriptName":"TencentSmsScript"}]},
     * {"message_type_40":[{"weights":20,"scriptName":"TencentSmsScript"}]}
     * ]
     * <p>
     * 通知类短信有两个发送渠道 TencentSmsScript 占流量80%, YunPianSmsScript占20%流量
     * 营销类短信只有一个发送渠道 YunPianSmsScript
     * 验证码短信只有一个发送渠道 TencentSmsScript
     *
     * @param taskInfo
     * @return
     */
    private List<MessageTypeSmsConfig> getMessageTypeSmsConfig(TaskInfo taskInfo) {

        /**
         * 如果模板指定了账号,则优先使用具体的账号进行发送
         */
        if (!taskInfo.getSendAccount().equals(AUTO_FLOW_RULE)) {
            SmsAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), SmsAccount.class);
            return Arrays.asList(MessageTypeSmsConfig.builder()
                            .sendAccount(taskInfo.getSendAccount())
                            .scriptName(account.getScriptName())
                            .weight(100)
                    .build());
        }

        /**
         * 读取流量配置
         */
        String property = config.getProperty(FLOW_KEY, CommonConstant.EMPTY_VALUE_JSON_ARRAY);
        JSONArray jsonArray = JSON.parseArray(property);
        for (int i = 0; i < jsonArray.size(); i++) {
            //根据message_type_消息类型 读取对应的 流量负载配置
            JSONArray array = jsonArray.getJSONObject(i).getJSONArray(FLOW_KEY_PREFIX + taskInfo.getMsgType());

            if (CollUtil.isNotEmpty(array)) {
                return JSON.parseArray(JSON.toJSONString(array), MessageTypeSmsConfig.class);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 如果有输入链接,则将链接拼在文案后
     * <p>
     * 如果是营销类的短信,需要考虑拼接 回TD退订 之类的文案
     *
     * @param taskInfo
     * @return
     */
    private String getSmsContent(TaskInfo taskInfo) {
        //获取短信内容模型
        SmsContentModel smsContentModel = (SmsContentModel) taskInfo.getContentModel();
        //短信发送链接存在,则将发送链接拼接在文案后
        if (StrUtil.isNotBlank(smsContentModel.getUrl())) {
            //短信发送内容 短信发送链接
            return smsContentModel.getContent() + StrUtil.SPACE + smsContentModel.getUrl();
        } else {
            return smsContentModel.getContent();
        }
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
