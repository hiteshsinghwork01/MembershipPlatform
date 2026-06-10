package com.firstclub.membership.util;

import com.firstclub.membership.dto.request.CreateUserRequest;
import com.firstclub.membership.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {

    public static User buildUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .cohort(request.getCohort())
                .active(true)
                .build();
    }

}
