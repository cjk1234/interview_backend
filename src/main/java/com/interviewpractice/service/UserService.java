package com.interviewpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interviewpractice.entity.User;
import com.interviewpractice.dto.UserDTO;
import com.interviewpractice.dto.LoginDTO;

public interface UserService extends IService<User> {
    User register(UserDTO userDTO);
    String login(LoginDTO loginDTO);
    User getUserInfo(Long userId);
    User updateUserInfo(Long userId, UserDTO userDTO);
}
