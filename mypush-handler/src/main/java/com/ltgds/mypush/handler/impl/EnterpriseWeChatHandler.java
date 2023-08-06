package com.ltgds.mypush.handler.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.model.EnterpriseWeChatContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.enums.SendMessageType;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpMessageServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.article.MpnewsArticle;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description
 */
@Slf4j
@Component
public class EnterpriseWeChatHandler extends BaseHandler implements Handler {

    @Autowired
    private AccountUtils accountUtils;

    public EnterpriseWeChatHandler() {
        channelCode = ChannelType.ENTERPRISE_WE_CHAT.getCode();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {
        try {
            WxCpDefaultConfigImpl accountConfig = accountUtils.getAccountById(taskInfo.getSendAccount(), WxCpDefaultConfigImpl.class);
            WxCpMessageServiceImpl messageService = new WxCpMessageServiceImpl(initService(accountConfig));
            WxCpMessageSendResult result = messageService.send(buildWxCpMessage(taskInfo, accountConfig.getAgentId()));
        } catch (Exception e) {

        }
        return false;
    }


    /**
     * 初始化WxCpServiceImpl 服务接口
     * @param config
     * @return
     */
    private WxCpService initService(WxCpDefaultConfigImpl config) {
        WxCpServiceImpl wxCpService = new WxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(config);
        return wxCpService;
    }

    /**
     * 构建企业微信下发消息的对象
     * @param taskInfo
     * @param agentId
     * @return
     */
    private WxCpMessage buildWxCpMessage(TaskInfo taskInfo, Integer agentId) {
        String userId;

        //当taskInfo集合中的第一个任务的接收者是 消息发送给所有人
        if (PushConstant.SEND_ALL.equals(CollUtil.getFirst(taskInfo.getReceiver()))) {
            userId = CollUtil.getFirst(taskInfo.getReceiver());
        } else {
            //使用|分隔receiver 进行拼接
            userId = StringUtils.join(taskInfo.getReceiver(), CommonConstant.RADICAL);
        }

        EnterpriseWeChatContentModel contentModel = (EnterpriseWeChatContentModel) taskInfo.getContentModel();

        //通用配置
        WxCpMessage wxCpMessage = null;

        if (SendMessageType.TEXT.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.TEXT().content(contentModel.getContent()).build();
        }else if (SendMessageType.IMAGE.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.IMAGE().mediaId(contentModel.getMediaId()).build();
        } else if (SendMessageType.VOICE.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.VOICE().mediaId(contentModel.getMediaId()).build();
        } else if (SendMessageType.VIDEO.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.VIDEO().mediaId(contentModel.getMediaId()).description(contentModel.getDescription()).title(contentModel.getTitle()).build();
        } else if (SendMessageType.FILE.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.FILE().mediaId(contentModel.getMediaId()).build();
        } else if (SendMessageType.TEXT_CARD.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.TEXTCARD().url(contentModel.getUrl()).title(contentModel.getTitle()).description(contentModel.getDescription()).btnTxt(contentModel.getBtnTxt()).build();
        } else if (SendMessageType.NEWS.getCode().equals(contentModel.getSendType())) {
            List<NewArticle> newArticles = JSON.parseArray(contentModel.getArticles(), NewArticle.class);
            wxCpMessage = WxCpMessage.NEWS().articles(newArticles).build();
        } else if (SendMessageType.MP_NEWS.getCode().equals(contentModel.getSendType())) {
            List<MpnewsArticle> mpNewsArticles = JSON.parseArray(contentModel.getMpNewsArticle(), MpnewsArticle.class);
            wxCpMessage = WxCpMessage.MPNEWS().articles(mpNewsArticles).build();
        } else if (SendMessageType.MARKDOWN.getCode().equals(contentModel.getSendType())) {
            wxCpMessage = WxCpMessage.MARKDOWN().content(contentModel.getContent()).build();
        } else if (SendMessageType.MINI_PROGRAM_NOTICE.getCode().equals(contentModel.getSendType())) {
            Map contentItems = JSON.parseObject(contentModel.getContentItems(), Map.class);
            wxCpMessage = WxCpMessage.newMiniProgramNoticeBuilder().appId(contentModel.getAppId()).page(contentModel.getPage()).emphasisFirstItem(contentModel.getEmphasisFirstItem()).contentItems(contentItems).title(contentModel.getTitle()).description(contentModel.getDescription()).build();
        } else if (SendMessageType.TEMPLATE_CARD.getCode().equals(contentModel.getSendType())) {
            // WxJava 未支持
        }
        wxCpMessage.setAgentId(agentId);
        wxCpMessage.setToUser(userId);
        return wxCpMessage;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
