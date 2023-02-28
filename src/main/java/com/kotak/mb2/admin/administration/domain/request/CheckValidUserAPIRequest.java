package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.kotak.mb2.admin.administration.constants.AppConstants.USERNAME_ERR;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.USERNAME_REGEX;

public record CheckValidUserAPIRequest(
        @JsonProperty("username") @NotBlank @Size(min = 3,max=20)
        @Pattern(regexp = USERNAME_REGEX, message = USERNAME_ERR) String username) {
}
