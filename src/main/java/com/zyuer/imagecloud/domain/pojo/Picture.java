package com.zyuer.imagecloud.domain.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 图片
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String url;
    private String name;
    private String introduction;
    private String category;
    private String tags;
    private Long picSize;
    private Integer picWidth;
    private Integer picHeight;
    private Double picScale;
    private String picFormat;
    private Long userId;
    private Date createTime;
    private Date editTime;
    private Date updateTime;
    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;
    private String reviewMessage;
    private Long reviewerId;
    private Date reviewTime;
    @TableLogic
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}