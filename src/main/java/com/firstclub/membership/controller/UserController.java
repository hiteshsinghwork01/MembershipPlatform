package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreateUserRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.UserResponse;
import com.firstclub.membership.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", userService.register(request)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUser(userId)));
    }

    @DeleteMapping("/{userId}/deregister")
    public ResponseEntity<ApiResponse<UserResponse>> deregister(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success("User deregistered successfully", userService.deregister(userId)));
    }
}
