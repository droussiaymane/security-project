package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.config.ServiceProperties;
import lombok.Data;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;

@Data
public class AddDepartmentResponse {
    private String deptId;
    private int statusCode;
    private String statusDesc;

    public static void setFailedStatusForAddDept(AddDepartmentResponse addDepartmentResponse) {
        addDepartmentResponse.setStatusCode(FAILED);
        addDepartmentResponse.setStatusDesc(FAILED_DESC);
    }

    public static void setFailedStatusForDeptAlreadyExists(AddDepartmentResponse addDepartmentResponse) {
        addDepartmentResponse.setStatusCode(FAILED);
        addDepartmentResponse.setStatusDesc(DEPT_ALREADY_EXIST);
    }

    public static void setFailedStatusForAddPendingDeptReq(AddDepartmentResponse addDepartmentResponse,
                                                           ServiceProperties serviceProperties) {
        addDepartmentResponse.setStatusCode(FAILED);
        addDepartmentResponse.setStatusDesc(serviceProperties.getMsgPendingApproval());
    }

    public static void setSuccessStatusForDeptAdded(AddDepartmentResponse addDepartmentResponse,
                                                    String deptId) {
        addDepartmentResponse.setDeptId(deptId);
        addDepartmentResponse.setStatusCode(SUCCESS);
        addDepartmentResponse.setStatusDesc(DEPT_ADDED);
    }

    public static void setSuccessStatusForAddReqSentForApproval(AddDepartmentResponse addDepartmentResponse,
                                                                String deptId,
                                                                ServiceProperties serviceProperties) {
        addDepartmentResponse.setDeptId(deptId);
        addDepartmentResponse.setStatusCode(SUCCESS);
        addDepartmentResponse.setStatusDesc(serviceProperties.getMsgSentApproval());
    }
}
