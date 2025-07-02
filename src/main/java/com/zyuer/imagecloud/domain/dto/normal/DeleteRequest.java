package com.zyuer.imagecloud.domain.dto.normal;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {
    private Long id;
    private static final long serialVersionUID = 1L;
}

