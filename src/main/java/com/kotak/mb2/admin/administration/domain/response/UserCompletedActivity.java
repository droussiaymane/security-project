package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;

@SuperBuilder
@Getter
public class UserCompletedActivity extends CompletedActivity {
    private String dept;
    private String username;
    private String role;
    private String empCode;

    public static CompletedActivity from(AdminUserMakerChecker adminUserMakerChecker) {
        return UserCompletedActivity.builder()
                .module(ADMIN_USER)
                .dept(adminUserMakerChecker.getDept())
                .username(adminUserMakerChecker.getUsername())
                .role(adminUserMakerChecker.getRole())
                .empCode(adminUserMakerChecker.getEmpCode())
                .maker(adminUserMakerChecker.getMaker())
                .makerDate(adminUserMakerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")))
                .makerComments(adminUserMakerChecker.getMakerComments())
                .makerAction(getMakerAction(adminUserMakerChecker.getAction()))
                .authorizedDate(Objects.nonNull(adminUserMakerChecker.getCheckerDate())
                        ? adminUserMakerChecker.getCheckerDate().toString()
                        : Objects.nonNull(adminUserMakerChecker.getDeletedAt())
                        ? adminUserMakerChecker.getDeletedAt().toString() : null)
                .authorizedBy(Objects.nonNull(adminUserMakerChecker.getChecker())
                        ? adminUserMakerChecker.getChecker() : adminUserMakerChecker.getDeletedBy())
                .authorizedAction(getAuthorizedAction(adminUserMakerChecker.getStatus()))
                .authorizedComments(adminUserMakerChecker.getCheckerComments())
                .type("USER")
                .build();
    }

    private static String getMakerAction(Action action) {
        if (Objects.isNull(action)) return null;
        if (action.equals(Action.ADD)) return Action.ADD.name();
        if (action.equals(Action.EDIT)) return "UPD";
        if (action.equals(Action.INACTIVE)) return "DIS";
        return null;
    }

    private static String getAuthorizedAction(String status) {
        if (Objects.isNull(status)) return "WITH";
        if (status.equals(STATUS_ACTIVE) || status.equals(STATUS_DELETE)) return STATUS_ACTIVE;
        else return "REJ";
    }
}
