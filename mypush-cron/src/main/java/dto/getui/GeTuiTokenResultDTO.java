package dto.getui;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/13
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeTuiTokenResultDTO {

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "code")
    private Integer code;

    @JSONField(name = "data")
    private DataDTO data;

    @Data
    @NoArgsConstructor
    public class DataDTO {

        @JSONField(name = "expire_time")
        private String expireTime;

        @JSONField(name = "token")
        private String token;
    }
}
