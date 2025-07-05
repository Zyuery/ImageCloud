package com.zyuer.imagecloud.domain.dto.file;

import lombok.Data;

@Data
public class UploadPictureResult {

    private String url;
    //比Picture多出的一个属性，是上传文件的原始名称：由FileUtil.mainName(multipartFile.getOriginalFilename())而来。
    private String picName;
    private Long picSize;
    private int picWidth;
    private int picHeight;
    private Double picScale;
    private String picFormat;

}

