package com.ltgds.mypush.domain.push.getui;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.plaf.PanelUI;
import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/8/14
 * @description 批量推送消息的param
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchSendPushParam {

    @JSONField(name = "audience")
    private AudienceVO audience;

    /**
     * AudienceVo
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AudienceVO {
        @JSONField(name = "cid")
        private Set<String> cid;
    }

    @JSONField(name = "taskid")
    private String taskId;

    @JSONField(name = "is_async")
    private Boolean isAsync;
}
