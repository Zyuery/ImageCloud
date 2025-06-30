package com.zyuer.imagecloud.common;

import lombok.Data;

@Data
public class PageRequest {

    private int current = 1;
    private int pageSize = 10;
    //排序字段
    private String sortField;
    //排序规则（默认降序）
    private String sortOrder = "descend";
}
