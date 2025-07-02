package com.zyuer.imagecloud.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyuer.imagecloud.domain.dto.User.UserQueryRequest;
import com.zyuer.imagecloud.domain.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyuer.imagecloud.domain.vo.user.LoginUserVO;
import com.zyuer.imagecloud.domain.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zengyue
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-30 21:47:50
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String checkPassword);
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
    LoginUserVO getLoginUserVO(User user);
    User getLoginUser(HttpServletRequest request);
    boolean userLogout(HttpServletRequest request);
    UserVO getUserVO(User user);
    List<UserVO> getUserVOList(List<User> userList);
    QueryWrapper<User> getWrapper(UserQueryRequest userQueryRequest);
}
