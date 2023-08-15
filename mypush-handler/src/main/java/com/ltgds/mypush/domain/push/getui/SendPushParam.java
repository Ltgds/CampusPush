package com.ltgds.mypush.domain.push.getui;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Li Guoteng
 * @data 2023/8/14
 * @description 推送消息的param
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendPushParam {

    @JSONField(name = "request_id")
    private String requestId;

    @JSONField(name = "settings")
    private SettingsVO settings;

    /**
     * SettingsVo
     */
    @Data
    @NoArgsConstructor
    public class SettingsVO {
        @JSONField(name = "ttl")
        private Integer ttl;
    }

    @JSONField(name = "audience")
    private AudienceVO audience;

    /**
     * AudienceVo
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class AudienceVO {
        @JSONField(name = "cid")
        private Set<String> cid;
    }

    @JSONField(name = "push_message")
    private PushMessageVO pushMessage;

    /**
     * PushMessageVO
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class PushMessageVO {
        /**
         * notification
         */
        @JSONField(name = "notification")
        private NotificationVO notification;

        /**
         * NotificationVO
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class NotificationVO {
            /**
             * title
             */
            @JSONField(name = "title")
            private String title;
            /**
             * body
             */
            @JSONField(name = "body")
            private String body;
            /**
             * clickType
             */
            @JSONField(name = "click_type")
            private String clickType;
            /**
             * url
             */
            @JSONField(name = "url")
            private String url;
        }
    }
}
