package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.projection.AdminUserDeptRolesProjection;
import lombok.Builder;

public record UserListResponse(
        String username,
        String role,
        String status,
        Integer isUpdated) {

    @Builder
    public UserListResponse {
    }

    public static UserListResponse getUserListResponse(AdminUserDeptRolesProjection projection) {
        return UserListResponse.builder()
                .username(projection.getUsername())
                .role(projection.getRole())
                .status(projection.getStatus())
                .isUpdated(projection.getIsUpdated())
                .build();
    }
}
