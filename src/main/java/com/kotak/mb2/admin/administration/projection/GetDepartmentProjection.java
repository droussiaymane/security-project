package com.kotak.mb2.admin.administration.projection;

import java.time.OffsetDateTime;

public interface GetDepartmentProjection {
    String getId();

    String getDeptName();

    String getDeptDesc();

    OffsetDateTime getCreatedAt();

    String getStatusDesc();

    Long getUserCount();
}
