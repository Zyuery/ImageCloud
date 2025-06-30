package com.zyuer.imagecloud.service;

import com.zyuer.imagecloud.domain.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zengyue
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-30 21:47:50
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

}
