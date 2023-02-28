package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.config.ServiceProperties;
import lombok.Data;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Data
public class ActivateDelDepartmentResponse {
    private String deptId;
    private int statusCode;
    private String statusDesc;

    public static void setFailedStatusForActivateDept(ActivateDelDepartmentResponse activateDelDepartmentResponse) {
        activateDelDepartmentResponse.setStatusCode(FAILED);
        activateDelDepartmentResponse.setStatusDesc(FAILED_DESC);
    }

    public static void setFailedStatusForCannotActivateOwnDept(ActivateDelDepartmentResponse activateDelDepartmentResponse) {
        activateDelDepartmentResponse.setStatusCode(FAILED);
        activateDelDepartmentResponse.setStatusDesc(CANNOT_EDIT_OWN_DEPT);
    }

    public static void setFailedStatusForActivatePendingDeptReq(
            ActivateDelDepartmentResponse activateDelDepartmentResponse,
            ServiceProperties serviceProperties) {
        activateDelDepartmentResponse.setStatusCode(FAILED);
        activateDelDepartmentResponse.setStatusDesc(serviceProperties.getMsgPendingApproval());
    }

    public static void setFailedStatusForPendingDeptReq(ActivateDelDepartmentResponse activateDelDepartmentResponse) {
        activateDelDepartmentResponse.setStatusCode(FAILED);
        activateDelDepartmentResponse.setStatusDesc(PENDING_DEPT_REQ);
    }

    public static void setSuccessStatusForActivateDept(ActivateDelDepartmentResponse activateDelDepartmentResponse,
                                                       String deptId) {
        activateDelDepartmentResponse.setDeptId(deptId);
        activateDelDepartmentResponse.setStatusCode(SUCCESS);
        activateDelDepartmentResponse.setStatusDesc(SUCCESS_DESC);
    }

    public static void setSuccessStatusForActivateDeptUpdated(ActivateDelDepartmentResponse activateDelDepartmentResponse,
                                                              String deptId) {
        activateDelDepartmentResponse.setDeptId(deptId);
        activateDelDepartmentResponse.setStatusCode(SUCCESS);
        activateDelDepartmentResponse.setStatusDesc(DEPT_STATUS_UPDATED);
    }

    public static void setSuccessStatusForActivateReqSentForApproval(ActivateDelDepartmentResponse activateDelDepartmentResponse,
                                                                     String deptId,
                                                                     ServiceProperties serviceProperties) {
        activateDelDepartmentResponse.setDeptId(deptId);
        activateDelDepartmentResponse.setStatusCode(SUCCESS);
        activateDelDepartmentResponse.setStatusDesc(serviceProperties.getMsgSentApproval());
    }

    public static void setFailedStatusDeptNotFoundForActivateDelDept(ActivateDelDepartmentResponse
                                                                             activateDelDepartmentResponse) {
        activateDelDepartmentResponse.setStatusCode(NOT_FOUND.value());
        activateDelDepartmentResponse.setStatusDesc(DEPT_NOT_EXISTS);
    }
}
