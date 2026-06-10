package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.UserResponse;
import com.firstclub.membership.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .cohort(user.getCohort())
                .active(user.isActive())
                .build();
    }
}
