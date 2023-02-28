package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import com.kotak.mb2.admin.administration.projection.FetchPendingActionsProjection;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;

@SuperBuilder
@Getter
public class DeptPendingAction extends PendingAction {
    @JsonProperty("dept")
    private String dept;
    @JsonProperty("desc")
    private String desc;
    @JsonProperty("maker_comments")
    private String makerComments;
    @JsonProperty("menu_code")
    private String menuCode;

    public static DeptPendingAction from(AdminDepartmentMakerChecker makerChecker) {
        return DeptPendingAction.builder()
                .moduleName(ADMIN_DEPT)
                .id(makerChecker.getId())
                .maker(makerChecker.getMaker())
                .makerDate(makerChecker.getMakerDate().toString())
                .actionType(getActionTypeFromMakerChecker(makerChecker))
                .status(makerChecker.getMakerStatus().equalsIgnoreCase(STATUS_PENDING)
                        ? STATUS_ACTIVE : STATUS_DELETE)
                .dept(makerChecker.getName())
                .desc(makerChecker.getDescription())
                .build();
    }

    private static String getActionTypeFromMakerChecker(AdminDepartmentMakerChecker makerChecker) {
        if (makerChecker.getMakerStatus().equalsIgnoreCase(STATUS_PENDING_DELETE)) {
            return DELETED;
        } else if (makerChecker.getMakerStatus().equalsIgnoreCase(STATUS_PENDING)
                && makerChecker.getAction().equals(Action.ADD)) {
            return ADDED;
        } else if (makerChecker.getMakerStatus().equalsIgnoreCase(STATUS_PENDING)
                && makerChecker.getAction().equals(Action.EDIT)) {
            return UPDATED;
        } else {
            return null;
        }
    }

}
