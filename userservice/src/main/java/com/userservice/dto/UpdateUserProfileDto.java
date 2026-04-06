package com.userservice.dto;

import lombok.Data;

@Data
public class UpdateUserProfileDto {

	
    private String fullName;
    private String avatarUrl;
    private String bio;
}