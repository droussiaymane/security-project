package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.*;

public record EditUserRequest(
        @JsonProperty("username") @NotBlank @Size(min = 3,max=20)
        @Pattern(regexp = USERNAME_REGEX, message = USERNAME_ERR) String username,
        @JsonProperty("dept") @Pattern(regexp = ALPHA_NUMERIC_REGEX, message = DEPT_NAME_ERR)
        String dept,
        @JsonProperty("role") @NotBlank @Pattern(regexp = SESSION_ROLE, message = ROLE_ERR) String role,
        @JsonProperty("session_role") @NotBlank @Pattern(regexp = SESSION_ROLE, message = SESSION_ROLE_ERR)
        String sessionRole,
        @JsonProperty("session_username") @NotBlank @Pattern(regexp = USERNAME_REGEX, message = SESSION_USERNAME_ERR)
        @Size(min = 3,max=20) String sessionUsername) {
}
