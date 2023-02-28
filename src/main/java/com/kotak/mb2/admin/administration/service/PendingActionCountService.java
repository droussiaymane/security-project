package com.kotak.mb2.admin.administration.service;

import com.kotak.mb2.admin.administration.domain.enums.Role;
import com.kotak.mb2.admin.administration.domain.request.PendingActionsCountRequest;
import com.kotak.mb2.admin.administration.domain.response.PendingActionsCountResponse;
import com.kotak.mb2.admin.administration.service.makerchecker.AdminDeptMakerCheckerManager;
import com.kotak.mb2.admin.administration.service.makerchecker.AdminUserMakerCheckerManager;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingActionCountService {

    private final AdminUserMakerCheckerManager adminUserMakerCheckerManager;
    private final AdminDeptMakerCheckerManager adminDeptMakerCheckerManager;

    public ApiResponse<PendingActionsCountResponse> getPendingActionsCount(PendingActionsCountRequest request) {
        try {
            log.debug("UserName:: {}", request.sessionUsername());
            if (request.sessionRole().equalsIgnoreCase(Role.CHECKER.name())
                    && StringUtils.isEmpty(request.sessionDeptName())) {
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                        "session_dept_name " + SHOULD_NOT_BE_BLANK));
            }
            long userPendingActionsCount = adminUserMakerCheckerManager.getPendingActionsCount(request);
            long deptPendingActionsCount = adminDeptMakerCheckerManager.getPendingActionsCount(request);
            //long custPendingActionsCount = adminUserMakerCheckerManager.getCustomerPendingActionsCount(request);

            long otherRequestCount = userPendingActionsCount + deptPendingActionsCount;
            long blockCount = adminUserMakerCheckerManager.getCustomerPendingActionsCount(request, List.of(BLOCK));
            long unBlockCount = adminUserMakerCheckerManager.getCustomerPendingActionsCount(request, List.of(UNBLOCK));
            long activateCount = adminUserMakerCheckerManager.getCustomerPendingActionsCount(request, List.of(PHONE_ACTIVATE,BRANCH_ACTIVATE));
            long deactivateCount = adminUserMakerCheckerManager.getCustomerPendingActionsCount(request, List.of(DEACTIVATE));
            long mpinUnlockCount = adminUserMakerCheckerManager.getCustomerPendingActionsCount(request, List.of(MPIN_UNLOCK));

            //long totalCount = userPendingActionsCount + deptPendingActionsCount + custPendingActionsCount;
            long totalCount = otherRequestCount + blockCount + unBlockCount + activateCount + deactivateCount + mpinUnlockCount ;

            return ApiResponse.success(new PendingActionsCountResponse(totalCount, otherRequestCount,
                    blockCount, unBlockCount,activateCount,deactivateCount,mpinUnlockCount));
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }
}
