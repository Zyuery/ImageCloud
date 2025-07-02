package com.zyuer.imagecloud.utils;


import org.springframework.util.DigestUtils;

public class EncryptUtils {
    public static String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "zyuer";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }
}
