package com.kotak.mb2.admin.administration.service;

import com.kotak.mb2.admin.administration.domain.request.CompletedActivitiesRequest;
import com.kotak.mb2.admin.administration.domain.response.CompletedActivity;
import com.kotak.mb2.admin.administration.domain.response.CustomerCompletedActivity;
import com.kotak.mb2.admin.administration.domain.response.DeptCompletedActivity;
import com.kotak.mb2.admin.administration.domain.response.UserCompletedActivity;
import com.kotak.mb2.admin.administration.repository.AdminUserDeptRolesRepository;
import com.kotak.mb2.admin.administration.repository.AdminUserMakerCheckerRepository;
import com.kotak.mb2.admin.administration.repository.CustomerMakerCheckerRepository;
import com.kotak.mb2.admin.administration.service.makerchecker.AdminDeptMakerCheckerManager;
import com.kotak.mb2.admin.administration.service.makerchecker.AdminUserMakerCheckerManager;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompletedActivityService {
    private final AdminDeptMakerCheckerManager adminDeptMakerCheckerManager;
    private final AdminUserMakerCheckerManager adminUserMakerCheckerManager;
    private final CustomerMakerCheckerRepository customerMakerCheckerRepository;

    public ApiResponse<List<CompletedActivity>> getDeptCompletedActivities(CompletedActivitiesRequest request) {
        try {
            log.debug("username:: {}", request.sessionUsername());
            List<CompletedActivity> completedActivities = adminDeptMakerCheckerManager.getCompletedActivities(request);
            return ApiResponse.success(completedActivities);
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ApiResponse<List<CompletedActivity>> getUserCompletedActivities(CompletedActivitiesRequest request) {
        try {
            log.debug("username:: {}", request.sessionUsername());
            final List<CompletedActivity> completedActivities = adminUserMakerCheckerManager.getCompletedActivities(
                    request);
            return ApiResponse.success(completedActivities);
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }
    public ApiResponse<List<CompletedActivity>> getCustomerCompletedActivities(CompletedActivitiesRequest request) {
        try {
            var completedActivities = customerMakerCheckerRepository.completedActivities(
                    request.sessionUsername());
            return ApiResponse.success(completedActivities.stream().map(CustomerCompletedActivity::from).toList());
        } catch (Exception e) {
            log.error("Exception", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }



}
