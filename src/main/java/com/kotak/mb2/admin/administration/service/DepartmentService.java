package com.kotak.mb2.admin.administration.service;

import com.kotak.mb2.admin.administration.config.ServiceProperties;
import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMenuAccess;
import com.kotak.mb2.admin.administration.domain.entity.AdminDeptMenuMakerChecker;
import com.kotak.mb2.admin.administration.domain.enums.Role;
import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.*;
import com.kotak.mb2.admin.administration.projection.AdminMenuProfileProjection;
import com.kotak.mb2.admin.administration.projection.AdminUserDeptRolesProjection;
import com.kotak.mb2.admin.administration.projection.MenuPendingActionProjection;
import com.kotak.mb2.admin.administration.repository.*;
import com.kotak.mb2.admin.administration.service.makerchecker.AdminDeptMakerCheckerManager;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker.*;
import static com.kotak.mb2.admin.administration.domain.response.AccessListResponse.fillMenus;
import static com.kotak.mb2.admin.administration.domain.response.ActivateDelDepartmentResponse.*;
import static com.kotak.mb2.admin.administration.domain.response.AddDepartmentResponse.*;
import static com.kotak.mb2.admin.administration.domain.response.DeleteDepartmentResponse.*;
import static com.kotak.mb2.admin.administration.domain.response.EditDepartmentResponse.*;
import static com.kotak.mb2.admin.administration.domain.response.GetDepartmentResponse.getDepartmentResponse;
import static com.kotak.mb2.admin.administration.domain.response.MenuProfileResponse.*;
import static com.kotak.mb2.admin.administration.domain.response.ShowDetailsMenuResponse.menuDetailsResponse;
import static com.kotak.mb2.admin.administration.domain.response.UpdateDepartmentAccessResponse.*;
import static com.kotak.mb2.admin.administration.util.CommonUtils.isStringEmptyOrNull;
import static org.springframework.http.HttpStatus.*;
import static com.kotak.mb2.admin.administration.domain.response.ShowDetailsDeptResponse.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final AdminDeptMakerCheckerRepository adminDeptMakerCheckerRepository;
    private final AdminDepartmentsRepository adminDepartmentsRepository;
    private final AdminMenuProfileRepository adminMenuProfileRepository;
    private final AdminDepartmentMenuAccessRepository adminDepartmentMenuAccessRepository;
    private final AdminDeptMenuMakerCheckerRepository adminDeptMenuMakerCheckerRepository;
    private final AdminUserDeptRolesRepository adminUserDeptRolesRepository;
    private final AdminDeptMakerCheckerManager adminDeptMakerCheckerManager;
    private final AdminUserMakerCheckerRepository adminUserMakerCheckerRepository;
    private final ServiceProperties serviceProperties;

    @Transactional
    public ApiResponse<List<GetDepartmentResponse>> getDepartments() {
        final List<GetDepartmentResponse> departmentResponses = new ArrayList<>();
        try {
            log.debug("Getting data from database..");
            final var departments = adminDepartmentsRepository.getDepartments();
            departments.forEach(adminDepartmentMakerChecker -> getDepartmentResponse(departmentResponses,
                    adminDepartmentMakerChecker));

            return departmentResponses.isEmpty() ? ApiResponse.success(Collections.emptyList())
                    : ApiResponse.success(departmentResponses);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ApiResponse<AddDepartmentResponse> addDepartment(AddDepartmentRequest addDepartmentRequest) {
        final AddDepartmentResponse addDepartmentResponse = new AddDepartmentResponse();
        try {
            addDepartment(addDepartmentRequest, addDepartmentResponse);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForAddDept(addDepartmentResponse);
        }
        return addDepartmentResponse.getStatusCode() == FAILED ? ApiResponse.error(new ErrorResponse(
                addDepartmentResponse.getStatusCode(), addDepartmentResponse.getStatusDesc()))
                : ApiResponse.success(addDepartmentResponse);
    }

    @Transactional
    private void addDepartment(AddDepartmentRequest addDepartmentRequest, AddDepartmentResponse addDepartmentResponse) {
        try {
            log.debug("Storing department in database..");
            final var deptName = addDepartmentRequest.deptName();
            final Long deptCount = adminDepartmentsRepository.countByName(deptName);

            if (deptCount > 0) {
                log.info("Department already exist {}", deptName);
                setFailedStatusForDeptAlreadyExists(addDepartmentResponse);
                return;
            }
            final Long pendingCount = adminDeptMakerCheckerRepository.countByNameAndCheckerNullAndDeletedByNull(
                    deptName);

            if (pendingCount > 0) {
                log.info("Department already pending for approval {}", deptName);
                setFailedStatusForAddPendingDeptReq(addDepartmentResponse, serviceProperties);
                return;
            }
            final var sessionRole = addDepartmentRequest.sessionRole();
            final var sessionUsername = addDepartmentRequest.sessionUsername();
            final var adminDepartmentMakerChecker = getAdminDepartmentMakerChecker(
                    addDepartmentRequest, deptName, sessionUsername);
            final var makerChecker = adminDeptMakerCheckerRepository.save(adminDepartmentMakerChecker);
            final var deptId = makerChecker.getId();

            log.debug("Department inserted successfully {}", deptName);
            if (ROLE_ADMIN.equalsIgnoreCase(sessionRole)) {
                setSuccessStatusForDeptAdded(addDepartmentResponse, deptId);
                log.info("Department is added {}", deptName);

                final ApiResponse<Void> approveResponse = adminDeptMakerCheckerManager.approve(
                        new ApproveRequest(deptId, sessionUsername, null,ADMIN_DEPT));
                if (approveResponse.isSuccessfulResponse()) {
                    log.info("Department is added.. {}", deptName);
                    setSuccessStatusForDeptAdded(addDepartmentResponse, deptId);
                } else {
                    setFailedStatusForAddDept(addDepartmentResponse);
                }
            } else {
                log.info("Request sent for approval..");
                setSuccessStatusForAddReqSentForApproval(addDepartmentResponse, deptId, serviceProperties);
            }
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForAddDept(addDepartmentResponse);
        }
    }

    public ApiResponse<EditDepartmentResponse> editDepartment(EditDepartmentRequest editDepartmentRequest) {
        final EditDepartmentResponse editDepartmentResponse = new EditDepartmentResponse();
        try {
            if (editDepartmentRequest.sessionDeptId() != null &&
                    editDepartmentRequest.sessionDeptId().equalsIgnoreCase(editDepartmentRequest.deptId())) {
                log.info("You cannot edit your own department");
                setFailedStatusForCannotEditOwnDept(editDepartmentResponse);
                return ApiResponse.error(new ErrorResponse(editDepartmentResponse.getStatusCode(),
                        editDepartmentResponse.getStatusDesc()));
            }
            editDepartment(editDepartmentRequest, editDepartmentResponse);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForEditDept(editDepartmentResponse);
        }
        return editDepartmentResponse.getStatusCode() == FAILED ? ApiResponse.error(new ErrorResponse(
                editDepartmentResponse.getStatusCode(), editDepartmentResponse.getStatusDesc())) :
                ApiResponse.success(editDepartmentResponse);
    }

    @Transactional
    private void editDepartment(EditDepartmentRequest editDepartmentRequest,
                                EditDepartmentResponse editDepartmentResponse) {
        try {
            final var deptId = editDepartmentRequest.deptId();
            final var departments = adminDepartmentsRepository.findById(deptId);
            if (departments.isEmpty()) {
                log.info("Department does not exist:: {}", deptId);
                setFailedStatusDeptNotFoundForEdit(editDepartmentResponse);
                return;
            }
            final var adminDepartments = departments.get();
            final var departmentName = adminDepartments.getName();
            final var pendingCount = adminDeptMakerCheckerRepository.countByNameAndCheckerNullAndDeletedByNull(
                    departmentName);

            if (pendingCount > 0) {
                setFailedStatusForEditPendingDeptReq(editDepartmentResponse, serviceProperties);
                log.info("Request already pending for approval {}", deptId);
                return;
            }
            final var role = editDepartmentRequest.sessionRole();
            final var saved = adminDeptMakerCheckerRepository.save(
                    setDescAndMakerForEditDept(editDepartmentRequest, departmentName));

            log.debug("Department edit successful {}", deptId);
            setSuccessStatusForDeptEdited(editDepartmentResponse, deptId);

            if (ROLE_ADMIN.equalsIgnoreCase(role)) {
                final ApiResponse<Void> approveResponse = adminDeptMakerCheckerManager.approve(
                        new ApproveRequest(saved.getId(), editDepartmentRequest.sessionUsername(), null,ADMIN_DEPT));
                if (approveResponse.isSuccessfulResponse()) {
                    log.info("Department is edited.. {}", deptId);
                    setSuccessStatusForDeptEdited(editDepartmentResponse, deptId);
                } else {
                    setFailedStatusForEditDept(editDepartmentResponse);
                }
            } else {
                setSuccessStatusForEditReqSentForApproval(editDepartmentResponse, deptId, serviceProperties);
            }
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForEditPendingDeptReq(editDepartmentResponse, serviceProperties);
        }
    }

    public ApiResponse<DeleteDepartmentResponse> deleteDepartment(DeleteDepartmentRequest deleteDepartmentRequest) {
        final DeleteDepartmentResponse deleteDepartmentResponse = new DeleteDepartmentResponse();
        try {
            if (deleteDepartmentRequest.sessionDeptId() != null &&
                    deleteDepartmentRequest.sessionDeptId().equalsIgnoreCase(deleteDepartmentRequest.deptId())) {
                log.info("You cannot delete your own department {}", deleteDepartmentRequest.deptId());
                setFailedStatusForCannotDeleteOwnDept(deleteDepartmentResponse);
                return ApiResponse.error(new ErrorResponse(deleteDepartmentResponse.getStatusCode(),
                        deleteDepartmentResponse.getStatusDesc()));
            }
            deleteDepartment(deleteDepartmentRequest, deleteDepartmentResponse);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForDeleteDept(deleteDepartmentResponse);
        }
        return deleteDepartmentResponse.getStatusCode() == FAILED ? ApiResponse.error(new ErrorResponse(
                deleteDepartmentResponse.getStatusCode(), deleteDepartmentResponse.getStatusDesc())) :
                ApiResponse.success(deleteDepartmentResponse);
    }

    @Transactional
    private void deleteDepartment(DeleteDepartmentRequest deleteDepartmentRequest,
                                  DeleteDepartmentResponse deleteDepartmentResponse) {
        try {
            final var deptId = deleteDepartmentRequest.deptId();
            final var departments = adminDepartmentsRepository.findById(deptId);
            if (departments.isEmpty()) {
                log.info("Department does not exist:: {}", deptId);
                setFailedStatusDeptNotFoundForDelete(deleteDepartmentResponse);
                return;
            }
            final var adminDepartments = departments.get();
            final var departmentName = adminDepartments.getName();
            final var pendingCount = adminDeptMakerCheckerRepository.countByNameAndCheckerNullAndDeletedByNull(
                    departmentName);

            if (pendingCount > 0) {
                setFailedStatusForDeletePendingDeptReq(deleteDepartmentResponse, serviceProperties);
                log.info("Request already pending for approval {}", deptId);
                return;
            }
            final var role = deleteDepartmentRequest.sessionRole();
            final var saved = adminDeptMakerCheckerRepository.save(
                    setDescAndMakerForDeleteDept(departmentName, deleteDepartmentRequest.sessionUsername()));

            log.debug("Department deleted successful {}", deptId);
            setSuccessStatusForDeptDeleted(deleteDepartmentResponse, deptId);

            if (ROLE_ADMIN.equalsIgnoreCase(role)) {
                final ApiResponse<Void> approveResponse = adminDeptMakerCheckerManager.approve(
                        new ApproveRequest(saved.getId(), deleteDepartmentRequest.sessionUsername(), null,ADMIN_DEPT));
                if (approveResponse.isSuccessfulResponse()) {
                    log.info("Department is deleted.. {}", deptId);
                    setSuccessStatusForDeptDeleted(deleteDepartmentResponse, deptId);
                } else {
                    setFailedStatusForDeleteDept(deleteDepartmentResponse);
                }
            } else {
                setSuccessStatusForDeleteReqSentForApproval(deleteDepartmentResponse, deptId, serviceProperties);
            }
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForDeletePendingDeptReq(deleteDepartmentResponse, serviceProperties);
        }
    }

    public ApiResponse<ActivateDelDepartmentResponse> activateDelDept(ActivateDelDepartmentRequest
                                                                              activateDelDepartmentRequest) {
        final ActivateDelDepartmentResponse activateDelDepartmentResponse = new ActivateDelDepartmentResponse();
        try {
            if (activateDelDepartmentRequest.sessionDeptId() != null &&
                    activateDelDepartmentRequest.sessionDeptId().equalsIgnoreCase(
                            activateDelDepartmentRequest.deptId())) {
                log.info("You cannot edit your own department");
                setFailedStatusForCannotActivateOwnDept(activateDelDepartmentResponse);
                return ApiResponse.error(new ErrorResponse(activateDelDepartmentResponse.getStatusCode(),
                        activateDelDepartmentResponse.getStatusDesc()));
            }
            activateDelDept(activateDelDepartmentRequest, activateDelDepartmentResponse);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForActivateDept(activateDelDepartmentResponse);
        }
        return activateDelDepartmentResponse.getStatusCode() == FAILED ? ApiResponse.error(new ErrorResponse(
                activateDelDepartmentResponse.getStatusCode(), activateDelDepartmentResponse.getStatusDesc()))
                : ApiResponse.success(activateDelDepartmentResponse);
    }

    @Transactional
    private void activateDelDept(ActivateDelDepartmentRequest activateDelDepartmentRequest,
                                 ActivateDelDepartmentResponse activateDelDepartmentResponse) {
        try {
            final var deptId = activateDelDepartmentRequest.deptId();
            final var departments = adminDepartmentsRepository.findById(deptId);
            if (departments.isEmpty()) {
                log.info("Department does not exist {}", deptId);
                setFailedStatusDeptNotFoundForActivateDelDept(activateDelDepartmentResponse);
                return;
            }
            final var adminDepartments = departments.get();
            final var departmentName = adminDepartments.getName();
            final var pendingCount = adminDeptMakerCheckerRepository.countByNameAndCheckerNullAndDeletedByNull(
                    departmentName);

            if (pendingCount > 0) {
                setFailedStatusForActivatePendingDeptReq(activateDelDepartmentResponse, serviceProperties);
                log.info("Request already pending for approval");
                return;
            }
            final var hasUserDept = adminUserMakerCheckerRepository.
                    countByDeptAndCheckerNullAndDeletedByNull(departmentName);
            final var hasAccessDept = adminDeptMenuMakerCheckerRepository.
                    countByDeptAndCheckerNullAndDeletedByNull(departmentName);

            if (hasUserDept > 0 || hasAccessDept > 0) {
                setFailedStatusForPendingDeptReq(activateDelDepartmentResponse);
                log.info("Pending dependent requests..");
                return;
            }
            final var sessionRole = activateDelDepartmentRequest.sessionRole();
            final var response = adminDeptMakerCheckerRepository.save(
                    setDescAndMakerForActivateDept(activateDelDepartmentRequest, departmentName));

            log.debug("Department edit successful");
            setSuccessStatusForActivateDept(activateDelDepartmentResponse, deptId);

            if (ROLE_ADMIN.equalsIgnoreCase(sessionRole)) {
                final ApiResponse<Void> approveResponse = adminDeptMakerCheckerManager.approve(
                        new ApproveRequest(response.getId(), activateDelDepartmentRequest.sessionUsername(),
                                null,ADMIN_DEPT));
                if (approveResponse.isSuccessfulResponse()) {
                    log.info("Department is updated.. {}", deptId);
                    setSuccessStatusForActivateDeptUpdated(activateDelDepartmentResponse, deptId);
                } else {
                    setFailedStatusForActivateDept(activateDelDepartmentResponse);
                }
            } else {
                setSuccessStatusForActivateReqSentForApproval(activateDelDepartmentResponse, deptId, serviceProperties);
            }
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForActivatePendingDeptReq(activateDelDepartmentResponse, serviceProperties);
        }
    }

    @Transactional
    public ApiResponse<ListDepartmentAccessResponse> listDeptAccess(ListDepartmentAccessRequest
                                                                            listDepartmentAccessRequest) {
        final var adminDepartment = adminDepartmentsRepository.findById(
                listDepartmentAccessRequest.deptId());
        if (adminDepartment.isEmpty()) {
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(), DEPT_NOT_EXISTS));
        }
        final var accessList = adminMenuProfileRepository.getDeptMenuAccessList();

        MenuProfileResponse parentMenu = null;
        MenuProfileResponse childMenu;
        final Hashtable<String, MenuProfileResponse> menus = new Hashtable<>();

        for (AdminMenuProfileProjection menu : accessList) {
            if (isStringEmptyOrNull(menu.getParentMenuName())) {
                continue;
            }

            if (Objects.isNull(parentMenu)  ||
                    !parentMenu.getMenuName().equals(menu.getParentMenuName())) {
                if (menus.containsKey(menu.getParentMenuName())) {
                    parentMenu = menus.get(menu.getParentMenuName());
                } else {
                    parentMenu = setParentMenuNameOfMenuProfile(menu);
                    menus.put(menu.getParentMenuName(), parentMenu);
                }
            }
            childMenu = menus.containsKey(menu.getMenuName()) ? menus.get(menu.getMenuName())
                    : setMenuNameOfMenuProfile(menu);

            final var departments = adminDepartment.get();


            if((menus.containsKey(menu.getMenuName()) && !menus.get(menu.getMenuName()).getStatus()) ||
                    !menus.containsKey(menu.getMenuName())){
                setChildDetails(childMenu, departments.getId(), menu, departments);
                menus.put(menu.getMenuName(), childMenu);
            }

            if (!parentMenu.hasChild(menu.getMenuName())) {
                parentMenu.addChild(childMenu);
            }
        }
        final MenuProfileResponse rootMenu = menus.get(ROLE_ADMIN);
        List<AccessListResponse> departmentAccessResponse = new ArrayList<>();

        for (MenuProfileResponse menuProfileResponse : rootMenu.getChildren()) {
            fillMenus(menuProfileResponse, departmentAccessResponse);
        }
        return ApiResponse.success(new ListDepartmentAccessResponse(departmentAccessResponse));
    }
    @Transactional
    public ApiResponse<UpdateDepartmentAccessResponse> updateDeptAccess(UpdateDepartmentAccessRequest
                                                                                updateDepartmentAccessRequest) {
        final UpdateDepartmentAccessResponse updateDepartmentAccessResponse = new UpdateDepartmentAccessResponse();
        try {
            if (updateDepartmentAccessRequest.sessionDeptId() != null &&
                    updateDepartmentAccessRequest.sessionDeptId().equalsIgnoreCase(
                            updateDepartmentAccessRequest.deptId())) {
                log.info("You cannot edit your own department");
                setFailedStatusForCannotEditOwnDeptAccess(updateDepartmentAccessResponse);
                return ApiResponse.error(new ErrorResponse(updateDepartmentAccessResponse.getStatusCode(),
                        updateDepartmentAccessResponse.getStatusDesc()));
            }
            updateDeptAccess(updateDepartmentAccessRequest, updateDepartmentAccessResponse);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            setFailedStatusForUpdateDeptAccess(updateDepartmentAccessResponse);
        }
        return updateDepartmentAccessResponse.getStatusCode() == FAILED ? ApiResponse.error(new ErrorResponse(
                updateDepartmentAccessResponse.getStatusCode(), updateDepartmentAccessResponse.getStatusDesc()))
                : ApiResponse.success(updateDepartmentAccessResponse);
    }

    private void updateDeptAccess(UpdateDepartmentAccessRequest updateDepartmentAccessRequest,
                                  UpdateDepartmentAccessResponse updateDepartmentAccessResponse) {
        final var deptId = updateDepartmentAccessRequest.deptId();
        final var adminDepartments = adminDepartmentsRepository.findById(deptId);
        if (adminDepartments.isEmpty()) {
            log.info("Department does not exist {}", deptId);
            setFailedStatusForDeptNotFound(updateDepartmentAccessResponse);
            return;
        }
        final var departments = adminDepartments.get();
        final var deptName = departments.getName();
        final Long pendingCount = adminDeptMakerCheckerRepository.countByNameAndCheckerNullAndDeletedByNull(deptName);

        if (pendingCount > 0) {
            log.info("Department already pending for approval {}", deptName);
            setFailedStatusForEditPendingDeptAccessReq(updateDepartmentAccessResponse, serviceProperties);
            return;
        }
        final var departmentMenuAccess = adminDepartmentMenuAccessRepository.findByDept(deptName);
        final Vector<String> IDs = new Vector<>();

        //final var maker = updateDepartmentAccessRequest.sessionRole();
        final var maker = updateDepartmentAccessRequest.sessionUsername();
        processAccessList(updateDepartmentAccessRequest.accessList(), departmentMenuAccess, IDs,
                deptName, maker);

        //if (ROLE_ADMIN.equalsIgnoreCase(maker)) {
        if (ROLE_ADMIN.equalsIgnoreCase(updateDepartmentAccessRequest.sessionRole())) {
            for (String id : IDs) {
                setDeptIdForUpdateDeptAccess(updateDepartmentAccessResponse, id);
                final ApiResponse<Void> approveResponse = adminDeptMakerCheckerManager.approve(
                        new ApproveRequest(id, updateDepartmentAccessRequest.sessionUsername(), null,ADMIN_DEPT_MENU));
                if (approveResponse.isSuccessfulResponse()) {
                    setSuccessStatusForUpdateDeptAccess(updateDepartmentAccessResponse);
                    log.info("Department is added.. deptName: {} Id: {}", deptName, id);
                } else {
                    setFailedStatusForUpdateDeptAccess(updateDepartmentAccessResponse);
                }
            }
            setSuccessStatusForUpdateDeptAccess(updateDepartmentAccessResponse);
            log.info("Department is added deptName: {} Id: {}", deptName, deptId);

        } else {
            log.info("Request sent for approval..");
            setSuccessStatusForDeptAccessReqSentForApproval(updateDepartmentAccessResponse, serviceProperties);
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void processAccessList(List<AccessListResponse> accessListResponse, List<AdminDepartmentMenuAccess>
            menuList, Vector<String> IDs, String deptName, String maker) {

        for (AccessListResponse response : accessListResponse) {
            final String menuCode = response.getMenuCode();
            final Boolean status = response.getStatus();
            final String description = response.getDescription();

            if ((!status && !containsMenuCode(menuList,menuCode)) || (status && containsMenuCode(menuList,menuCode))) {
                // Rejecting nodes(request for deletion) which are not there in currentMenus.
                // Rejecting nodes(request for addition) which are already present in currentMenus.
                if (!response.getChildren().isEmpty()) {
                    processAccessList(response.getChildren(), menuList, IDs, deptName, maker);
                }
                continue;
            }
            final var menuMakerChecker = adminDeptMenuMakerCheckerRepository.save(
                    AdminDeptMenuMakerChecker.builder()
                            .withDept(deptName)
                            .withMenuCode(menuCode)
                            .withMaker(maker)
                            .withMakerStatus(status ? STATUS_PENDING : STATUS_PENDING_DELETE)
                            .withMakerDate(OffsetDateTime.now())
                            .withDescription(description)
                            .withAction(status ? ENABLED : DISABLED)
                            .build());

            IDs.add(menuMakerChecker.getId());

            /*adminDepartmentMenuAccessRepository.deleteByDeptAndMenuCode(deptName,menuCode);
            adminDepartmentMenuAccessRepository.save(
                    AdminDepartmentMenuAccess.builder()
                            .withDept(deptName)
                            .withMenuCode(menuCode)
                            .build());*/

            if (!response.getChildren().isEmpty()) {
                processAccessList(response.getChildren(), menuList, IDs, deptName, maker);
            }
        }
    }

    public ApiResponse<GetDeptUsersResponse> getDepartmentUsers(GetDeptUsersRequest getDeptUsersRequest) {
        try {
            final List<AdminUserDeptRolesProjection> adminUserDeptRolesProjection = adminUserDeptRolesRepository.
                    getDeptUsers(getDeptUsersRequest.deptId());
            return ApiResponse.success(new GetDeptUsersResponse(adminUserDeptRolesProjection
                    .stream().map(UserListResponse::getUserListResponse).toList()));
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                    String.valueOf(FAILED), USER_NOT_FOUND));
        }
    }
    @Transactional
    public ApiResponse<Void> approveDepartment(@Valid List<ApproveRequest> request) {
        try {
            return this.adminDeptMakerCheckerManager.approveBulk(request);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    public ApiResponse<List<DeptPendingAction>> getPendingActions(PendingActionsRequest request) {
        try {
            log.debug("UserName:: {}", request.sessionUsername());
            if (request.sessionRole().equalsIgnoreCase(Role.CHECKER.name())
                    && StringUtils.isEmpty(request.sessionDeptName())) {
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                        "session_dept_name " + SHOULD_NOT_BE_BLANK));
            }
            return ApiResponse.success(adminDeptMakerCheckerManager.getPendingActions(request));
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }


    @Transactional
    public ApiResponse<Void> rejectDepartment(@Valid List<RejectRequest> request) {
        try {
            return adminDeptMakerCheckerManager.rejectBulk(request);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    public boolean containsMenuCode(final List<AdminDepartmentMenuAccess> menuList, final String menuCode){
        return menuList.stream().map(AdminDepartmentMenuAccess::getMenuCode).filter(menuCode::equals).findFirst().isPresent();
    }

    public ApiResponse<ShowPendingDeptDetails> ShowDetails(ShowDetailsRequest request) {
        try {
            final var userId = request.id();
            final var deptName = request.name();

            final Optional<AdminDepartmentMakerChecker> adminDeptMakerChecker = adminDeptMakerCheckerRepository.findById(userId);
            if (adminDeptMakerChecker.isEmpty()) {
                log.info("Department does not exist {}", userId);
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                        String.valueOf(FAILED), USER_NOT_FOUND));
            }
            final Optional<AdminDepartmentMakerChecker> existingDeptMakerChecker =
                    adminDeptMakerCheckerRepository.findFirstByNameAndCheckerNotNullOrderByCreatedAtDesc(deptName);

            final var requestedData = adminDeptMakerChecker.stream().map(admc -> from(admc, REQUESTED_DETAILS));
            final var existingData = existingDeptMakerChecker.stream().map(admc -> from(admc, EXISTING_DETAILS));

            return ApiResponse.success(new ShowPendingDeptDetails(existingData, requestedData));



        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    public ApiResponse<List<ShowDetailsMenuResponse>> showMenuDetails(showMenuDetailsRequest request) {
        final var deptName = request.name();
        final var makerDate = request.makerDate();
        Map<String,ShowDetailsMenuResponse> menuResponseMap = new HashMap<>();

        final List<MenuPendingActionProjection> existingMenuProjection = adminDeptMenuMakerCheckerRepository.getExistingMenuDetails(deptName);
        List<ShowDetailsMenuResponse> showDetailsExistingMenuResponse =  existingMenuProjection.stream().map(ShowDetailsMenuResponse -> menuDetailsResponse(ShowDetailsMenuResponse, menuResponseMap)).toList();

        final List<MenuPendingActionProjection> menuPendingActionProjection = adminDeptMenuMakerCheckerRepository.getPendingMenuDetails(deptName, makerDate);
        List<ShowDetailsMenuResponse> showDetailsMenuResponse =  menuPendingActionProjection.stream().map(ShowDetailsMenuResponse -> menuDetailsResponse(ShowDetailsMenuResponse, menuResponseMap)).toList();
        //showDetailsExistingMenuResponse.addAll(showDetailsMenuResponse);

        return ApiResponse.success(Stream.of(menuResponseMap.values())
                .flatMap(Collection::stream).collect(Collectors.toList()));


        //return ApiResponse.success(showDetailsExistingMenuResponse);
    }

}
