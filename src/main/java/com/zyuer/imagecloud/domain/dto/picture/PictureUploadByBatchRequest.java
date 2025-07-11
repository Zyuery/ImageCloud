package com.zyuer.imagecloud.domain.dto.picture;

import lombok.Data;

@Data
public class PictureUploadByBatchRequest {
    private String searchText;
    private String namePrefix;
    private Integer count = 10;
}
