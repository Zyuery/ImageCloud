package com.zyuer.imagecloud.domain.dto.picture;

import com.zyuer.imagecloud.domain.dto.normal.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private String name;
    private String introduction;
    private String category;
    private List<String> tags;
    private Long picSize;
    private Integer picWidth;
    private Integer picHeight;
    private Double picScale;
    private String picFormat;
    private String searchText;
    private Long userId;
    private static final long serialVersionUID = 1L;
}

