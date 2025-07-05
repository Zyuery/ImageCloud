package com.zyuer.imagecloud.aop;

import com.zyuer.imagecloud.annotation.AuthCheck;
import com.zyuer.imagecloud.domain.dto.user.UserRoleEnum;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.exception.ThrowUtils;
import com.zyuer.imagecloud.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthIntercepter {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if(mustRoleEnum == null){
            return joinPoint.proceed();
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User user = userService.getLoginUser(request);
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(user.getUserRole());

        ThrowUtils.throwIf(
                userRoleEnum==null,
                ErrorCode.NO_AUTH_ERROR
        );
        ThrowUtils.throwIf(
                UserRoleEnum.USER.equals(mustRoleEnum)
                &&!(UserRoleEnum.USER.equals(userRoleEnum)||UserRoleEnum.ADMIN.equals(userRoleEnum)),
                ErrorCode.NO_AUTH_ERROR
        );
        ThrowUtils.throwIf(
                UserRoleEnum.ADMIN.equals(mustRoleEnum)
                &&!UserRoleEnum.ADMIN.equals(userRoleEnum),
                ErrorCode.NO_AUTH_ERROR
        );
        return joinPoint.proceed();

    }
}
