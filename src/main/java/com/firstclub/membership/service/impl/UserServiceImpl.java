package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreateUserRequest;
import com.firstclub.membership.dto.response.UserResponse;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.mapper.UserMapper;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.UserService;
import com.firstclub.membership.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMembershipRepository membershipRepository;

    @Override
    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new MembershipException("User already registered with email: " + request.getEmail());
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new MembershipException("User already registered with phone: " + request.getPhoneNumber());
        }

        return UserMapper.toResponse(userRepository.save(UserUtil.buildUser(request)));
    }

    @Override
    public UserResponse getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    @Override
    public UserResponse deregister(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!user.isActive()) {
            throw new MembershipException("User " + userId + " is already deregistered");
        }

        membershipRepository.findByUser_IdAndStatus(userId, MembershipStatus.ACTIVE)
                .ifPresent(membership -> {
                    membership.setStatus(MembershipStatus.CANCELLED);
                    membership.setCancellationReason("User deregistered");
                    membershipRepository.save(membership);
                });

        user.setActive(false);
        return UserMapper.toResponse(userRepository.save(user));
    }
}
