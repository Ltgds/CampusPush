package com.ltgds.mypush.handler.impl;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.account.EnterpriseWeChatRobotAccount;
import com.ltgds.mypush.common.dto.model.EnterpriseWeChatContentModel;
import com.ltgds.mypush.common.dto.model.EnterpriseWeChatRobotContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.enums.SendMessageType;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.domain.wechat.robot.EnterpriseWeChatRobotParam;
import com.ltgds.mypush.domain.wechat.robot.EnterpriseWeChatRootResult;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/6
 * @description 企业微信群机器人 消息处理器
 */
@Slf4j
@Service
public class EnterpriseWeChatRobotHandler extends BaseHandler implements Handler {

    @Autowired
    private AccountUtils accountUtils;

    public EnterpriseWeChatRobotHandler() {
        channelCode = ChannelType.ENTERPRISE_WE_CHAT_ROBOT.getCode();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {

        try {
            EnterpriseWeChatRobotAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), EnterpriseWeChatRobotAccount.class);
            EnterpriseWeChatRobotParam enterpriseWeChatRobotParam = assembleParam(taskInfo);

            String result = HttpRequest.post(account.getWebhook())
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .body(JSON.toJSONString(enterpriseWeChatRobotParam))
                    .timeout(2000)
                    .execute()
                    .body();

            //企业微信 返回值
            EnterpriseWeChatRootResult weChatRootResult = JSON.parseObject(result, EnterpriseWeChatRootResult.class);

            if (weChatRootResult.getErrcode() == 0) {
                return true;
            }

            log.error("EnterpriseWeChatRobotHandler#handler fail! result:{},params:{}", JSON.toJSONString(weChatRootResult), JSON.toJSONString(taskInfo));
        } catch (Exception e) {
            log.error("EnterpriseWeChatRobotHandler#handler fail!e:{},params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(taskInfo));
        }
        return false;
    }

    private EnterpriseWeChatRobotParam assembleParam(TaskInfo taskInfo) {
        EnterpriseWeChatRobotContentModel contentModel = (EnterpriseWeChatRobotContentModel) taskInfo.getContentModel();
        EnterpriseWeChatRobotParam param = EnterpriseWeChatRobotParam.builder()
                .msgType(SendMessageType.getEnterpriseWeChatRobotTypeByCode(contentModel.getSendType())).build();

        if (SendMessageType.TEXT.getCode().equals(contentModel.getSendType())) {
            param.setText(EnterpriseWeChatRobotParam.TextDTO.builder().content(contentModel.getContent()).build());
        }
        if (SendMessageType.MARKDOWN.getCode().equals(contentModel.getSendType())) {
            param.setMarkdown(EnterpriseWeChatRobotParam.MarkdownDTO.builder().content(contentModel.getContent()).build());
        }
        if (SendMessageType.IMAGE.getCode().equals(contentModel.getSendType())) {
            param.setImage(EnterpriseWeChatRobotParam.ImageDTO.builder().base64(contentModel.getBase64()).md5(contentModel.getMd5()).build());
        }
        if (SendMessageType.FILE.getCode().equals(contentModel.getSendType())) {
            param.setFile(EnterpriseWeChatRobotParam.FileDTO.builder().mediaId(contentModel.getMediaId()).build());
        }
        if (SendMessageType.NEWS.getCode().equals(contentModel.getSendType())) {
            List<EnterpriseWeChatRobotParam.NewsDTO.ArticlesDTO> articlesDtoS = JSON.parseArray(contentModel.getArticles(), EnterpriseWeChatRobotParam.NewsDTO.ArticlesDTO.class);
            param.setNews(EnterpriseWeChatRobotParam.NewsDTO.builder().articles(articlesDtoS).build());
        }
        if (SendMessageType.TEMPLATE_CARD.getCode().equals(contentModel.getSendType())) {
            //
        }
        return param;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
