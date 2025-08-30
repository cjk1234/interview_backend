package com.interviewpractice.controller;

import com.interviewpractice.dto.LoginDTO;
import com.interviewpractice.dto.UserDTO;
import com.interviewpractice.entity.User;
import com.interviewpractice.service.UserService;
import com.interviewpractice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserDTO userDTO) {
        User user = userService.register(userDTO);
        String token = jwtUtil.generateToken(user.getId().toString());

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(token));
        User user = userService.getUserInfo(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return result;
    }

    @GetMapping("/info")
    public User getUserInfo(HttpServletRequest request) {
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(request.getHeader("Authorization").substring(7)));
        return userService.getUserInfo(userId);
    }

    @PutMapping("/info")
    public User updateUserInfo(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        Long userId = Long.parseLong(jwtUtil.getUserIdFromToken(request.getHeader("Authorization").substring(7)));
        return userService.updateUserInfo(userId, userDTO);
    }
}
