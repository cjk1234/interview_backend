package com.interviewpractice.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String code; // 可选：错误码

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "成功", data, null);
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        return new ApiResponse<>(false, message, null, code);
    }
}
