package com.zyuer.imagecloud.domain.dto.normal;

import lombok.Data;

@Data
public class PageRequest {
    private int pageNow = 1;
    private int pageSize = 10;
    //排序字段
    private String sortField;
    //排序规则（默认降序）
    private String sortOrder = "descend";
}
