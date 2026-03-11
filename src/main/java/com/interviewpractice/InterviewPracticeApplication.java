package com.interviewpractice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.interviewpractice.mapper")
public class InterviewPracticeApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterviewPracticeApplication.class, args);
    }
}
