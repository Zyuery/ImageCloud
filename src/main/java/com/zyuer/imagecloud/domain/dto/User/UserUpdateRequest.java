package com.zyuer.imagecloud.domain.dto.User;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private String userRole;
}
