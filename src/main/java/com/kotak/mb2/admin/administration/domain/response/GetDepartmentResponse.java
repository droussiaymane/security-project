package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.projection.GetDepartmentProjection;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class GetDepartmentResponse {
    private String deptId;
    private String deptName;
    private String description;
    private String status;
    private String createdAt;
    private Long numberOfUsers;

    public static void getDepartmentResponse(List<GetDepartmentResponse> departmentResponses,
                                             GetDepartmentProjection projection) {
        final GetDepartmentResponse departmentResponse = new GetDepartmentResponse();
        departmentResponse.setDeptId(projection.getId());
        departmentResponse.setDeptName(projection.getDeptName());
        departmentResponse.setDescription(projection.getDeptDesc());
        departmentResponse.setCreatedAt(projection.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-M-YYYY HH:mm:ss")));
        departmentResponse.setStatus(projection.getStatusDesc());
        departmentResponse.setNumberOfUsers(projection.getUserCount());

        departmentResponses.add(departmentResponse);
    }
}
