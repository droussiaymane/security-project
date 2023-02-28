package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import com.kotak.mb2.admin.administration.projection.CompletedActivityProjection;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;

@SuperBuilder
@Getter
public class DeptCompletedActivity extends CompletedActivity {
    private String dept;
    private String description;

    public static CompletedActivity from(AdminDepartmentMakerChecker adminDepartmentMakerChecker) {
        return DeptCompletedActivity.builder()
                .module(ADMIN_DEPT)
                .dept(adminDepartmentMakerChecker.getName())
                .maker(adminDepartmentMakerChecker.getMaker())
                .makerDate(adminDepartmentMakerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")))
                .makerComments(adminDepartmentMakerChecker.getMakerComments())
                .makerAction(getMakerAction(adminDepartmentMakerChecker.getAction()))
                .authorizedDate(Objects.nonNull(adminDepartmentMakerChecker.getCheckerDate())
                        ? adminDepartmentMakerChecker.getCheckerDate().toString()
                        : Objects.nonNull(adminDepartmentMakerChecker.getDeletedAt())
                        ? adminDepartmentMakerChecker.getDeletedAt().toString() : null)
                .authorizedBy(Objects.nonNull(adminDepartmentMakerChecker.getChecker())
                        ? adminDepartmentMakerChecker.getChecker() : adminDepartmentMakerChecker.getDeletedBy())
                .authorizedAction(getAuthorizedAction(adminDepartmentMakerChecker.getStatus()))
                .authorizedComments(adminDepartmentMakerChecker.getCheckerComments())
                .type("DEPT")
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
