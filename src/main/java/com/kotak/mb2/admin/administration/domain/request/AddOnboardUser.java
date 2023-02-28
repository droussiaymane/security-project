package com.kotak.mb2.admin.administration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.domain.response.AccessListResponse;

import javax.validation.constraints.*;
import java.util.List;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.constants.RegexConstants.*;

public record AddOnboardUser(
        @JsonProperty("request") @NotBlank String deptId,
        @JsonProperty("access_list") @NotNull List<AccessListResponse> accessList,
        @JsonProperty("session_dept_id") String sessionDeptId,
        @JsonProperty("session_role") @NotBlank @Pattern(regexp = SESSION_ROLE, message = SESSION_ROLE_ERR)
        String sessionRole,
        @JsonProperty("session_username") @NotBlank @Pattern(regexp = USERNAME_REGEX, message = SESSION_USERNAME_ERR)
        @Size(min = 3,max=20) String sessionUsername) {

}
