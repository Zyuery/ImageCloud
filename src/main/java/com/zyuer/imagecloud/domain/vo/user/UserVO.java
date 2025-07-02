package com.zyuer.imagecloud.domain.vo.user;

import lombok.Data;
import java.util.Date;
import java.io.Serializable;

@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String userAccount;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private String userRole;
    private Date createTime;
}

