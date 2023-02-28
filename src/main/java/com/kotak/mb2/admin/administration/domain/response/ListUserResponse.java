package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.projection.AdminUserMakerCheckerProjection;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public record ListUserResponse(
        @JsonProperty("user_id") String userId,
        @JsonProperty("username") String username,
        @JsonProperty("role") String role,
        @JsonProperty("dept_name") String deptName,
        @JsonProperty("status") String status,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("emp_code") String empCode,
        @JsonProperty("is_updated") Integer isUpdated) {

    @Builder
    public ListUserResponse {
    }

    public static ListUserResponse getUserResponse(AdminUserMakerCheckerProjection projection) {
        return ListUserResponse.builder()
                .userId(projection.getId())
                .username(projection.getUsername())
                .role(projection.getRole())
                .status(projection.getStatus())
                .deptName(projection.getDeptName())
                .createdAt(projection.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-M-YYYY HH:mm:ss")))
                .empCode(projection.getEmpCode())
                .isUpdated(projection.getIsUpdated())
                .build();
    }
}
