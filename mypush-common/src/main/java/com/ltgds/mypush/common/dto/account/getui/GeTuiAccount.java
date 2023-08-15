package com.ltgds.mypush.common.dto.account.getui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/13
 * @description 创建个推账号时的元信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeTuiAccount {

    private String appId;

    private String appKey;

    private String masterSecret;
}
