package com.collabservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinSessionRequest {

    @NotNull
    private Integer userId;

    @NotBlank
    private String role;

    private String sessionPassword;
}