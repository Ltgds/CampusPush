package com.ltgds.mypush.dao;

import com.ltgds.mypush.domain.SmsRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Li Guoteng
 * @data 2023/6/2
 * @description 短信记录Dao
 */
public interface SmsRecordDao extends CrudRepository<SmsRecord, Long> {

    /**
     * 通过日期和手机号发送记录
     * @param phone
     * @param sendDate
     * @return
     */
    List<SmsRecord> findByPhoneAndSendDate(Long phone, Integer sendDate);

}
