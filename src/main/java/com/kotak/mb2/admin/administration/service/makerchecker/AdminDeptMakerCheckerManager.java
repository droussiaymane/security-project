package com.kotak.mb2.admin.administration.service.makerchecker;

import com.kotak.mb2.admin.administration.domain.entity.*;
import com.kotak.mb2.admin.administration.domain.enums.Action;
import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.*;
import com.kotak.mb2.admin.administration.projection.CompletedActivityProjection;
import com.kotak.mb2.admin.administration.projection.FetchPendingActionsProjection;
import com.kotak.mb2.admin.administration.repository.*;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker.updateAdminDeptMakerChecker;
import static com.kotak.mb2.admin.administration.domain.enums.Role.CHECKER;
import static com.kotak.mb2.admin.administration.domain.enums.Role.MAKER;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDeptMakerCheckerManager implements BaseMakerCheckerManager<DeptPendingAction, DeptCompletedActivity> {

    private final AdminDeptMakerCheckerRepository adminDeptMakerCheckerRepository;
    private final AdminDepartmentsRepository adminDepartmentsRepository;
    private final AdminUserDeptRolesRepository adminUserDeptRolesRepository;
    private final CustomerMakerCheckerRepository customerMakerCheckerRepository;
    private final AdminDeptMenuMakerCheckerRepository adminDeptMenuMakerCheckerRepository;
    private final AdminDepartmentMenuAccessRepository adminDepartmentMenuAccessRepository;
    @Override
    public ApiResponse<Void> approve(ApproveRequest request) {
        final var moduleName = request.moduleName();
        final String sessionUsername = request.sessionUsername();
        final String comments = request.checkerComments();


        if(!moduleName.isEmpty() && moduleName.equalsIgnoreCase(ADMIN_DEPT)){
            final Optional<AdminDepartmentMakerChecker> adminDepartmentMakerCheckerOptional =
                    adminDeptMakerCheckerRepository.findById(request.id());


            if (adminDepartmentMakerCheckerOptional.isEmpty()) {
                log.info("Department not found, username {}", sessionUsername);
                return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                        USER_NOT_FOUND));
            }
            final AdminDepartmentMakerChecker adminDepartmentMakerChecker = adminDepartmentMakerCheckerOptional.get();

            if (Objects.nonNull(adminDepartmentMakerChecker.getStatus())
                    || Objects.nonNull(adminDepartmentMakerChecker.getDeletedBy())) {
                log.info("The record is already Approved/Rejected, username {}", sessionUsername);
                return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                        RECORD_ALREADY_APPROVE_OR_REJECTED));
            }

            if (!adminDepartmentMakerChecker.getMaker().equalsIgnoreCase(ROLE_ADMIN)
                    && adminDepartmentMakerChecker.getMaker().equals(sessionUsername)) {
                log.info("Maker and Checker are same, username {}", sessionUsername);
                return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                        MAKER_AND_CHECKER_ARE_SAME));
            }
            String status = STATUS_ACTIVE;

            if (adminDepartmentMakerChecker.getMakerStatus().equals(STATUS_PENDING_DELETE)) {
                status = STATUS_DELETE;
            }
            final AdminDepartmentMakerChecker updatedDeptMakerChecker = adminDeptMakerCheckerRepository.save(
                    updateAdminDeptMakerChecker(adminDepartmentMakerChecker, status, sessionUsername,
                            request.checkerComments()));
            final Optional<AdminDepartments> deptByName = adminDepartmentsRepository.findByName(
                    updatedDeptMakerChecker.getName());

            if (updatedDeptMakerChecker.getStatus().equals(STATUS_ACTIVE)) {
                if (updatedDeptMakerChecker.getAction().equals(Action.ADD) && deptByName.isEmpty()) {
                    adminDepartmentsRepository.save(AdminDepartments.from(updatedDeptMakerChecker));
                } else if (updatedDeptMakerChecker.getAction().equals(Action.EDIT) && deptByName.isPresent()) {
                    final AdminDepartments updatedDepartment = AdminDepartments.updateFrom(
                            deptByName.get(), STATUS_ACTIVE, updatedDeptMakerChecker.getDescription());
                    adminDepartmentsRepository.save(updatedDepartment);
                    adminUserDeptRolesRepository.updateAdminUserDeptRolesAfterAdminDeptUpdate(updatedDeptMakerChecker.getName(),
                            updatedDeptMakerChecker.getLastModifiedAt());
                } else if (updatedDeptMakerChecker.getAction().equals(Action.INACTIVE) && deptByName.isPresent()) {
                    final AdminDepartments updatedDepartment = AdminDepartments.updateFrom(
                            deptByName.get(), STATUS_INACTIVE, updatedDeptMakerChecker.getDescription());
                    adminDepartmentsRepository.save(updatedDepartment);
                    adminUserDeptRolesRepository.updateStatusAfterAdminDeptDelete(updatedDeptMakerChecker.getName());
                }
            } else if (updatedDeptMakerChecker.getStatus().equals(STATUS_DELETE) && deptByName.isPresent()) {
                final AdminDepartments updatedDepartment = AdminDepartments.updateFrom(
                        deptByName.get(), STATUS_DELETE, updatedDeptMakerChecker.getDescription());
                adminDepartmentsRepository.save(updatedDepartment);
                adminUserDeptRolesRepository.updateStatusAfterAdminDeptDelete(updatedDeptMakerChecker.getName());
            }

        }else if(!moduleName.isEmpty() && moduleName.equalsIgnoreCase(ADMIN_DEPT_MENU)){


            final Optional<AdminDeptMenuMakerChecker> adminDeptMenuMakerCheckerOptional =
                    adminDeptMenuMakerCheckerRepository.findById(request.id());

            if (adminDeptMenuMakerCheckerOptional.isEmpty()) {
                log.info("Department not found, username {}", sessionUsername);
                return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                        USER_NOT_FOUND));
            }
            final AdminDeptMenuMakerChecker adminDeptMenuMakerChecker = adminDeptMenuMakerCheckerOptional.get();
            final List<AdminDeptMenuMakerChecker> adminDeptMenuMakerCheckerByMakerDate = adminDeptMenuMakerCheckerRepository.findByMakerDate(adminDeptMenuMakerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")));

            List<ApiResponse<Void>> response = adminDeptMenuMakerCheckerByMakerDate.stream().map(deptMenuMakerChecker -> approveDepartmentMenu(deptMenuMakerChecker, request)).collect(Collectors.toList());

            if(response.get(0).isSuccessfulResponse())  return ApiResponse.success();
            return ApiResponse.error(response.get(0).getError());


        }
        return ApiResponse.success();
    }

    private ApiResponse<Void> approveDepartmentMenu(AdminDeptMenuMakerChecker adminDeptMenuMakerChecker, ApproveRequest request) {
        if (Objects.nonNull(adminDeptMenuMakerChecker.getStatus())
                || Objects.nonNull(adminDeptMenuMakerChecker.getDeletedBy())) {
            log.info("The record is already Approved/Rejected, username {}", request.sessionUsername());
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                    RECORD_ALREADY_APPROVE_OR_REJECTED));
        }

        if (!adminDeptMenuMakerChecker.getMaker().equalsIgnoreCase(ROLE_ADMIN)
                && adminDeptMenuMakerChecker.getMaker().equals(request.sessionUsername())) {
            log.info("Maker and Checker are same, username {}", request.sessionUsername());
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), String.valueOf(FAILED),
                    MAKER_AND_CHECKER_ARE_SAME));
        }
        String status = STATUS_ACTIVE;

        if (adminDeptMenuMakerChecker.getMakerStatus().equals(STATUS_PENDING_DELETE)) {
            status = STATUS_DELETE;
        }
        adminDeptMenuMakerCheckerRepository.updateAdminDepartMenumentMakerChecker(status,
                request.sessionUsername(), request.checkerComments(), adminDeptMenuMakerChecker.getId());

        if(status.equalsIgnoreCase(STATUS_ACTIVE)) {
            adminDepartmentMenuAccessRepository.save(AdminDepartmentMenuAccess.builder()
                    .withDept(adminDeptMenuMakerChecker.getDept())
                    .withMenuCode(adminDeptMenuMakerChecker.getMenuCode())
                    .build());
        }else if(status.equalsIgnoreCase(STATUS_DELETE)){
            adminDepartmentMenuAccessRepository.deleteByDeptAndMenuCode(adminDeptMenuMakerChecker.getDept(),
                    adminDeptMenuMakerChecker.getMenuCode());
        }
        return ApiResponse.success();
    }

    @Override
    public List<DeptPendingAction> getPendingActions(PendingActionsRequest request) {
        final List<AdminDepartmentMakerChecker> adminDeptMakerCheckers;
        List<FetchPendingActionsProjection> pendingActionResponseList = new ArrayList<>();
        Map<String,DeptPendingAction> pendingActionResponseMap = new HashMap<>();

        if (MAKER.name().equalsIgnoreCase(request.sessionRole())) {
            adminDeptMakerCheckers = adminDeptMakerCheckerRepository.
                    findByCheckerDateNullAndDeletedByNullAndMakerIn(List.of(request.sessionUsername()));
            pendingActionResponseList = adminDeptMenuMakerCheckerRepository.findByCheckerDateNullAndDeletedByNullAndMakerIn(List.of(request.sessionUsername()));
       } else if (CHECKER.name().equalsIgnoreCase(request.sessionRole())) {
            final List<AdminUserDeptRoles> deptUsers = adminUserDeptRolesRepository.
                    findByDept(request.sessionDeptName());
            final List<String> makers = deptUsers.stream().map(AdminUserDeptRoles::getUsername).toList();
            adminDeptMakerCheckers = adminDeptMakerCheckerRepository.
                    findByCheckerDateNullAndDeletedByNullAndMakerIn(makers);
            pendingActionResponseList = adminDeptMenuMakerCheckerRepository.
                    findByCheckerDateNullAndDeletedByNullAndMakerIn(makers);

        } else {
            adminDeptMakerCheckers = adminDeptMakerCheckerRepository.findByCheckerDateNullAndDeletedByNull();
            pendingActionResponseList = adminDeptMenuMakerCheckerRepository.findByCheckerDateNullAndDeletedByNull();
        }

        List<DeptPendingAction> pendingActionsForDept =  adminDeptMakerCheckers.stream().map(DeptPendingAction :: from).toList();
        pendingActionResponseList.stream().forEach(DeptPendingAction -> PendingMenuMakerCheckerManager(DeptPendingAction, pendingActionResponseMap));
        Collection<DeptPendingAction> values = pendingActionResponseMap.values();
        return Stream.of(pendingActionsForDept, values)
                .flatMap(x -> x.stream()).collect(Collectors.toList());
    }

    private void PendingMenuMakerCheckerManager(FetchPendingActionsProjection makerChecker, Map<String, DeptPendingAction> pendingActionResponseMap) {

        pendingActionResponseMap.put(makerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")),DeptPendingAction.builder()
                .moduleName(ADMIN_DEPT_MENU)
                .dept(makerChecker.getDept())
                .id(makerChecker.getId())
                .maker(makerChecker.getMaker())
                .makerDate(makerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")))
                .makerComments(makerChecker.getMakerComments())
                .menuCode(makerChecker.getMenuCode())
                .status(makerChecker.getMakerStatus().equalsIgnoreCase(STATUS_PENDING)
                        ? STATUS_ACTIVE : STATUS_DELETE)
                .desc(makerChecker.getDescription())
                .actionType(makerChecker.getAction())
                .build());

    }

    @Override
    public long getPendingActionsCount(PendingActionsCountRequest request) {
        final long pendingActionsCount;
        final long pendingActionsCountForMenu;

        if (MAKER.name().equalsIgnoreCase(request.sessionRole())) {
            pendingActionsCount = adminDeptMakerCheckerRepository.
                    countByCheckerDateNullAndDeletedByNullAndMakerIn(List.of(request.sessionUsername()));
            pendingActionsCountForMenu = adminDeptMenuMakerCheckerRepository.countByCheckerDateNullAndDeletedByNullAndMakerIn(List.of(request.sessionUsername()));

        } else if (CHECKER.name().equalsIgnoreCase(request.sessionRole())) {
            final List<AdminUserDeptRoles> deptUsers = adminUserDeptRolesRepository.
                    findByDept(request.sessionDeptName());
            final List<String> makers = deptUsers.stream().map(AdminUserDeptRoles::getUsername).toList();
            pendingActionsCount = adminDeptMakerCheckerRepository.
                    countByCheckerDateNullAndDeletedByNullAndMakerIn(makers);
            pendingActionsCountForMenu = adminDeptMenuMakerCheckerRepository.
                    countByCheckerDateNullAndDeletedByNullAndMakerIn(makers);

        } else {
            pendingActionsCount = adminDeptMakerCheckerRepository.countByCheckerDateNullAndDeletedByNull();
            pendingActionsCountForMenu = adminDeptMenuMakerCheckerRepository.countByCheckerDateNullAndDeletedByNull();
        }
        return pendingActionsCount+pendingActionsCountForMenu;
    }

    @Override
    public List<CompletedActivity> getCompletedActivities(CompletedActivitiesRequest request) {
        var completedActivities = adminDeptMakerCheckerRepository.completedActivities(
                request.sessionUsername());
        var completedActivitiesMenu =  adminDeptMenuMakerCheckerRepository.completedActivities(
                request.sessionUsername());
        List<CompletedActivity> completeActionsForDept = completedActivities.stream().map(DeptCompletedActivity::from).toList();

       // List<CompletedActivity> completeActionsForMenu = completedActivitiesMenu.stream().map(DeptCompletedActivity::MenuMakerCheckerManager).toList();
        final Map<String,CompletedActivity> completeActionsForMenu = new HashMap<>();
        final Set<String> makerDate = new HashSet<>();
        final MultiMap<String, MenuResponse> menuMap = new MultiValueMap<>();
        completedActivitiesMenu.stream().forEach(menuProjection -> completedMenuMakerCheckerManager(menuProjection, completeActionsForMenu, makerDate, menuMap));
        Collection<CompletedActivity> values = completeActionsForMenu.values();

        return Stream.of(completeActionsForDept,values)
                .flatMap(x -> x.stream()).collect(Collectors.toList());

    }

    private void completedMenuMakerCheckerManager(CompletedActivityProjection completedActivityProjection, Map<String,CompletedActivity> completeActionsForMenu,
                                         Set<String> makerDateSet, MultiMap<String, MenuResponse> menuMap) {
        String makerDate = completedActivityProjection.getMakerDate();
        makerDateSet.add(makerDate);


        menuMap.put(makerDate,
                MenuResponse.builder()
                        .menuName(completedActivityProjection.getDescription())
                        .menuAction(completedActivityProjection.getMakerAction())
                        .build());


        completeActionsForMenu.put(completedActivityProjection.getMakerDate(),
                CompletedActivity.builder()
                    .module(ADMIN_DEPT_MENU)
                    .maker(completedActivityProjection.getMaker())
                    .makerDate(completedActivityProjection.getMakerDate())
                    .makerComments(completedActivityProjection.getMakerComments())
                    .authorizedDate(Objects.nonNull(completedActivityProjection.getAuthzDate())
                            ? completedActivityProjection.getAuthzDate()
                            : null)
                    .authorizedBy(completedActivityProjection.getAuthz())
                    .authorizedAction(completedActivityProjection.getAuthzAction())
                    .authorizedComments(completedActivityProjection.getAuthzComments())
                    .type("DEPT")
                    .menuList((List<MenuResponse>)menuMap.get(makerDate))
                    .department(completedActivityProjection.getDept())
                    .build());

    }



    @Override
    public ApiResponse<Void> reject(RejectRequest request) {
        try {
            final var sessionRole = request.sessionRole();
            final var sessionUserName = request.sessionUsername();
            final var departmentId = request.id();
            final var comments = request.checkerComments();
            final var moduleName = request.moduleName();

            if(!moduleName.isEmpty() && moduleName.equalsIgnoreCase(ADMIN_DEPT)){
                if (MAKER.name().equalsIgnoreCase(sessionRole)) {
                    adminDeptMakerCheckerRepository.rejectDepartmentRequest(sessionUserName, comments, departmentId);
                } else {
                    final Optional<AdminDepartmentMakerChecker> adminDepartmentMakerCheckerOptional =
                            adminDeptMakerCheckerRepository.findById(departmentId);
                    if (adminDepartmentMakerCheckerOptional.isEmpty()) {
                        log.info("Department not found {}", sessionUserName);
                        return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                String.valueOf(FAILED), USER_NOT_FOUND));
                    }
                    final AdminDepartmentMakerChecker adminDepartmentMakerChecker =
                            adminDepartmentMakerCheckerOptional.get();

                    if (adminDepartmentMakerChecker.getStatus() != null
                            || adminDepartmentMakerChecker.getDeletedBy() != null) {
                        return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                String.valueOf(FAILED), RECORD_ALREADY_APPROVE_OR_REJECTED));
                    }
                    adminDeptMakerCheckerRepository.updateAdminDepartmentMakerChecker(STATUS_REJECTED,
                            sessionUserName, comments, departmentId);
                }

            }else if(!moduleName.isEmpty() && moduleName.equalsIgnoreCase(ADMIN_DEPT_MENU)){
                if (MAKER.name().equalsIgnoreCase(sessionRole)) {
                    adminDeptMenuMakerCheckerRepository.rejectDepartmentMenuRequest(sessionUserName, comments, departmentId);
                } else {
                    final Optional<AdminDeptMenuMakerChecker> adminDeptMenuMakerCheckerOptional =
                            adminDeptMenuMakerCheckerRepository.findById(departmentId);
                    if (adminDeptMenuMakerCheckerOptional.isEmpty()) {
                        log.info("Department not found {}", sessionUserName);
                        return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                String.valueOf(FAILED), USER_NOT_FOUND));
                    }
                    final AdminDeptMenuMakerChecker adminDeptMenuMakerChecker = adminDeptMenuMakerCheckerOptional.get();
                    final List<AdminDeptMenuMakerChecker> adminDeptMenuMakerCheckerByMakerDate = adminDeptMenuMakerCheckerRepository.findByMakerDate(adminDeptMenuMakerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")));

                    List<ApiResponse<Void>> response = adminDeptMenuMakerCheckerByMakerDate.stream().map(deptMenuMakerChecker -> rejectDepartmentMenu(deptMenuMakerChecker, request)).collect(Collectors.toList());

                    if(response.get(0).isSuccessfulResponse())  return ApiResponse.success();
                    return ApiResponse.error(response.get(0).getError());


                }
            }
            return ApiResponse.success();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception {}", ex.getMessage());
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    private ApiResponse<Void> rejectDepartmentMenu(AdminDeptMenuMakerChecker adminDeptMenuMakerChecker, RejectRequest request) {
        if (adminDeptMenuMakerChecker.getStatus() != null
                || adminDeptMenuMakerChecker.getDeletedBy() != null) {
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    String.valueOf(FAILED), RECORD_ALREADY_APPROVE_OR_REJECTED));
        }
        adminDeptMenuMakerCheckerRepository.updateAdminDepartMenumentMakerChecker(STATUS_REJECTED,
                request.sessionUsername(), request.checkerComments(), adminDeptMenuMakerChecker.getId());
        return ApiResponse.success();
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
                    makers, type);
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
            List<ApiResponse<Void>> response = request.stream().map(approveData -> approve(approveData)).collect(Collectors.toList());
            if(response.get(0).isSuccessfulResponse())  return ApiResponse.success();
            return ApiResponse.error(response.get(0).getError());

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
            List<ApiResponse<Void>> response = request.stream().map(rejectData -> reject(rejectData)).collect(Collectors.toList());
            if(response.get(0).isSuccessfulResponse())  return ApiResponse.success();
            return ApiResponse.error(response.get(0).getError());
        } catch (Exception ex) {
            log.error("Exception", ex);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    String.valueOf(FAILED), FAILED_DESC));
        }

    }

}
