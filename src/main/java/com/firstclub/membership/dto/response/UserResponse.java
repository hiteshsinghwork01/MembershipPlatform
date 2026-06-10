package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.UserCohort;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserCohort cohort;
    private boolean active;
}
