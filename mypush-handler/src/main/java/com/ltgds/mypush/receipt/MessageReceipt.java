package com.ltgds.mypush.receipt;

import com.google.common.base.Throwables;
import com.ltgds.mypush.config.SupportThreadPoolConfig;
import com.ltgds.mypush.stater.ReceiptMessageStater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description 拉取回执信息 入口
 */
@Slf4j
@Component
public class MessageReceipt {

    @Autowired
    private List<ReceiptMessageStater> receiptMessageStaterList;

    /**
     * 通过轮询 拉取回执信息
     */
    @PostConstruct
    private void init() {
        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
            while (true) {
                try {
                    for (ReceiptMessageStater receiptMessageStater : receiptMessageStaterList) {
                        // receiptMessageStater.start();
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error("MessageReceipt#init fail:{}", Throwables.getStackTraceAsString(e));
                }
            }
        });
    }
}

