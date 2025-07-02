package com.zyuer.imagecloud;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyuer.imagecloud.domain.pojo.User;
import com.zyuer.imagecloud.exception.BusinessException;
import com.zyuer.imagecloud.exception.ErrorCode;
import com.zyuer.imagecloud.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;


@Slf4j
@SpringBootTest
class ImageCloudApplicationTests {

    @Autowired
    private UserMapper userMapper;


    @Test
    void testQueryWrapper(){
       QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
               .select("id","userAccount","userPassword","userAvatar","userName","userProfile","userRole","createTime","updateTime","isDelete")
               .like("userAccount","z");
        List<User> users = userMapper.selectList(queryWrapper);
        for(User user : users){
            log.info(user.toString());
        }
    }

    @Test
    void testLambdaWrapper(){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .select(User::getId,User::getUserAccount,User::getUserPassword,User::getUserAvatar,User::getUserName,User::getUserProfile,User::getUserRole,User::getCreateTime,User::getUpdateTime,User::getIsDelete)
               .like(User::getUserAccount,"z");
        List<User> users = userMapper.selectList(queryWrapper);
        for(User user : users){
            log.info(user.toString());
        }
    }

    @Test
    void testCountIngore(){
        int count = userMapper.countUserIgnoreDeleted("ipsum veniam ex dsad id");
        if(count ==0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

//    @Test
//    void testUpdateUserAvatar(){
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
//                .eq(User::getUserAccount,"123zyuer");
//        int count = userMapper.updateUserAvatar(queryWrapper,"testAvator");
//        if(count>0){
//            log.info(userMapper.selectOne(queryWrapper).toString());
//        }
//    }

    @Test
    void contextLoads() {
    }


}
