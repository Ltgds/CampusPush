package com.ltgds.mypush.common.dto.account.sms;

import lombok.*;

/**
 * @author Li Guoteng
 * @data 2023/8/10
 * @description
 * @EqualsAndHashCode(callSuper = true): 根据子类自身的字段值和从父类继承的字段值 来生成hashcode，
 * 当两个子类对象比较时，只有子类对象的本身的字段值和继承父类的字段值都相同，equals方法的返回值是true。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinTongSmsAccount extends SmsAccount {

    /**
     * api相关
     */
    private String url;

    /**
     * 账号相关
     */
    private String userName;

    private String password;

    /**
     * 标识渠道商id
     */
    private Integer supplierId;

    /**
     * 标识渠道商名字
     */
    private String supplierName;
}
