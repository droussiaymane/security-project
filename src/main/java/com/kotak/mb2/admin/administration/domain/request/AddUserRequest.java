package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.*;

public record AddUserRequest(
        @JsonProperty("username") @NotBlank @Size(min = 3,max=20)
        @Pattern(regexp = USERNAME_REGEX, message = USERNAME_ERR)
        String username,
        @JsonProperty("role") @NotBlank @Pattern(regexp = SESSION_ROLE, message = ROLE_ERR) String role,
        @JsonProperty("dept_id") String deptId,
        @JsonProperty("session_role") @Pattern(regexp = SESSION_ROLE, message = SESSION_ROLE_ERR)
        @NotBlank String sessionRole,
        @JsonProperty("session_username") @NotBlank @Pattern(regexp = USERNAME_REGEX, message = SESSION_USERNAME_ERR)
        @Size(min = 3,max=20) String sessionUsername) {
}
