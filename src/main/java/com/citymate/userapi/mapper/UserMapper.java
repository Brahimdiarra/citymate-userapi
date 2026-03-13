package com.citymate.userapi.mapper;

import com.citymate.userapi.dto.RegisterRequest;
import com.citymate.userapi.dto.UserDTO;
import com.citymate.userapi.entity.User;

public class UserMapper {

    public static User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setProfileType(request.getProfileType());
        user.setIsActive(true);
        user.setIsVerified(false);
        return user;
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(user);
    }

    public static UserDTO toResponse(User user) {
        return toDTO(user);
    }
}