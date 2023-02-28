package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;

import static com.kotak.mb2.admin.administration.constants.AppConstants.SESSION_USERNAME_ERR;
import static com.kotak.mb2.admin.administration.constants.AppConstants.USERNAME_ERR;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.USERNAME_REGEX;


public record CheckValidUserRequest(
        @JsonProperty("username") @NotBlank @Size(min = 3,max=20)
        @Pattern(regexp = USERNAME_REGEX, message = USERNAME_ERR) String username,
        @JsonProperty("session_username")
        @NotBlank @Pattern(regexp = USERNAME_REGEX, message = SESSION_USERNAME_ERR)
        @Size(min = 3,max=20) String sessionUsername) {
}
