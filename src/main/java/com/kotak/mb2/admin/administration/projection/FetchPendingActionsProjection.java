package com.kotak.mb2.admin.administration.projection;

import java.time.OffsetDateTime;

public interface FetchPendingActionsProjection {
    String getId();
    OffsetDateTime getMakerDate();
    String getMaker();
    String getMakerComments();
    String getMakerStatus();
    String getDept();
    String getMenuCode();
    String getDescription();
    String getAction();
}
