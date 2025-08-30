package com.interviewpractice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private String avatarUrl;
    private String school;
    private String major;
    private String grade;
    private String resumeUrl;
}
