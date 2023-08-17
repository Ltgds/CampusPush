package com.ltgds.mypush.web.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.constant.OfficialAccountParamConstant;
import com.ltgds.mypush.common.enums.RespStatusEnum;
import com.ltgds.mypush.utils.AccountUtils;
import com.ltgds.mypush.web.amis.CommonAmisVo;
import com.ltgds.mypush.web.annotation.MyPushAspect;
import com.ltgds.mypush.web.annotation.MyPushResult;
import com.ltgds.mypush.web.config.WeChatLoginConfig;
import com.ltgds.mypush.web.exception.CommonException;
import com.ltgds.mypush.web.utils.Convert4Amis;
import com.ltgds.mypush.web.utils.LoginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Li Guoteng
 * @data 2023/8/17
 * @description 微信服务号
 */
@Slf4j
@MyPushAspect
@RequestMapping("/officialAccount")
@RestController
@Api("微信服务号")
public class OfficialAccountController {

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private LoginUtils loginUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     *
     * @param id 账号
     * @return
     */
    @GetMapping("/template/list")
    @ApiOperation("/根据账号id获取模板列表")
    @MyPushResult
    public List<CommonAmisVo> queryList(Integer id) {

        try {
            List<CommonAmisVo> result = new ArrayList<>();
            WxMpService wxMpService = accountUtils.getAccountById(id, WxMpService.class);

            List<WxMpTemplate> allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
            for (WxMpTemplate wxMpTemplate : allPrivateTemplate) {
                CommonAmisVo commonAmisVo = CommonAmisVo.builder()
                        .label(wxMpTemplate.getTitle())
                        .value(wxMpTemplate.getTemplateId()).build();

                result.add(commonAmisVo);
            }
            return result;
        } catch (Exception e) {
            log.error("OfficialAccountController#queryList fail:{}", Throwables.getStackTraceAsString(e));
            throw new CommonException(RespStatusEnum.SERVICE_ERROR);
        }
    }

    /**
     * 根据账号id和模板id获取模板列表
     */
    @PostMapping("/detailTemplate")
    @ApiOperation("/根据账号id和模板id获取模板列表")
    @MyPushResult
    public CommonAmisVo queryDetailList(Integer id, String wxTemplateId) {
        if (Objects.isNull(id) || Objects.isNull(wxTemplateId)) {
            log.info("id || wxTemplateId null ! Id:{}, wxTemplateId:{}", id, wxTemplateId);
            return CommonAmisVo.builder().build();
        }

        try {
            WxMpService wxMpService = accountUtils.getAccountById(id, WxMpService.class);
            List<WxMpTemplate> allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
            return Convert4Amis.getWxMpTemplateParam(wxTemplateId, allPrivateTemplate);
        } catch (Exception e) {
            log.error("OfficialAccountController#queryDetailList fail:{}", Throwables.getStackTraceAsString(e));
            throw new CommonException(RespStatusEnum.SERVICE_ERROR);
        }
    }

    /**
     * 接收微信的事件消息
     *
     * 临时给微信服务号登录使用，正常消息推送平台不会有此接口
     */
    @RequestMapping(value = "/receipt", produces = {CommonConstant.CONTENT_TYPE_XML})
    @ApiOperation("/接收微信的事件消息")
    public String receiptMessage(HttpServletRequest request) {
        try {
            WeChatLoginConfig configService = loginUtils.getLoginConfig();

            if (Objects.isNull(configService)) {
                return RespStatusEnum.DO_NOT_NEED_LOGIN.getMsg();
            }
            WxMpService wxMpService = configService.getOfficialAccountLoginService();

            String echoStr = request.getParameter(OfficialAccountParamConstant.ECHO_STR);
            String signature = request.getParameter(OfficialAccountParamConstant.SIGNATURE);
            String nonce = request.getParameter(OfficialAccountParamConstant.NONCE);
            String timestamp = request.getParameter(OfficialAccountParamConstant.TIMESTAMP);

            //echoStr != null,说明只是微信的调试请求
            if (StrUtil.isNotBlank(echoStr)) {
                return echoStr;
            }

            if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
                return RespStatusEnum.CLIENT_BAD_PARAMETERS.getMsg();
            }

            String encryptType = StrUtil.isBlank(request.getParameter(OfficialAccountParamConstant.ENCRYPT_TYPE))
                    ? OfficialAccountParamConstant.RAW
                    : request.getParameter(OfficialAccountParamConstant.ENCRYPT_TYPE);

            if (OfficialAccountParamConstant.RAW.equals(encryptType)) {
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
                log.info("raw inMessage:{}", JSON.toJSONString(inMessage));

                WxMpXmlOutMessage outMessage = configService.getWxMpMessageRouter().route(inMessage);
                return outMessage.toXml();
            } else if (OfficialAccountParamConstant.AES.equals(encryptType)) {
                String msgSignature = request.getParameter(OfficialAccountParamConstant.MSG_SIGNATURE);
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), configService.getConfig(),
                        timestamp, nonce, msgSignature);
                log.info("aes InMessage:{}", JSON.toJSONString(inMessage));
                WxMpXmlOutMessage outMessage = configService.getWxMpMessageRouter().route(inMessage);
                return outMessage.toEncryptedXml(configService.getConfig());
            }

            return RespStatusEnum.SUCCESS.getMsg();
        } catch (Exception e) {
            log.error("OfficialAccountController#receiptMessage fail:{}", Throwables.getStackTraceAsString(e));
            return RespStatusEnum.SERVICE_ERROR.getMsg();
        }
    }
}
