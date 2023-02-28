package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.config.ServiceProperties;
import lombok.Data;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Data
public class DeleteDepartmentResponse {
    private String deptId;
    private int statusCode;
    private String statusDesc;

    public static void setFailedStatusForDeleteDept(DeleteDepartmentResponse deleteDepartmentResponse) {
        deleteDepartmentResponse.setStatusCode(FAILED);
        deleteDepartmentResponse.setStatusDesc(FAILED_DESC);
    }

    public static void setFailedStatusForCannotDeleteOwnDept(DeleteDepartmentResponse deleteDepartmentResponse) {
        deleteDepartmentResponse.setStatusCode(FAILED);
        deleteDepartmentResponse.setStatusDesc(CANNOT_EDIT_OWN_DEPT);
    }

    public static void setFailedStatusForDeletePendingDeptReq(DeleteDepartmentResponse deleteDepartmentResponse,
                                                              ServiceProperties serviceProperties) {
        deleteDepartmentResponse.setStatusCode(FAILED);
        deleteDepartmentResponse.setStatusDesc(serviceProperties.getMsgPendingApproval());
    }

    public static void setSuccessStatusForDeptDeleted(DeleteDepartmentResponse deleteDepartmentResponse,
                                                      String deptId) {
        deleteDepartmentResponse.setDeptId(deptId);
        deleteDepartmentResponse.setStatusCode(SUCCESS);
        deleteDepartmentResponse.setStatusDesc(SUCCESS_DESC);
    }

    public static void setSuccessStatusForDeleteReqSentForApproval(DeleteDepartmentResponse deleteDepartmentResponse,
                                                                   String deptId,
                                                                   ServiceProperties serviceProperties) {
        deleteDepartmentResponse.setDeptId(deptId);
        deleteDepartmentResponse.setStatusCode(SUCCESS);
        deleteDepartmentResponse.setStatusDesc(serviceProperties.getMsgSentApproval());
    }

    public static void setFailedStatusDeptNotFoundForDelete(DeleteDepartmentResponse deleteDepartmentResponse) {
        deleteDepartmentResponse.setStatusCode(NOT_FOUND.value());
        deleteDepartmentResponse.setStatusDesc(DEPT_NOT_EXISTS);
    }
}
