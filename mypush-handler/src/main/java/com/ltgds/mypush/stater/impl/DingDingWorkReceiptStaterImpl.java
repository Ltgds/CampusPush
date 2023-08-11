package com.ltgds.mypush.stater.impl;

import com.ltgds.mypush.common.constant.CommonConstant;
import com.ltgds.mypush.common.enums.ChannelType;
import com.ltgds.mypush.dao.ChannelAccountDao;
import com.ltgds.mypush.domain.ChannelAccount;
import com.ltgds.mypush.handler.impl.DingDingWorkNoticeHandler;
import com.ltgds.mypush.stater.ReceiptMessageStater;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 拉取 钉钉工作消息的回执
 */
public class DingDingWorkReceiptStaterImpl implements ReceiptMessageStater {

    @Autowired
    private DingDingWorkNoticeHandler workNoticeHandler;

    @Autowired
    private ChannelAccountDao channelAccountDao;

    @Override
    public void start() {
        List<ChannelAccount> accountList = channelAccountDao.findAllByIsDeletedEqualsAndSendChannelEquals(CommonConstant.FALSE, ChannelType.DING_DING_WORK_NOTICE.getCode());

        for (ChannelAccount channelAccount : accountList) {
            workNoticeHandler.pull(channelAccount.getId());
        }
    }
}
