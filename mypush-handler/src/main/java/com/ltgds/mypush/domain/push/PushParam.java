package com.ltgds.mypush.domain.push;

import com.ltgds.mypush.common.domain.TaskInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/14
 * @description push的参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushParam {

    /**
     * 调用接口时需要的token
     */
    private String token;

    /**
     * 调用接口时需要的appId
     */
    private String appId;

    /**
     * 消息模板的信息
     */
    private TaskInfo taskInfo;
}
