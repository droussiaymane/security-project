package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.*;

public record showMenuDetailsRequest(
        @JsonProperty("id") @NotBlank String id,
        @JsonProperty("name") @NotBlank @Pattern(regexp = USERNAME_REGEX, message = NAME_ERR)
        String name,
        @JsonProperty("module_name") @NotBlank @Pattern(regexp = ALPHA_NUMERIC_REGEX,message = NAME_ERR)
        String moduleName,
        @JsonProperty("maker_date") String makerDate,
        @JsonProperty("session_username") @NotBlank @Pattern(regexp = USERNAME_REGEX, message = SESSION_USERNAME_ERR)
        @Size(min = 3,max=20) String sessionUsername
) {
}
