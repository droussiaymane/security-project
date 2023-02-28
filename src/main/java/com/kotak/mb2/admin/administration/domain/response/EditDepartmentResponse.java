package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.config.ServiceProperties;
import lombok.Data;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Data
public class EditDepartmentResponse {
    private String deptId;
    private int statusCode;
    private String statusDesc;

    public static void setFailedStatusForEditDept(EditDepartmentResponse editDepartmentResponse) {
        editDepartmentResponse.setStatusCode(FAILED);
        editDepartmentResponse.setStatusDesc(FAILED_DESC);
    }

    public static void setFailedStatusForCannotEditOwnDept(EditDepartmentResponse editDepartmentResponse) {
        editDepartmentResponse.setStatusCode(FAILED);
        editDepartmentResponse.setStatusDesc(CANNOT_EDIT_OWN_DEPT);
    }

    public static void setFailedStatusForEditPendingDeptReq(EditDepartmentResponse editDepartmentResponse,
                                                            ServiceProperties serviceProperties) {
        editDepartmentResponse.setStatusCode(FAILED);
        editDepartmentResponse.setStatusDesc(serviceProperties.getMsgPendingApproval());
    }

    public static void setSuccessStatusForDeptEdited(EditDepartmentResponse editDepartmentResponse,
                                                     String deptId) {
        editDepartmentResponse.setDeptId(deptId);
        editDepartmentResponse.setStatusCode(SUCCESS);
        editDepartmentResponse.setStatusDesc(SUCCESS_DESC);
    }

    public static void setSuccessStatusForEditReqSentForApproval(EditDepartmentResponse editDepartmentResponse,
                                                                 String deptId,
                                                                 ServiceProperties serviceProperties) {
        editDepartmentResponse.setDeptId(deptId);
        editDepartmentResponse.setStatusCode(SUCCESS);
        editDepartmentResponse.setStatusDesc(serviceProperties.getMsgSentApproval());
    }

    public static void setFailedStatusDeptNotFoundForEdit(EditDepartmentResponse editDepartmentResponse) {
        editDepartmentResponse.setStatusCode(NOT_FOUND.value());
        editDepartmentResponse.setStatusDesc(DEPT_NOT_EXISTS);
    }
}
