package com.zyuer.imagecloud.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyuer.imagecloud.common.EncryptUtils;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.exception.ThrowUtils;
import com.zyuer.imagecloud.service.UserService;
import com.zyuer.imagecloud.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author zengyue
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-06-30 21:47:50
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        ThrowUtils.throwIf(
                StrUtil.hasBlank(userAccount,userPassword,checkPassword),
                ErrorCode.PARAMS_ERROR,
                "参数不能为空");
        ThrowUtils.throwIf(
                userAccount.length()<4,
                ErrorCode.PARAMS_ERROR,
                "用户账号过短");
        ThrowUtils.throwIf(
                userPassword.length()<8 && checkPassword.length()<8,
                ErrorCode.PARAMS_ERROR,
                "用户密码过短");
        ThrowUtils.throwIf(
                !userPassword.equals(checkPassword),
                ErrorCode.PARAMS_ERROR,
                "两次密码不一致");
        //2.查重
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(
                count>0,
                ErrorCode.PARAMS_ERROR,
                "该用户名已存在！");
        //3.加密
        String encryptPassword = EncryptUtils.getEncryptPassword(userPassword);
        //4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("用户还没取昵称~");
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(
                !saveResult,
                ErrorCode.SYSTEM_ERROR,
                "注册失败，数据库错误");
        return count;
    }



}




