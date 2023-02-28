package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Getter
public class ShowDetailsUserResponse {
    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("employee_code")
    private String employeeCode;

    @JsonProperty("department")
    private String department;

    @JsonProperty("role")
    private String role;

    @JsonProperty("status")
    private String status;

    @JsonProperty("action_type")
    private String actionType;

    @JsonProperty("action")
    private Action action;

    public static ShowDetailsUserResponse from(AdminUserMakerChecker adminUserMakerChecker, String type) {
        return ShowDetailsUserResponse.builder()
                .department(adminUserMakerChecker.getDept())
                .userName(adminUserMakerChecker.getUsername())
                .employeeCode(adminUserMakerChecker.getEmpCode())
                .role(adminUserMakerChecker.getRole())
                .status(Objects.isNull(adminUserMakerChecker.getStatus()) ? String.valueOf(adminUserMakerChecker.getAction()) :
                        adminUserMakerChecker.getStatus())
                .actionType(type)
                .action(adminUserMakerChecker.getAction())
                .build();

    }

}
