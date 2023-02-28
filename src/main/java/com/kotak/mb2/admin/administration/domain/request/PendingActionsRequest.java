package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;

import static com.kotak.mb2.admin.administration.constants.AppConstants.SESSION_ROLE_ERR;
import static com.kotak.mb2.admin.administration.constants.AppConstants.SESSION_USERNAME_ERR;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.SESSION_ROLE;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.USERNAME_REGEX;

public record PendingActionsRequest(
        @JsonProperty("session_role") @NotBlank @Pattern(regexp = SESSION_ROLE, message = SESSION_ROLE_ERR)
        String sessionRole,
        @JsonProperty("session_username") @NotBlank @Pattern(regexp = USERNAME_REGEX, message = SESSION_USERNAME_ERR)
        @Size(min = 3,max=20) String sessionUsername,
        @JsonProperty("session_dept_name") String sessionDeptName) {
}
