package handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.SendAccountConstant;
import com.ltgds.mypush.common.dto.account.DingDingWorkNoticeAccount;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.config.SupportThreadPoolConfig;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description 刷新钉钉的access_token
 */
@Service
@Slf4j
public class RefreshDingDingAccessTokenHandler {

    private static final String URL = "https://oapi.dingtalk.com/gettoken";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ChannelAccountDao channelAccountDao;

    /**
     * 每小时请求一次接口刷新
     */
    @XxlJob("refreshAccessTokenJob")
    public void execute() {
        log.info("refreshAccessTokenJob#execute!");
        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
            List<ChannelAccount> accountList = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE, ChannelType.DING_DING_WORK_NOTICE.getCode());

            for (ChannelAccount channelAccount : accountList) {

                //将账号配置转为 DingDingWorkNoticeAccount类
                DingDingWorkNoticeAccount account = JSON.parseObject(channelAccount.getAccountConfig(), DingDingWorkNoticeAccount.class);
                String accessToken = getAccessToken(account);

                if (StrUtil.isNotBlank(accessToken)) {
                    redisTemplate.opsForValue().set(SendAccountConstant.DING_DING_ACCESS_TOKEN_PREFIX + channelAccount.getId(), accessToken);
                }
            }

        });
    }

    /**
     * 获取 access_token
     * @param account
     * @return
     */
    private String getAccessToken(DingDingWorkNoticeAccount account) {
        String accessToken = "";

        try {
            DingTalkClient client = new DefaultDingTalkClient(URL);
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey(account.getAppKey());
            req.setAppsecret(account.getAppSecret());
            req.setHttpMethod(CommonConstant.REQUEST_METHOD_GET);

            OapiGettokenResponse rsp = client.execute(req);
            accessToken = rsp.getAccessToken();
        } catch (Exception e) {
            log.error("RefreshDingDingAccessTokenHandler#getAccessToken fail:{}", Throwables.getStackTraceAsString(e));
        }
        return accessToken;
    }
}
