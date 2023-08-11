package com.ltgds.mypush.handler.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.account.dingDing.DingDingRobotAccount;
import com.ltgds.mypush.common.dto.model.DingDingRobotContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.common.enums.SendMessageType;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.domain.dingding.DingDingRobotParam;
import com.ltgds.mypush.domain.dingding.DingDingRobotResult;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/7
 * @description 钉钉消息自定义机器人 消息处理器
 */
@Slf4j
@Service
public class DingDingRobotHandler extends BaseHandler implements Handler {

    @Autowired
    private AccountUtils accountUtils;

    public DingDingRobotHandler() {
        channelCode = ChannelType.DING_DING_ROBOT.getCode();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {

        try {
            //返回账号对象
            DingDingRobotAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), DingDingRobotAccount.class);
            //拼装dingDing机器人参数
            DingDingRobotParam dingDingRobotParam = assembleParam(taskInfo);

            String httpResult = HttpUtil.post(assembleParamUrl(account), JSON.toJSONString(dingDingRobotParam));
            DingDingRobotResult dingDingRobotResult = JSON.parseObject(httpResult, DingDingRobotResult.class);

            if (dingDingRobotResult.getErrCode() == 0) {
                return true;
            }

            //常见的错误 应当 关联至 AnchorState, 由后台统一透出失败原因
            log.error("DingDingHandler#handler fail! result:{}, params:{}", JSON.toJSONString(dingDingRobotResult), JSON.toJSONString(taskInfo));
        } catch (Exception e) {
            log.error("DingDingHandler#handler fail! e:{}, params:{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(taskInfo));
        }
        return false;
    }

    /**
     * 拼装url
     * @param account
     * @return
     */
    private String assembleParamUrl(DingDingRobotAccount account) {
        long currentTimeMillis = System.currentTimeMillis();
        String sign = assembleSign(currentTimeMillis, account.getSecret());
        return (account.getWebhook() + "&timestamp=" + currentTimeMillis + "&sign=" + sign);
    }

    /**
     * 使用sha256算法计算签名
     * @param currentTimeMillis
     * @param secret
     * @return
     */
    private String assembleSign(long currentTimeMillis, String secret) {
        String sign = "";

        try {
            String stringToSign = currentTimeMillis + String.valueOf(StrUtil.C_LF) + secret;
            Mac mac = Mac.getInstance(CommonConstant.HMAC_SHA256_ENCRYPTION_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(CommonConstant.CHARSET_NAME), CommonConstant.HMAC_SHA256_ENCRYPTION_ALGO));
            byte[] signData = mac.doFinal(stringToSign.getBytes(CommonConstant.CHARSET_NAME));
            sign = URLEncoder.encode(new String(Base64.encode(signData)), CommonConstant.CHARSET_NAME);
        } catch (Exception e) {
            log.error("DingDingHandler#assembleSign file!:{}", Throwables.getStackTraceAsString(e));
        }

        return sign;
    }

    /**
     * 组装接收者 和消息类型以及内容 参数
     * @param taskInfo
     * @return
     */
    private DingDingRobotParam assembleParam(TaskInfo taskInfo) {
        //接收者相关
        DingDingRobotParam.AtVO atVO = DingDingRobotParam.AtVO.builder().build();
        if (PushConstant.SEND_ALL.equals(CollUtil.getFirst(taskInfo.getReceiver()))) {
            atVO.setIsAtAll(true);
        } else {
            atVO.setAtUserIds(new ArrayList<>(taskInfo.getReceiver()));
        }

        //消息类型以及内容相关
        DingDingRobotContentModel contentModel = (DingDingRobotContentModel) taskInfo.getContentModel();
        DingDingRobotParam param = DingDingRobotParam.builder()
                .at(atVO)
                .msgtype(SendMessageType.getDingDingRobotTypeCode(contentModel.getSendType()))
                .build();

        if (SendMessageType.TEXT.getCode().equals(contentModel.getSendType())) {
            param.setText(DingDingRobotParam.TextVO.builder()
                    .content(contentModel.getContent()).build());
        }

        if (SendMessageType.MARKDOWN.getCode().equals(contentModel.getSendType())) {
            param.setMarkdown(DingDingRobotParam.MarkdownVO.builder()
                    .title(contentModel.getTitle())
                    .text(contentModel.getContent()).build());
        }

        if (SendMessageType.LINK.getCode().equals(contentModel.getSendType())) {
            param.setLink(DingDingRobotParam.LinkVO.builder()
                    .title(contentModel.getTitle())
                    .text(contentModel.getContent())
                    .messageUrl(contentModel.getUrl())
                    .picUrl(contentModel.getPicUrl()).build());
        }

        if (SendMessageType.NEWS.getCode().equals(contentModel.getSendType())) {
            List<DingDingRobotParam.FeedCardVO.LinksVO> linksVOS = JSON.parseArray(contentModel.getFeedCards(), DingDingRobotParam.FeedCardVO.LinksVO.class);
            DingDingRobotParam.FeedCardVO feedCardVO = DingDingRobotParam.FeedCardVO.builder()
                    .links(linksVOS).build();

            param.setFeedCard(feedCardVO);
        }

        if (SendMessageType.ACTION_CARD.getCode().equals(contentModel.getSendType())) {
            List<DingDingRobotParam.ActionCardVO.BtnsVO> btnsVOS = JSON.parseArray(contentModel.getBtns(), DingDingRobotParam.ActionCardVO.BtnsVO.class);
            DingDingRobotParam.ActionCardVO actionCardVO = DingDingRobotParam.ActionCardVO.builder()
                    .title(contentModel.getTitle())
                    .text(contentModel.getContent())
                    .btnOrientation(contentModel.getBtnOrientation())
                    .btns(btnsVOS)
                    .build();

            param.setActionCard(actionCardVO);
        }

        return param;
    }

    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
