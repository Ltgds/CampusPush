package dto.getui;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/13
 * @description 请求token时的参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryTokenParamDTO {

    @JSONField(name = "sign")
    private String sign;

    @JSONField(name = "timestamp")
    private String timestamp;

    @JSONField(name = "appkey")
    private String appKey;

}
