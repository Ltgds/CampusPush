package handler;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.dto.account.getui.GeTuiAccount;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.config.SupportThreadPoolConfig;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import com.xxl.job.core.handler.annotation.XxlJob;
import dto.getui.GeTuiTokenResultDTO;
import dto.getui.QueryTokenParamDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/13
 * @description 刷新个推的token
 */
@Slf4j
@Service
public class RefreshGeTuiAccessTokenHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ChannelAccountDao channelAccountDao;

    /**
     * 每小时请求一次接口刷新
     */
    @XxlJob("refreshGeTuiAccessTokenJob")
    public void execute() {
        log.info("refreshGeTuiAccessTokenJob#execute");

        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
            //找到所有push对应的账号列表
            List<ChannelAccount> accountList = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE,
                    ChannelType.PUSH.getCode());

            for (ChannelAccount channelAccount : accountList) {
                //将账号配置转为GeTui账号类
                GeTuiAccount account = JSON.parseObject(channelAccount.getAccountConfig(), GeTuiAccount.class);
                String accessToken = getAccessToken(account);
            }

        });
    }

    /**
     * 获取 access_token
     * @param account
     * @return
     */
    private String getAccessToken(GeTuiAccount account) {

        String accessToken = "";

        try {
            String url = "https://restapi.getui.com/v2/" + account.getAppId() + "/auth";
            String time = String.valueOf(System.currentTimeMillis());
            String digest = SecureUtil.sha256().digestHex(account.getAppKey() + time + account.getMasterSecret());

            //请求token的参数
            QueryTokenParamDTO param = QueryTokenParamDTO.builder()
                    .sign(digest)
                    .appKey(account.getAppKey())
                    .timestamp(time)
                    .build();

            //发送post请求
            String body = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()) //Content-Type application/json
                    .body(JSON.toJSONString(param)) //将token请求参数转为string类型
                    .timeout(20000)
                    .execute().body();

            //请求参数转为GeTuiTokenResultDTO
            GeTuiTokenResultDTO geTuiTokenResultDTO = JSON.parseObject(body, GeTuiTokenResultDTO.class);

            if (geTuiTokenResultDTO.getCode().equals(0)) {
                accessToken = geTuiTokenResultDTO.getData().getToken();
            }
        } catch (Exception e) {
            log.error("RefreshGeTuiAccessTokenHandler#getAccessToken fail:{}", Throwables.getStackTraceAsString(e));
        }

        return accessToken;
    }

}
