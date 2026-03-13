package com.citymate.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username est obligatoire")
    private String username;

    @NotBlank(message = "Password est obligatoire")
    private String password;
}