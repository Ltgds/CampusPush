package com.ltgds.mypush.web.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.SendAccountConstant;
import com.ltgds.mypush.common.dto.account.weChat.EnterpriseWeChatRobotAccount;
import com.ltgds.mypush.common.enums.EnumUtil;
import com.ltgds.mypush.common.enums.FileType;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.common.vo.BasicResultVO;
import com.ltgds.mypush.domain.wechat.robot.EnterpriseWeChatRootResult;
import com.ltgds.mypush.utils.AccountUtils;
import com.ltgds.mypush.web.service.MaterialService;
import com.ltgds.mypush.web.utils.SpringFileUtils;
import com.ltgds.mypush.web.vo.UploadResponseVo;
import com.taobao.api.FileItem;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Li Guoteng
 * @data 2023/8/8
 * @description
 */
@Slf4j
@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AccountUtils accountUtils;

    private static final String DING_DING_URL = "https://oapi.dingtalk.com/media/upload";
    private static final String ENTERPRISE_WE_CHAT_ROBOT_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?key=<KEY>&type=<TYPE>";

    @Override
    public BasicResultVO dingDingMaterialUpload(MultipartFile file, String sendAccount, String fileType) {
        OapiMediaUploadResponse response;

        try {
            String accessToken = redisTemplate.opsForValue().get(SendAccountConstant.DING_DING_ACCESS_TOKEN_PREFIX + sendAccount);
            DingTalkClient client = new DefaultDingTalkClient(DING_DING_URL);
            OapiMediaUploadRequest request = new OapiMediaUploadRequest();

            FileItem item = new FileItem(new StringBuilder().append(IdUtil.fastSimpleUUID()).append(file.getOriginalFilename()).toString(),
                    file.getInputStream());

            request.setMedia(item);
            request.setType(EnumUtil.getDescriptionByCode(Integer.valueOf(fileType), FileType.class));
            response = client.execute(request, accessToken);

            if (response.getErrcode() == 0L) {
                return new BasicResultVO(RespStatusEnum.SUCCESS, UploadResponseVo.builder().id(response.getMediaId()).build());
            }
            log.error("MaterialService#dingDingMaterialUpload fail:{}", response.getErrmsg());
        } catch (Exception e) {
            log.error("MaterialService#dingDingMaterialUpload fail:{}", Throwables.getStackTraceAsString(e));
        }
        return BasicResultVO.fail("未知错误,联系管理员");
    }

    @Override
    public BasicResultVO enterpriseWeChatRootMaterialUpload(MultipartFile file, String sendAccount, String fileType) {

        try {
            EnterpriseWeChatRobotAccount account = accountUtils.getAccountById(Integer.valueOf(sendAccount), EnterpriseWeChatRobotAccount.class);

            String key = account.getWebhook().substring(account.getWebhook().indexOf(CommonConstant.EQUAL_STRING) + 1);
            String url = ENTERPRISE_WE_CHAT_ROBOT_URL.replace("<KEY>", key).replace("<TYPE>", "file");

            String response = HttpRequest.post(url)
                    .form(IdUtil.fastSimpleUUID(), SpringFileUtils.getFile(file))
                    .execute().body();

            EnterpriseWeChatRootResult result = JSON.parseObject(response, EnterpriseWeChatRootResult.class);

            if (result.getErrcode() == 0) {
                return new BasicResultVO(RespStatusEnum.SUCCESS, UploadResponseVo.builder().id(result.getMediaId()).build());
            }

            log.error("MaterialService#enterpriseWeChatRootMaterialUpload fail:{}", result.getErrmsg());
        } catch (Exception e) {
            log.error("MaterialService#enterpriseWeChatRootMaterialUpload fail:{}", Throwables.getStackTraceAsString(e));
        }
        return BasicResultVO.fail("未知错误,联系管理员");
    }

    @Override
    public BasicResultVO enterpriseWeChatMaterialUpload(MultipartFile file, String sendAccount, String fileType) {

        try {
            WxCpDefaultConfigImpl accountConfig = accountUtils.getAccountById(Integer.valueOf(sendAccount), WxCpDefaultConfigImpl.class);
            WxCpServiceImpl wxCpService = new WxCpServiceImpl();
            wxCpService.setWxCpConfigStorage(accountConfig);

            WxMediaUploadResult result = wxCpService.getMediaService().upload(
                    EnumUtil.getDescriptionByCode(Integer.valueOf(fileType), FileType.class), SpringFileUtils.getFile(file)
            );

            if (StrUtil.isNotBlank(result.getMediaId())) {
                return new BasicResultVO(RespStatusEnum.SUCCESS, UploadResponseVo.builder().id(result.getMediaId()).build());
            }
            log.error("MaterialService#enterpriseWeChatMaterialUpload fail:{}", JSON.toJSONString(result));

        } catch (WxErrorException e) {
            log.error("MaterialService#enterpriseWeChatMaterialUpload fail:{}", Throwables.getStackTraceAsString(e));
        }

        return BasicResultVO.fail("未知错误,联系管理者");
    }
}
