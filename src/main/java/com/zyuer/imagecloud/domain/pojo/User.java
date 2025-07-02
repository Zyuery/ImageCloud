package com.zyuer.imagecloud.domain.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value ="user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;
    private String userAccount;
    private String userPassword;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private String userRole;
    private Date editTime;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDelete;
}