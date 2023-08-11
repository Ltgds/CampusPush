package com.ltgds.mypush.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendresultRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationRecallRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationRecallResponse;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.common.constant.SendAccountConstant;
import com.ltgds.mypush.common.domain.LogParam;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.account.dingDing.DingDingWorkNoticeAccount;
import com.ltgds.mypush.common.dto.model.DingDingWorkContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.enums.SendMessageType;
import com.ltgds.mypush.config.SupportThreadPoolConfig;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.utils.AccountUtils;
import com.ltgds.mypush.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Li Guoteng
 * @data 2023/8/7
 * @description 钉钉消息自定义机器人 消息处理器
 */
@Service
@Slf4j
public class DingDingWorkNoticeHandler extends BaseHandler implements Handler {

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LogUtils logUtils;

    public DingDingWorkNoticeHandler() {
        channelCode = ChannelType.DING_DING_WORK_NOTICE.getCode();
    }

    private static final String SEND_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";
    private static final String RECALL_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/recall";
    private static final String PULL_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult";
    private static final String DING_DING_RECALL_KEY_PREFIX = "RECALL_";
    private static final String RECALL_BIZ_TYPE = "DingDingWorkNoticeHandler#recall";

    @Override
    public boolean handler(TaskInfo taskInfo) {

        DingDingWorkNoticeAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), DingDingWorkNoticeAccount.class);
        OapiMessageCorpconversationAsyncsendV2Request request = assembleParam(account, taskInfo);

        return false;
    }

    /**
     * 拼装参数
     * @param account
     * @param taskInfo
     * @return
     */
    private OapiMessageCorpconversationAsyncsendV2Request assembleParam(DingDingWorkNoticeAccount account, TaskInfo taskInfo) {

        OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
        DingDingWorkContentModel contentModel = (DingDingWorkContentModel) taskInfo.getContentModel();

        try {
            //接收者相关
            if (PushConstant.SEND_ALL.equals(CollUtil.getFirst(taskInfo.getReceiver()))) {
                req.setToAllUser(true);
            } else {
                req.setUseridList(StringUtils.join(taskInfo.getReceiver(), StrUtil.C_COMMA));
            }
            req.setAgentId(Long.parseLong(account.getAgentId()));

            OapiMessageCorpconversationAsyncsendV2Request.Msg message = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            message.setMsgtype(SendMessageType.getDingDingWorkTypeByCode(contentModel.getSendType()));

            //根据类型设置入参
            if (SendMessageType.TEXT.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.Text textObj = new OapiMessageCorpconversationAsyncsendV2Request.Text();
                textObj.setContent(contentModel.getContent());
                message.setText(textObj);
            }

            if (SendMessageType.IMAGE.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.Image image = new OapiMessageCorpconversationAsyncsendV2Request.Image();
                image.setMediaId(contentModel.getMediaId());
                message.setImage(image);
            }

            if (SendMessageType.VOICE.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.Voice voice = new OapiMessageCorpconversationAsyncsendV2Request.Voice();
                voice.setMediaId(contentModel.getMediaId());
                voice.setDuration(contentModel.getDuration());
                message.setVoice(voice);
            }

            if (SendMessageType.FILE.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.File file = new OapiMessageCorpconversationAsyncsendV2Request.File();
                file.setMediaId(contentModel.getMediaId());
                message.setFile(file);
            }

            if (SendMessageType.LINK.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.Link link = new OapiMessageCorpconversationAsyncsendV2Request.Link();
                link.setText(contentModel.getContent());
                link.setMessageUrl(contentModel.getUrl());
                link.setPicUrl(contentModel.getMediaId());
                link.setTitle(contentModel.getTitle());
                message.setLink(link);
            }

            if (SendMessageType.MARKDOWN.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown = new OapiMessageCorpconversationAsyncsendV2Request.Markdown();
                markdown.setText(contentModel.getContent());
                markdown.setTitle(contentModel.getTitle());
                message.setMarkdown(markdown);
            }

            if (SendMessageType.ACTION_CARD.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.ActionCard actionCard = new OapiMessageCorpconversationAsyncsendV2Request.ActionCard();
                actionCard.setMarkdown(contentModel.getContent());
                actionCard.setTitle(contentModel.getTitle());
                actionCard.setBtnJsonList(JSON.parseArray(contentModel.getBtns(), OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList.class));
                actionCard.setBtnOrientation(contentModel.getBtnOrientation());
                message.setActionCard(actionCard);
            }

            if (SendMessageType.OA.getCode().equals(contentModel.getSendType())) {
                OapiMessageCorpconversationAsyncsendV2Request.OA oa = new OapiMessageCorpconversationAsyncsendV2Request.OA();
                oa.setMessageUrl(contentModel.getUrl());
                oa.setHead(JSON.parseObject(contentModel.getDingDingOaHead(), OapiMessageCorpconversationAsyncsendV2Request.Head.class));
                oa.setBody(JSON.parseObject(contentModel.getDingDingOaBody(), OapiMessageCorpconversationAsyncsendV2Request.Body.class));
                message.setOa(oa);
            }
            req.setMsg(message);
        } catch (Exception e) {
            log.error("assembleParam fail:{}, params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(taskInfo));
        }

        return req;
    }

    /**
     * 下发时存储 messageTemplate -> taskIdList
     * 只要还存在 taskIdList,则将其去除
     * @param messageTemplate
     */
    @Override
    public void recall(MessageTemplate messageTemplate) {
        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
            try {
                //获取DingDing工作账户
                DingDingWorkNoticeAccount account = accountUtils.getAccountById(messageTemplate.getSendAccount(), DingDingWorkNoticeAccount.class);
                //通过发送账户获取 accessToken
                String accessToken = redisTemplate.opsForValue().get(SendAccountConstant.DING_DING_ACCESS_TOKEN_PREFIX + messageTemplate.getSendAccount());
                //id列表长度 > 0
                while (redisTemplate.opsForList().size(DING_DING_RECALL_KEY_PREFIX + messageTemplate.getId()) > 0) {
                    //获取列表左边第一个元素 为taskId
                    String taskId = redisTemplate.opsForList().leftPop(DING_DING_RECALL_KEY_PREFIX + messageTemplate.getId());
                    //钉钉会话客户端
                    DefaultDingTalkClient client = new DefaultDingTalkClient(RECALL_URL);
                    OapiMessageCorpconversationRecallRequest req = new OapiMessageCorpconversationRecallRequest();
                    req.setAgentId(Long.valueOf(account.getAgentId()));
                    req.setMsgTaskId(Long.valueOf(taskId));
                    //客户端执行请求
                    OapiMessageCorpconversationRecallResponse rsp = client.execute(req, accessToken);

                    //打点
                    logUtils.print(LogParam.builder()
                            .bizType(RECALL_BIZ_TYPE)
                            .object(JSON.toJSONString(rsp)).build());
                }
            } catch (Exception e) {
                log.error("DingDingWorkNoticeHandler#recall fail!:{}", Throwables.getStackTraceAsString(e));
            }
        });
    }

    /**
     * 拉取回执
     * @param accountId
     */
    public void pull(Long accountId) {

        try {
            DingDingWorkNoticeAccount account = accountUtils.getAccountById(accountId.intValue(), DingDingWorkNoticeAccount.class);

            String accessToken = redisTemplate.opsForValue().get(SendAccountConstant.DING_DING_ACCESS_TOKEN_PREFIX + accountId);
            DingTalkClient client = new DefaultDingTalkClient(PULL_URL);

            OapiMessageCorpconversationGetsendresultRequest req = new OapiMessageCorpconversationGetsendresultRequest();
            req.setAgentId(Long.valueOf(account.getAgentId()));
            req.setTaskId(456L);

            OapiMessageCorpconversationGetsendresultResponse rsp = client.execute(req, accessToken);
        } catch (Exception e) {
            log.error("DingDingWorkNoticeHandler#pull fail:{}", Throwables.getStackTraceAsString(e));
        }
    }


}
