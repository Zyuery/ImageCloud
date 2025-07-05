package com.zyuer.imagecloud.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zyuer.imagecloud.domain.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author zyuer
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-06-30 21:47:50
* @Entity com.zyuer.imagecloud.domain.pojo.user
*/
public interface UserMapper extends BaseMapper<User> {
//  int countUserIgnoreDeleted(@Param("ew") LambdaQueryWrapper<user> ew)
    int countUserIgnoreDeleted(@Param("userAccount" ) String userAccount);
}




