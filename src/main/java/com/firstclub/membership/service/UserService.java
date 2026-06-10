package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreateUserRequest;
import com.firstclub.membership.dto.response.UserResponse;

public interface UserService {

    UserResponse register(CreateUserRequest request);

    UserResponse getUser(Long userId);

    UserResponse deregister(Long userId);
}
