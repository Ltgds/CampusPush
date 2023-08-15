package com.ltgds.mypush.handler.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.SendAccountConstant;
import com.ltgds.mypush.common.domain.TaskInfo;
import com.ltgds.mypush.common.dto.account.getui.GeTuiAccount;
import com.ltgds.mypush.common.dto.model.ContentModel;
import com.ltgds.mypush.common.dto.model.PushContentModel;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.domain.MessageTemplate;
import com.ltgds.mypush.domain.push.PushParam;
import com.ltgds.mypush.domain.push.getui.BatchSendPushParam;
import com.ltgds.mypush.domain.push.getui.SendPushParam;
import com.ltgds.mypush.domain.push.getui.SendPushResult;
import com.ltgds.mypush.handler.BaseHandler;
import com.ltgds.mypush.handler.Handler;
import com.ltgds.mypush.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/8/14
 * @description 通知栏消息发送处理
 */
@Slf4j
@Component
public class PushHandler extends BaseHandler implements Handler {

    private static final String BASE_URL = "https://restapi.getui.com/v2/";
    private static final String SINGLE_PUSH_PATH = "/push/single/cid";
    private static final String BATCH_PUSH_CREATE_TASK_PATH = "/push/list/message";
    private static final String BATCH_PUSH_PATH = "/push/list/cid";

    public PushHandler() {
        channelCode = ChannelType.PUSH.getCode();
    }

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean handler(TaskInfo taskInfo) {

        try {
            GeTuiAccount account = accountUtils.getAccountById(taskInfo.getSendAccount(), GeTuiAccount.class);
            //设置token ge_tui_access_token+
            String token = redisTemplate.opsForValue().get(SendAccountConstant.GE_TUI_ACCESS_TOKEN_PREFIX + taskInfo.getSendAccount());
            PushParam pushParam = PushParam.builder()
                    .token(token)
                    .appId(account.getAppId())
                    .taskInfo(taskInfo)
                    .build();

            String result;
            //接收者只有一个
            if (taskInfo.getReceiver().size() == 1) {
                result = singlePush(pushParam);
            } else {
                result = batchPush(createTaskId(pushParam), pushParam);
            }

            SendPushResult sendPushResult = JSON.parseObject(result, SendPushResult.class);

            if (sendPushResult.getCode().equals(0)) {
                return true;
            }
            //常见的错误, 关联至AnchorState,由后台统一透出失败原因
            log.error("PushHandler#handler fail! result:{}, param:{}", JSON.toJSONString(sendPushResult),
                    JSON.toJSONString(taskInfo));
        } catch (Exception e) {
            log.error("PushHandler#handler fail!e:{},params:{}", Throwables.getStackTraceAsString(e),
                    JSON.toJSONString(taskInfo));
        }

        return false;
    }

    /**
     * 单推
     * @param pushParam
     * @return
     */
    private String singlePush(PushParam pushParam) {

        String url = BASE_URL + pushParam.getAppId() + SINGLE_PUSH_PATH;
        //组装sendPush参数
        SendPushParam sendPushParam = assembleParam((PushContentModel) pushParam.getTaskInfo().getContentModel(),
                pushParam.getTaskInfo().getReceiver());

        String body = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", pushParam.getToken())
                .body(JSON.toJSONString(sendPushParam))
                .timeout(2000)
                .execute().body();

        return body;
    }

    /**
     * 批量推送
     * @param taskId 个推 返回的任务Id
     * @param pushParam
     * @return
     */
    private String batchPush(String taskId, PushParam pushParam) {

        String url = BASE_URL + pushParam.getAppId() + BATCH_PUSH_PATH;

        BatchSendPushParam batchSendPushParam = BatchSendPushParam.builder()
                .taskId(taskId)
                .isAsync(true)
                .audience(BatchSendPushParam.AudienceVO.builder()
                        .cid(pushParam.getTaskInfo().getReceiver())
                        .build())
                .build();

        String body = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .header("token", pushParam.getToken())
                .body(JSON.toJSONString(batchSendPushParam))
                .timeout(2000)
                .execute().body();

        return body;
    }

    /**
     * 批量推送前 需要构建taskId
     * @param pushParam
     * @return
     */
    private String createTaskId(PushParam pushParam) {

        String url = BASE_URL + pushParam.getAppId() + BATCH_PUSH_CREATE_TASK_PATH;

        SendPushParam param = assembleParam((PushContentModel) pushParam.getTaskInfo().getContentModel());

        String taskId = "";
        try {
            String body = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .header("token", pushParam.getToken())
                    .body(JSON.toJSONString(param))
                    .timeout(2000)
                    .execute().body();

            //将json字符串转为SendPushResult对象
            taskId = JSON.parseObject(body, SendPushResult.class)
                    .getData().getString("taskId");
        } catch (Exception e) {
            log.error("PushHandler#createTaskId fail :{},params:{}",
                    Throwables.getStackTraceAsString(e), JSON.toJSONString(pushParam.getTaskInfo()));
        }
        return taskId;
    }

    /**
     * 批量推送拼装发送参数
     * @param pushContentModel
     * @return
     */
    private SendPushParam assembleParam(PushContentModel pushContentModel) {
        return assembleParam(pushContentModel, null);
    }

    /**
     * 拼装push发送参数
     * @param pushContentModel
     * @param cid 接收者 receiver
     * @return
     */
    private SendPushParam assembleParam(PushContentModel pushContentModel, Set<String> cid) {
        SendPushParam param = SendPushParam.builder()
                .requestId(String.valueOf(IdUtil.getSnowflake().nextId()))
                .pushMessage(SendPushParam.PushMessageVO.builder()
                        .notification(SendPushParam.PushMessageVO.NotificationVO.builder()
                                .title(pushContentModel.getTitle())
                                .body(pushContentModel.getContent())
                                .clickType("startapp").build())
                        .build())
                .build();

        if (CollUtil.isNotEmpty(cid)) {
            param.setAudience(SendPushParam.AudienceVO.builder().cid(cid).build());
        }
        return param;
    }


    @Override
    public void recall(MessageTemplate messageTemplate) {

    }
}
