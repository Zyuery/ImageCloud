package com.zyuer.imagecloud.domain.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {
    private Long id;
    private static final long serialVersionUID = 1L;
}
