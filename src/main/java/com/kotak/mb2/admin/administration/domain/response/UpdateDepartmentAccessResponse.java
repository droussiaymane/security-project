package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.config.ServiceProperties;
import lombok.Data;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Data
public class UpdateDepartmentAccessResponse {
    private String deptId;
    private int statusCode;
    private String statusDesc;

    public static void setFailedStatusForUpdateDeptAccess(UpdateDepartmentAccessResponse
                                                                  updateDepartmentAccessResponse) {
        updateDepartmentAccessResponse.setStatusCode(FAILED);
        updateDepartmentAccessResponse.setStatusDesc(FAILED_DESC);
    }

    public static void setFailedStatusForCannotEditOwnDeptAccess(UpdateDepartmentAccessResponse
                                                                         updateDepartmentAccessResponse) {
        updateDepartmentAccessResponse.setStatusCode(FAILED);
        updateDepartmentAccessResponse.setStatusDesc(CANNOT_EDIT_OWN_DEPT);
    }

    public static void setFailedStatusForDeptNotFound(UpdateDepartmentAccessResponse
                                                              updateDepartmentAccessResponse) {
        updateDepartmentAccessResponse.setStatusCode(NOT_FOUND.value());
        updateDepartmentAccessResponse.setStatusDesc(DEPT_NOT_EXISTS);
    }

    public static void setFailedStatusForEditPendingDeptAccessReq(UpdateDepartmentAccessResponse
                                                                          updateDepartmentAccessResponse,
                                                                  ServiceProperties serviceProperties) {
        updateDepartmentAccessResponse.setStatusCode(FAILED);
        updateDepartmentAccessResponse.setStatusDesc(serviceProperties.getMsgPendingApproval());
    }

    public static void setDeptIdForUpdateDeptAccess(UpdateDepartmentAccessResponse updateDepartmentAccessResponse,
                                                    String deptId) {
        updateDepartmentAccessResponse.setDeptId(deptId);
    }

    public static void setSuccessStatusForUpdateDeptAccess(UpdateDepartmentAccessResponse
                                                                   updateDepartmentAccessResponse) {
        updateDepartmentAccessResponse.setStatusCode(SUCCESS);
        updateDepartmentAccessResponse.setStatusDesc(DEPT_ACCESS_UPDATED);
    }

    public static void setSuccessStatusForDeptAccessReqSentForApproval(UpdateDepartmentAccessResponse
                                                                               updateDepartmentAccessResponse,
                                                                       ServiceProperties serviceProperties) {
        updateDepartmentAccessResponse.setStatusCode(SUCCESS);
        updateDepartmentAccessResponse.setStatusDesc(serviceProperties.getMsgSentApproval());
    }
}
