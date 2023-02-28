package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@Getter
public class ShowDetailsDeptResponse {
    @JsonProperty("department_name")
    private String departmentName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("action_type")
    private String actionType;

    @JsonProperty("action")
    private Action action;

    public static ShowDetailsDeptResponse from(AdminDepartmentMakerChecker adminDepartmentMakerChecker, String type) {
        return ShowDetailsDeptResponse.builder()
                .departmentName(adminDepartmentMakerChecker.getName())
                .description(adminDepartmentMakerChecker.getDescription())
                .status(Objects.isNull(adminDepartmentMakerChecker.getStatus()) ? String.valueOf(adminDepartmentMakerChecker.getAction()) :
                        adminDepartmentMakerChecker.getStatus())
                .actionType(type)
                .action(adminDepartmentMakerChecker.getAction())
                .build();

    }
}
