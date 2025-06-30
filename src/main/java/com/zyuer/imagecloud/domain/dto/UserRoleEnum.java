package com.zyuer.imagecloud.domain.dto;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;


@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;
    private final String value;
    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    //根据value获取enum枚举
    public static UserRoleEnum getUserRoleEnum(String value) {
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for(UserRoleEnum e : UserRoleEnum.values()) {
            if(e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
