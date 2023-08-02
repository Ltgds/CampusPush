package com.ltgds.mypush.web.controller;

import cn.hutool.core.util.StrUtil;
import com.ltgds.mypush.common.constant.PushConstant;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import com.ltgds.mypush.web.amis.CommonAmisVo;
import com.ltgds.mypush.web.annotation.MyPushAspect;
import com.ltgds.mypush.web.annotation.MyPushResult;
import com.ltgds.mypush.web.service.ChannelAccountService;
import com.ltgds.mypush.web.utils.Convert4Amis;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Li Guoteng
 * @data 2023/8/2
 * @description 渠道账号管理接口
 */
@Slf4j
@MyPushAspect
@MyPushResult
@RestController
@RequestMapping("/account")
@Api("渠道账号管理接口")
public class ChannelAccountController {
    @Autowired
    private ChannelAccountDao channelAccountDao;

    @Autowired
    private ChannelAccountService channelAccountService;

    /**
     * 如果id存在,则修改
     * 如果id不存在,则保存
     * @param channelAccount
     * @return
     */
    @PostMapping("/save")
    @ApiOperation("/保存数据")
    public ChannelAccount saveOrUpdate(@RequestBody ChannelAccount channelAccount) {

        channelAccount.setCreator(StrUtil.isBlank(channelAccount.getCreator()) ? PushConstant.DEFAULT_CREATOR : channelAccount.getCreator());
        return channelAccountService.save(channelAccount);
    }

    /**
     * 根据渠道标识查询账号相关的信息
     * @param channelType
     * @param creator
     * @return
     */
    @GetMapping("/queryByChannelType")
    @ApiOperation("/根据渠道标识查询账号相关记录")
    public List<CommonAmisVo> query(Integer channelType, String creator) {

        creator = StrUtil.isBlank(creator) ? PushConstant.DEFAULT_CREATOR : creator;

        List<ChannelAccount> channelAccounts = channelAccountService.queryByChannelType(channelType, creator);

        return Convert4Amis.getChannelAccountVo(channelAccounts, channelType);
    }

    /**
     * 所有的渠道账号信息
     * @param creator
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("/渠道账号列表信息")
    public List<ChannelAccount> list(String creator) {
        creator = StrUtil.isBlank(creator) ? PushConstant.DEFAULT_CREATOR : creator;

        return channelAccountService.list(creator);
    }

    /**
     * 根据id删除
     * id使用 , 分隔开
     * @param id
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("/根据Ids删除")
    public void deleteByIds(@PathVariable("id")String id) {
        if (StrUtil.isNotBlank(id)) {
            List<Long> idList = Arrays.stream(id.split(StrUtil.COMMA)).map(Long::valueOf).collect(Collectors.toList());
            channelAccountService.deleteByIds(idList);
        }
    }
}
