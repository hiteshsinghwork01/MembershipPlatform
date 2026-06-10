package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.UserCohort;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Phone Number can't be blank")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;

    private UserCohort cohort;
}
