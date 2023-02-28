package com.kotak.mb2.admin.administration.service.makerchecker;

import com.kotak.mb2.admin.administration.domain.entity.AdminUserDeptRoles;
import com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.CompletedActivity;
import com.kotak.mb2.admin.administration.domain.response.UserCompletedActivity;
import com.kotak.mb2.admin.administration.domain.response.UserPendingAction;
import com.kotak.mb2.admin.administration.repository.AdminUserDeptRolesRepository;
import com.kotak.mb2.admin.administration.repository.AdminUserMakerCheckerRepository;
import com.kotak.mb2.admin.administration.repository.CustomerMakerCheckerRepository;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker.updateAdminUserMakerChecker;
import static com.kotak.mb2.admin.administration.domain.enums.Role.CHECKER;
import static com.kotak.mb2.admin.administration.domain.enums.Role.MAKER;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminUserMakerCheckerManager implements BaseMakerCheckerManager<UserPendingAction, UserCompletedActivity> {

    private final AdminUserDeptRolesRepository adminUserDeptRolesRepository;
    private final AdminUserMakerCheckerRepository adminUserMakerCheckerRepository;
    private final CustomerMakerCheckerRepository customerMakerCheckerRepository;

    @Override
    public ApiResponse<Void> approve(ApproveRequest request) {
        final Optional<AdminUserMakerChecker> adminUserMakerCheckerOptional =
                adminUserMakerCheckerRepository.findById(request.id());
        final String sessionUsername = request.sessionUsername();

        if (adminUserMakerCheckerOptional.isEmpty()) {
            log.info("User not found {}", sessionUsername);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                    USER_NOT_FOUND));
        }
        final AdminUserMakerChecker adminUserMakerChecker = adminUserMakerCheckerOptional.get();

        if (Objects.nonNull(adminUserMakerChecker.getStatus())
                || Objects.nonNull(adminUserMakerChecker.getDeletedBy())) {
            log.info("The record is already Approved/Rejected {}", sessionUsername);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                    RECORD_ALREADY_APPROVE_OR_REJECTED));
        }

        if (!adminUserMakerChecker.getMaker().equalsIgnoreCase(ROLE_ADMIN)
                && adminUserMakerChecker.getMaker().equals(sessionUsername)) {
            log.info("Maker and Checker are same {}", sessionUsername);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                    MAKER_AND_CHECKER_ARE_SAME));
        }
        String status = STATUS_ACTIVE;

        if (adminUserMakerChecker.getMakerStatus().equals(STATUS_PENDING_DELETE)) {
            status = STATUS_DELETE;
        }
        final AdminUserMakerChecker updatedUserMakerChecker = adminUserMakerCheckerRepository.save(
                updateAdminUserMakerChecker(adminUserMakerChecker, status, sessionUsername,
                        request.checkerComments()));
        final Optional<AdminUserDeptRoles> adminUserDeptRolesOptional =
                adminUserDeptRolesRepository.findByUsername(updatedUserMakerChecker.getUsername());

        if (updatedUserMakerChecker.getStatus().equals(STATUS_ACTIVE)) {
            if (updatedUserMakerChecker.getAction().equals(Action.ADD) && adminUserDeptRolesOptional.isEmpty()) {
                final AdminUserDeptRoles adminUserDeptRoles = AdminUserDeptRoles.from(updatedUserMakerChecker);
                adminUserDeptRolesRepository.save(adminUserDeptRoles);
            } else if (updatedUserMakerChecker.getAction().equals(Action.EDIT)
                    && adminUserDeptRolesOptional.isPresent()) {
                final AdminUserDeptRoles adminUserDeptRoles = AdminUserDeptRoles.updateAdminUserDeptRoles(
                        adminUserDeptRolesOptional.get(), adminUserMakerChecker);
                adminUserDeptRolesRepository.save(adminUserDeptRoles);
            } else if (updatedUserMakerChecker.getAction().equals(Action.INACTIVE)
                    && adminUserDeptRolesOptional.isPresent()) {
                final AdminUserDeptRoles adminUserDeptRoles = adminUserDeptRolesOptional.get();
                adminUserDeptRoles.setStatus(STATUS_INACTIVE);
                adminUserDeptRolesRepository.save(adminUserDeptRoles);
            }
        } else if (updatedUserMakerChecker.getStatus().equals(STATUS_DELETE)) {
            adminUserDeptRolesRepository.deleteByUsername(updatedUserMakerChecker.getUsername());
        }
        return ApiResponse.success();
    }

    @Override
    public List<UserPendingAction> getPendingActions(PendingActionsRequest request) {
        final List<AdminUserMakerChecker> adminUserMakerCheckers;

        if (MAKER.name().equalsIgnoreCase(request.sessionRole())) {
            adminUserMakerCheckers = adminUserMakerCheckerRepository.
                    findByCheckerDateNullAndDeletedByNullAndMakerIn(List.of(request.sessionUsername()));
        } else if (CHECKER.name().equalsIgnoreCase(request.sessionRole())) {
            final List<AdminUserDeptRoles> deptUsers = adminUserDeptRolesRepository.
                    findByDept(request.sessionDeptName());
            final List<String> makers = deptUsers.stream().map(AdminUserDeptRoles::getUsername).toList();
            adminUserMakerCheckers = adminUserMakerCheckerRepository.
                    findByCheckerDateNullAndDeletedByNullAndMakerIn(makers);
        } else {
            adminUserMakerCheckers = adminUserMakerCheckerRepository.findByCheckerDateNullAndDeletedByNull();
        }
        return adminUserMakerCheckers.stream().map(UserPendingAction::from).toList();
    }

    @Override
    public long getPendingActionsCount(PendingActionsCountRequest request) {
        final long pendingActionsCount;

        if (MAKER.name().equalsIgnoreCase(request.sessionRole())) {
            pendingActionsCount = adminUserMakerCheckerRepository.
                    countByCheckerDateNullAndDeletedByNullAndMakerIn(List.of(request.sessionUsername()));
        } else if (CHECKER.name().equalsIgnoreCase(request.sessionRole())) {
            final List<AdminUserDeptRoles> deptUsers = adminUserDeptRolesRepository.
                    findByDept(request.sessionDeptName());
            final List<String> makers = deptUsers.stream().map(AdminUserDeptRoles::getUsername).toList();
            pendingActionsCount = adminUserMakerCheckerRepository.
                    countByCheckerDateNullAndDeletedByNullAndMakerIn(makers);
        } else {
            pendingActionsCount = adminUserMakerCheckerRepository.countByCheckerDateNullAndDeletedByNull();
        }
        return pendingActionsCount;
    }

    @Override
    public List<CompletedActivity> getCompletedActivities(CompletedActivitiesRequest request) {
        var completedActivities = adminUserMakerCheckerRepository.completedActivities(request.sessionUsername());
        return completedActivities.stream().map(UserCompletedActivity::from).toList();
    }

    @Override
    public ApiResponse<Void> reject(RejectRequest request) {
        try {
            final var sessionRole = request.sessionRole();
            final var sessionUserName = request.sessionUsername();
            final var userId = request.id();
            final var comments = request.checkerComments();

            if (MAKER.name().equalsIgnoreCase(sessionRole)) {
                adminUserMakerCheckerRepository.rejectUserRequest(sessionUserName, comments, userId);
            } else {
                final Optional<AdminUserMakerChecker> adminUserMakerCheckerOptional =
                        adminUserMakerCheckerRepository.findById(userId);
                if (adminUserMakerCheckerOptional.isEmpty()) {
                    log.info("User not found {}", sessionUserName);
                    return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                            String.valueOf(FAILED), USER_NOT_FOUND));
                }
                final AdminUserMakerChecker adminUserMakerChecker = adminUserMakerCheckerOptional.get();

                if (adminUserMakerChecker.getStatus() != null || adminUserMakerChecker.getDeletedBy() != null) {
                    return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                            String.valueOf(FAILED), RECORD_ALREADY_APPROVE_OR_REJECTED));
                }
                adminUserMakerCheckerRepository.updateAdminUserMakerChecker(STATUS_REJECTED, sessionUserName, comments, userId);
            }
            return ApiResponse.success();
        } catch (Exception ex) {
            log.error("Exception {}", ex.getMessage());
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }
    @Override
    public long getCustomerPendingActionsCount(PendingActionsCountRequest pendingActionsCountRequest,List<String> type) {

        final long pendingActionsCount;

        final var role = pendingActionsCountRequest.sessionRole();
        final var departmentName = pendingActionsCountRequest.sessionDeptName();

        if (MAKER.name().equalsIgnoreCase(role)) {
            pendingActionsCount = customerMakerCheckerRepository.countByMakerAndCheckerNullAndDeletedByNullAndTypeIn(
                    pendingActionsCountRequest.sessionUsername(), type);
        } else if (CHECKER.name().equalsIgnoreCase(role)) {

            final List<AdminUserDeptRoles> deptUsers = adminUserDeptRolesRepository.findByDept(departmentName);
            final List<String> makers = deptUsers.stream().map(AdminUserDeptRoles::getUsername).toList();
            pendingActionsCount = customerMakerCheckerRepository.countByMakerInAndCheckerNullAndDeletedByNullAndTypeIn(
                    makers,type);
        } else {
            pendingActionsCount = customerMakerCheckerRepository.countByCheckerNullAndDeletedByNullAndTypeIn(type);
        }
        return pendingActionsCount;
    }

    @Override
    public ApiResponse<Void> approveBulk(List<ApproveRequest> request) {
        try {
            if (request.isEmpty()) {
                log.info("approve pending list is empty");
                return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                        EMPTY_DATA));
            }
            request.forEach(approveData -> approve(approveData));
            return ApiResponse.success();
        } catch (Exception ex) {
            log.error("Exception", ex);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    String.valueOf(FAILED), FAILED_DESC));
        }
    }

    @Override
    public ApiResponse<Void> rejectBulk(List<RejectRequest> request) {
        try {
            if (request.isEmpty()) {
                log.info("reject pending list is empty");
                return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                        EMPTY_DATA));
            }
            request.forEach(rejectData -> reject(rejectData));
            return ApiResponse.success();
        } catch (Exception ex) {
            log.error("Exception", ex);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    String.valueOf(FAILED), FAILED_DESC));
        }
    }

}
