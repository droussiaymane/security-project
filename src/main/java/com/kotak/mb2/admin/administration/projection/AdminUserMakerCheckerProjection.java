package com.kotak.mb2.admin.administration.projection;

import java.time.OffsetDateTime;

public interface AdminUserMakerCheckerProjection {
    String getId();

    String getUsername();

    String getRole();

    String getStatus();

    String getDeptName();

    OffsetDateTime getCreatedAt();

    String getEmpCode();

    Integer getIsUpdated();
}
