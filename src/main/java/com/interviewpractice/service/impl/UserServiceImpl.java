package com.interviewpractice.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewpractice.dto.LoginDTO;
import com.interviewpractice.dto.UserDTO;
import com.interviewpractice.entity.User;
import com.interviewpractice.mapper.UserMapper;
import com.interviewpractice.service.UserService;
import com.interviewpractice.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User register(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (lambdaQuery().eq(User::getUsername, userDTO.getUsername()).count() > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (lambdaQuery().eq(User::getEmail, userDTO.getEmail()).count() > 0) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        save(user);
        return user;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        User user = lambdaQuery().eq(User::getUsername, loginDTO.getUsername()).one();
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return jwtUtil.generateToken(user.getId().toString());
    }

    @Override
    public User getUserInfo(Long userId) {
        return getById(userId);
    }

    @Override
    public User updateUserInfo(Long userId, UserDTO userDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        BeanUtils.copyProperties(userDTO, user);
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);

        return user;
    }
}
