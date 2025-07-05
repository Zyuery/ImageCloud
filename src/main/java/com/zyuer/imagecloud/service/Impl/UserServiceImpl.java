package com.zyuer.imagecloud.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyuer.imagecloud.annotation.AuthCheck;
import com.zyuer.imagecloud.domain.dto.user.UserQueryRequest;
import com.zyuer.imagecloud.domain.vo.user.UserVO;
import com.zyuer.imagecloud.utils.EncryptUtils;
import com.zyuer.imagecloud.common.UserConstant;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.domain.vo.user.LoginUserVO;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.exception.ThrowUtils;
import com.zyuer.imagecloud.service.UserService;
import com.zyuer.imagecloud.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
* @author zengyue
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-06-30 21:47:50
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    //注册
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
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

        long count = userMapper.countUserIgnoreDeleted(userAccount);
        ThrowUtils.throwIf(
                count>0,
                ErrorCode.PARAMS_ERROR,
                "该账号名已被使用过！");

        String encryptPassword = EncryptUtils.getEncryptPassword(userPassword);
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

    //登录
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        ThrowUtils.throwIf(
                StrUtil.hasBlank(userAccount,userPassword),
                ErrorCode.PARAMS_ERROR,
                "参数不能为空");
        ThrowUtils.throwIf(
                userAccount.length()<4||userPassword.length()<8,
                ErrorCode.PARAMS_ERROR,
                "账号或密码错误");
        String encryptPassword = EncryptUtils.getEncryptPassword(userPassword);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount,userAccount)
                .eq(User::getUserPassword,encryptPassword);
        User loginUser = this.baseMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(
                loginUser==null,
                ErrorCode.PARAMS_ERROR,
                "账号或密码错误");
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,loginUser);
        return getLoginUserVO(loginUser);
    }

    //获取登录用户信息（user）
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_LOGIN_ERROR);
        User loingUser = this.baseMapper.selectById(user.getId());
        ThrowUtils.throwIf(loingUser==null,ErrorCode.NOT_LOGIN_ERROR);
        return loingUser;
    }

    //用户登出
    @Override
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public boolean userLogout(HttpServletRequest request) {
        Object user = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_LOGIN_ERROR);
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    //User转loginUserVO
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user,loginUserVO);
        return loginUserVO;
    }


    //User转UserVO
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    //UserList转UserVOList
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    //UserQueryRequest转QueryWrapper
    @Override
    public QueryWrapper<User> getWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(
                userQueryRequest==null,
                ErrorCode.PARAMS_ERROR,
                "请求参数为空");
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

       return new QueryWrapper<User>()
                .eq(ObjUtil.isNotNull(id),"id",id)
                .eq(StrUtil.isNotBlank(userRole),"userRole",userRole)
                .like(StrUtil.isNotBlank(userAccount),"userAccount",userAccount)
                .like(StrUtil.isNotBlank(userName),"userName",userName)
                .like(StrUtil.isNotBlank(userProfile),"userProfile",userProfile)
                .orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder),sortField);
    }

    @Override
    public boolean isAdmin(User user) {
        ThrowUtils.throwIf(user==null,ErrorCode.PARAMS_ERROR,"参数为空");
        return UserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }
}




