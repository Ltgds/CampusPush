package com.ltgds.mypush.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Li Guoteng
 * @data 2023/7/31
 * @description 上传后成功返回素材的id
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseVo {

    private String id;
}
