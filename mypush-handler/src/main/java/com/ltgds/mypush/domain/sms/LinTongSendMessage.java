package com.ltgds.mypush.domain.sms;

import lombok.Builder;
import lombok.Data;

/**
 * @author Li Guoteng
 * @data 2023/8/11
 * @description
 *
 * <span>from File</span>
 * <p>Description</p>
 * <p>Company: QQ 752340543</p>
 */
@Data
@Builder
public class LinTongSendMessage {

    String phone;
    String content;
}
