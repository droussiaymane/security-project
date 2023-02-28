package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.constants.AppConstants;
import com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.enums.Action.ADD;
import static com.kotak.mb2.admin.administration.domain.enums.Action.INACTIVE;

@SuperBuilder
@Getter
public class UserPendingAction extends PendingAction {
    @JsonProperty("username")
    private String username;
    @JsonProperty("emp_code")
    private String empCode;
    @JsonProperty("dept")
    private String dept;
    @JsonProperty("role")
    private String role;

    public static UserPendingAction from(AdminUserMakerChecker userMakerChecker) {
        final UserPendingActionBuilder<?, ?> userPendingActionBuilder = UserPendingAction.builder()
                .moduleName(ADMIN_USER)
                .id(userMakerChecker.getId())
                .username(userMakerChecker.getUsername())
                .empCode(userMakerChecker.getEmpCode())
                .dept(userMakerChecker.getDept())
                .role(userMakerChecker.getRole())
                .maker(userMakerChecker.getMaker())
                .makerDate(userMakerChecker.getMakerDate().toString());

        setActionTypeAndStatus(userMakerChecker, userPendingActionBuilder);
        return userPendingActionBuilder.build();
    }

    private static void setActionTypeAndStatus(AdminUserMakerChecker userMakerChecker,
                                               UserPendingActionBuilder<?, ?> userPendingActionBuilder) {
        final Action action = userMakerChecker.getAction();
        if (STATUS_PENDING.equals(userMakerChecker.getMakerStatus())) {
            if (action.equals(ADD)) {
                userPendingActionBuilder.actionType(ADDED);
            } else if (action.equals(INACTIVE)) {
                userPendingActionBuilder.actionType(UPDATED);
                userPendingActionBuilder.status(AppConstants.INACTIVE);
            } else {
                userPendingActionBuilder.actionType(UPDATED);
                userPendingActionBuilder.status(ACTIVE);
            }
        } else {
            userPendingActionBuilder.actionType(DELETED);
        }
    }
}
