package com.zyuer.imagecloud.domain.dto.User;

import lombok.Data;

@Data
public class UserLoginRequest {
    private static final long serialVersionUID = 3191241716373120793L;
    private String userAccount;
    private String userPassword;
}
