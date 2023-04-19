package com.project.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {

    private String password;
    private String confirmPassword;
}
