package org.example.vo_ai_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo_ai_service.entity.User;
import org.example.vo_ai_service.service.UserService;
import org.example.vo_ai_service.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Given
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-04-25 12:59:57
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




