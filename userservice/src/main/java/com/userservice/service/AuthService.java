package com.userservice.service;

import com.userservice.dto.ChangePasswordDto;
import com.userservice.dto.RegisterResponseDto;
import com.userservice.dto.RegisterUserDto;
import com.userservice.dto.UpdateUserProfileDto;
import com.userservice.dto.UserProfileDto;
import com.userservice.entity.User;
import java.util.List;

public interface AuthService {


	RegisterResponseDto register(RegisterUserDto registerUserDto);

    String login(String userName, String password);

    void logout(String token);

    String refreshToken(String token);


    UserProfileDto getUserById(Integer userId);

    UserProfileDto updateProfile(Integer userId, UpdateUserProfileDto dto);

    void changePassword(Integer userId, ChangePasswordDto dto);

    List<User> searchUsers(String username);


	
	     
}
