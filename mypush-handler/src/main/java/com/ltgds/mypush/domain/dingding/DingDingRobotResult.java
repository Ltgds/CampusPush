package com.ltgds.mypush.domain.dingding;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/8/7
 * @description 钉钉群 自定义机器人返回结果
 *
 * 正常的返回：{"errcode":0, "errmsg":"ok"}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotResult {

    /**
     * errcode
     */
    @SerializedName("errcode")
    private Integer errCode;

    /**
     * errmsg
     */
    @SerializedName("errmsg")
    private String errMsg;
}
