package com.kotak.mb2.admin.administration.service;

import com.kotak.mb2.admin.administration.config.LdapClient;
import com.kotak.mb2.admin.administration.config.ServiceProperties;
import com.kotak.mb2.admin.administration.constants.AppConstants;
import com.kotak.mb2.admin.administration.domain.entity.*;
import com.kotak.mb2.admin.administration.domain.enums.Role;
import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.*;
import com.kotak.mb2.admin.administration.projection.EditDepartmentUserProjection;
import com.kotak.mb2.admin.administration.repository.*;
import com.kotak.mb2.admin.administration.service.makerchecker.AdminUserMakerCheckerManager;
import com.kotak.mb2.admin.administration.util.EncryptDecrypt;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.entity.AdminUserDeptRoles.*;
import static com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker.*;
import static com.kotak.mb2.admin.administration.domain.response.ShowDetailsUserResponse.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final LdapClient ldapClient;
    private final AdminUserDeptRolesRepository adminUserDeptRolesRepository;
    private final AdminUserMakerCheckerRepository adminUserMakerCheckerRepository;
    private final AdminDepartmentsRepository adminDepartmentsRepository;

    private final AdminRolesRepository adminRolesRepository;
    private final ServiceProperties serviceProperties;
    private final AdminUserMakerCheckerManager adminUserMakerCheckerManager;

    private final AddUserRepository addUserRepository;

    public ApiResponse<CheckValidUserResponse> isValidUser(CheckValidUserAPIRequest request) {
        final String username = request.username();
        log.debug("UserName {}", username);
        final Map<String, String> userData = ldapClient.getUserData(username);
        log.info("userData {} ",userData);
        if (CollectionUtils.isEmpty(userData)) {
            return ApiResponse.error(new ErrorResponse(NOT_FOUND.value(), String.valueOf(FAILED),
                    USER_NOT_FOUND));
        }
        final Long userCount = adminUserDeptRolesRepository.countByUsernameContainingIgnoreCase(username);

        if (userCount > 0) {
            log.info("User already exists {}", username);
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                    USER_ALREADY_EXISTS + userData.get("department")));
        }
        final Long pendingCount = adminUserMakerCheckerRepository.countByUsernameAndCheckerNullAndDeletedByNull(
                username);

        if (pendingCount > 0) {
            log.info("Requests already pending {}", username);
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                    USER_ALREADY_PENDING_FOR_APPROVAL + ": " + username));
        }
        return ApiResponse.success(CheckValidUserResponse.from(username, userData));
    }

    public ApiResponse<AddUserResponse> addUser(AddUserRequest addUserRequest) {
        try {
            log.debug("UserName {}", addUserRequest.username());
            var validUser = isValidUser(new CheckValidUserAPIRequest(
                    addUserRequest.username()));

            if (!validUser.isSuccessfulResponse()) {
                return ApiResponse.error(validUser.getError());
            }
            return saveDepartmentUser(getAdminUserDeptRoles(addUserRequest, validUser.getData()),
                    addUserRequest.sessionUsername(), addUserRequest.sessionRole());
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public JSONObject addOnboardingUser(JSONObject kmbRequest) {
        String reqData = null;
        JSONObject finalResponse = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("infoID", "0");
        response.put("data", new JSONObject());
        try {

            JSONObject request = (JSONObject) kmbRequest.get("request");
            String isRSA = request.get("isRSA").toString();
            if (isRSA.equalsIgnoreCase("false")){

                reqData = request.get("encryptedData").toString();

                reqData = EncryptDecrypt.decryptText(AppConstants.COMM0N_API_AES_ENC_KEY, reqData);
                System.out.println("reqData ::::: "+reqData);

            } else {
                reqData = request.get("encryptedData").toString();
            }

            JSONObject dataObj = new JSONObject(reqData);
            System.out.println("data obj : "+dataObj);

            String username = dataObj.getString("username");
            String dept = dataObj.optString("dept", null);
            String role = dataObj.optString("role", null);
//            logRequest(kmbRequest);
            String channelId = request.getString("channel_id");
            String requestID = request.getString("request_id");
            String maker = channelId + "_m";
            String checker = channelId + "_c";
            String ip =request.getString("ip");
            String action = dataObj.getString("action");
            String employeeNum = dataObj.optString("employeeNum", null);
            String p1 = dataObj.optString("P1", null);
            String p2 = dataObj.optString("P2", null);
            String p3 = dataObj.optString("P3", null);
            String p4 = dataObj.optString("P4", null);
            String p5 = dataObj.optString("P5", null);
//            kmbResponse.setRequestID(requestID);
            response.put("request_id", requestID);
            response.put("serverTime", request.getString("request_time"));
            response.put("svcName", "AddUser");
            response.put("svcGroup", "CommonAPI");
            response.put("msgID", String.valueOf(UUID.randomUUID()));

            if (!(Arrays.asList(AppConstants.ADD_USER_VALID_REQUEST_IP.split(",")).contains(ip))) {
                response.put("infoID", AppConstants.ADD_USER_INVALID_IP);
                response.put("infoMsg", ADD_USER_INVALID_IP_MSG);
            }

            if (!channelId.toUpperCase().matches(AppConstants.ADD_USER_CHANNEL_IDS)) {

                response.put("infoID", AppConstants.ADD_USER_CHANNEL_ID_INVALID);
                response.put("infoMsg", ADD_USER_CHANNEL_ID_INVALID_MSG);
            }

            if (response.getString("infoID").equalsIgnoreCase(AppConstants.SUCCESS_CODE)) {
                if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("deactivate")) {

                    response.put("infoID", AppConstants.INVALID_ACTION_TYPE);
                    response.put("infoMsg", INVALID_ACTION_TYPE_MSG);
                } else if (username == null || username.isEmpty()) {

                    response.put("infoID", AppConstants.USERNAME_VALIDATION);
                    response.put("infoMsg", USERNAME_VALIDATION_MSG);
                } else if ((!action.equalsIgnoreCase("deactivate")) && (dept == null || dept.isEmpty())) {

                    response.put("infoID", AppConstants.DEPT_VALIDATION);
                    response.put("infoMsg", DEPT_VALIDATION_MSG);

                } else if ((!action.equalsIgnoreCase("deactivate")) && (role == null || role.isEmpty())) {

                    response.put("infoID", AppConstants.ROLE_VALIDATION);
                    response.put("infoMsg", ROLE_VALIDATION_MSG);
                } else if (channelId == null || channelId.isEmpty()) {

                    response.put("infoID", AppConstants.CHANNEL_ID_VALIDATION);
                    response.put("infoMsg", CHANNEL_ID_VALIDATION_MSG);
                }
                AdminUserDeptRoles addUserRequestObj = null;
                Hashtable<String, String> userData = null;
                if (response.getString("infoID").equalsIgnoreCase(AppConstants.SUCCESS_CODE)) {
                    System.out.println("line no 192 : " + dept.toUpperCase());
                    Optional<AdminDepartments> deptByName = adminDepartmentsRepository.findByName(dept.toUpperCase());
                    System.out.println("line no 194 : " + deptByName);
                    Optional<AdminRoles> adminRoles = adminRolesRepository.findByRole(role.toLowerCase());

                    if (deptByName.isPresent()) {
                        AdminDepartments departmentName = deptByName.get();
                        dept = departmentName.getName();
                    }
                    if (adminRoles.isPresent()) {
                        AdminRoles roles = adminRoles.get();
                        role = roles.getRole();
                    }

                    userData = ldapClient.getUserData(username);

                    long userCount = adminUserMakerCheckerRepository.getUserCountByUsername(username);

                    boolean isValidUser;
                    if (userCount == 0) {
                        isValidUser = false;
                    } else {
                        isValidUser = true;
                    }

                    addUserRequestObj = getAdminUserDeptRolesForNewUser(username, role, userData);
                    JSONObject userResponse = null;

                    if (userData != null && userData.size() > 0 && !isValidUser &&
                            !action.equalsIgnoreCase("deactivate")) {
                        userResponse = createOnboardUser(addUserRequestObj, action);
                        response.put("infoID", userResponse.get("infoID").toString());
                        response.put("infoMsg", userResponse.getString("infoMsg"));
                    } else if (isValidUser && !action.equalsIgnoreCase("deactivate")) {
                        userResponse = modifyOnboardUser(addUserRequestObj);
                        response.put("infoID", userResponse.get("infoID").toString());
                        response.put("infoMsg", userResponse.getString("infoMsg"));
                    } else if (isValidUser && action.equalsIgnoreCase("deactivate")) {
                        userResponse = revokeOnboardUser(addUserRequestObj);
                        userResponse = revokeOnboardRoleDept(addUserRequestObj);
                        response.put("infoID", userResponse.get("infoID").toString());
                        response.put("infoMsg", userResponse.getString("infoMsg"));
                    } else {
                        response.put("infoID", INVALID_USR);
                        response.put("infoMsg", INVALID_USR_MSG);
                    }
                }

                // add code for logging purpose
                addUserRepository.save(AddUser.from(addUserRequestObj, maker, checker, ip, userData, response));

            }

        } catch (Exception e) {
            log.error("Exception::", e);
            response.put("infoID", INTERNAL_SERVER_ERROR.value());
            response.put("infoMsg", INTERNAL_SERVER_ERROR.getReasonPhrase());
            return response;

        }
        finalResponse.put("response", response);
        System.out.println("finalResponse in service : "+finalResponse);
        return finalResponse;
    }

    @Transactional
    private JSONObject revokeOnboardRoleDept(AdminUserDeptRoles addUserRequestObj) {
        JSONObject userResponse = new JSONObject();
        try {
            adminUserMakerCheckerRepository.revokeOnboardRoleDept(addUserRequestObj.getUsername());
            userResponse.put("infoID", SUCCESS);
            userResponse.put("infoMsg", USER_DEACTIVATE_MSG);

            return userResponse;
        } catch (Exception e) {
            log.error("Exception::", e);
            userResponse.put("infoID", INTERNAL_SERVER_ERROR.value());
            userResponse.put("infoMsg", INTERNAL_SERVER_ERROR.getReasonPhrase());
            return userResponse;
        }
    }

    @Transactional
    private JSONObject revokeOnboardUser(AdminUserDeptRoles addUserRequestObj) {
        JSONObject userResponse = new JSONObject();
        try {
            adminUserMakerCheckerRepository.revokeOnboardUser(addUserRequestObj.getUsername());
            userResponse.put("infoID", SUCCESS);
            userResponse.put("infoMsg", USER_DEACTIVATE_MSG);
            return userResponse;
        } catch (Exception e) {
            log.error("Exception::", e);
            userResponse.put("infoID", INTERNAL_SERVER_ERROR.value());
            userResponse.put("infoMsg", INTERNAL_SERVER_ERROR.getReasonPhrase());
            return userResponse;
        }


    }

    @Transactional
    public JSONObject modifyOnboardUser(AdminUserDeptRoles addUserRequestObj) {
        JSONObject userResponse = new JSONObject();

        boolean checkForRoleDept = checkForRoleDept(addUserRequestObj.getUsername(), addUserRequestObj.getDept(),
                addUserRequestObj.getRole());

        if (checkForRoleDept) {
            adminUserMakerCheckerRepository.updateModifiedOnboardUser(addUserRequestObj.getEmpCode(),
                    addUserRequestObj.getDisplayName(), addUserRequestObj.getCompany(), addUserRequestObj.getAdDept(),
                    addUserRequestObj.getGivenName(), addUserRequestObj.getSn(), addUserRequestObj.getMail(),
                    addUserRequestObj.getMobile(), addUserRequestObj.getTelephoneNo(), addUserRequestObj.getUsername());

        } else {

            adminUserMakerCheckerRepository.updateModifiedOnboardUserForRoleAndDept(addUserRequestObj.getRole(),
                    addUserRequestObj.getDept(), addUserRequestObj.getUsername());

        }

        userResponse.put("infoID", SUCCESS);
        userResponse.put("infoMsg", USER_ADDED_MSG);

        return userResponse;
    }

    private boolean checkForRoleDept(String username, String dept, String role) {
        Boolean checkForRoleDept = false;
        Long roleDept = adminUserMakerCheckerRepository.getAdminUserMakerCheckerRoleDeptDetails(username, role, dept);
        if (roleDept == 0){
            checkForRoleDept = false;
        } else {
            checkForRoleDept = true;
        }
        return checkForRoleDept;
    }

    @Transactional
    public JSONObject createOnboardUser(AdminUserDeptRoles addUserRequestObj, String action) {

        JSONObject userResponse = new JSONObject();
        try {
            final String username = addUserRequestObj.getUsername();
            log.info("Adding user in database {}", username);
            final var dept = addUserRequestObj.getDept();
            final Long deptCount = adminDepartmentsRepository.countByName(dept);

            if (deptCount == 0) {
                log.info("Department doesn't exist {}", dept);

                userResponse.put("infoID", FAILED);
                userResponse.put("infoMsg", INVALID_DEPT);
                return userResponse;

            }

            final Long userCount = adminUserDeptRolesRepository.countByUsernameContainingIgnoreCase(username);

            if (userCount > 0) {
                log.info("User already exists {}", username);
                userResponse.put("infoID", INVALID_DEPT);
                userResponse.put("infoMsg", INVALID_DEPT_MSG);
                return userResponse;

            }
            final Long pendingCount = adminUserMakerCheckerRepository.countByUsernameAndCheckerNullAndDeletedByNull(
                    username);

            if (pendingCount > 0) {
                log.info("Requests already pending {}", username);
                userResponse.put("infoID", PENDING_REQUEST_CODE);
                userResponse.put("infoMsg", PENDING_REQUEST_MSG);
                return userResponse;

            }

            final var savedAdminUserMakerChecker = adminUserMakerCheckerRepository.save(
                    getAdminUserMakerCheckerForOnboardUser(addUserRequestObj, username));

            final String userId = savedAdminUserMakerChecker.getId();
            log.debug("User added successfully {}", username);

            final Optional<AdminUserDeptRoles> adminUserDeptRolesOptional = adminUserDeptRolesRepository.
                    findByUsername(username);

            if (adminUserDeptRolesOptional.isEmpty()) {
                addUserRequestObj.setStatus(STATUS_ACTIVE);
                adminUserDeptRolesRepository.save(addUserRequestObj);
            } else if (adminUserDeptRolesOptional.isPresent()) {
                    final AdminUserDeptRoles adminUserDeptRoles = AdminUserDeptRoles.
                            updateAdminUserDeptRolesForOnboardUser(adminUserDeptRolesOptional.get(), addUserRequestObj);
                    adminUserDeptRolesRepository.save(adminUserDeptRoles);
            }
            log.info("User is approved {}", username);
            userResponse.put("infoID", SUCCESS);
            userResponse.put("infoMsg", USER_ADDED_MSG);
            return userResponse;

        } catch (Exception e) {
            log.error("Exception::", e);
            userResponse.put("infoID", INTERNAL_SERVER_ERROR.value());
            userResponse.put("infoMsg", INTERNAL_SERVER_ERROR.getReasonPhrase());
            return userResponse;
        }
    }

    private AddUserObject getUserDataObject(String username, String dept, String role, String ip, String maker,
                                            String checker, Hashtable<String, String> userData) {
        AddUserObject addUserObj = new AddUserObject();
        addUserObj.setUsername(username);
        addUserObj.setDept(dept);
        addUserObj.setRole(role);
        addUserObj.setMaker(maker);
        addUserObj.setChecker(checker);
        addUserObj.setIp(ip);
        if (userData != null && userData.size() > 0 && userData.get("employeeNumber") != null
                && !(userData.get("employeeNumber").isEmpty())) {
            addUserObj.setEmployeeNumber(userData.get("employeeNumber"));
            addUserObj.setDisplayName(userData.get("displayName"));
            addUserObj.setCompany(userData.get("company"));
            addUserObj.setDepartment(userData.get("department"));
            addUserObj.setGivenName(userData.get("givenName"));
            addUserObj.setSn(userData.get("sn"));
            addUserObj.setMail(userData.get("mail"));
            addUserObj.setMobile(userData.get("mobile"));
            addUserObj.setTelephoneNumber(userData.get("telephoneNumber"));
        }
        return addUserObj;
    }

    @Transactional
    private ApiResponse<AddUserResponse> saveDepartmentUser(AdminUserDeptRoles deptUser,
                                                            String sessionUsername,
                                                            String sessionRole) {
        final String username = deptUser.getUsername();
        log.info("Adding user in database {}", username);
        final var dept = deptUser.getDept();
        final Long deptCount = adminDepartmentsRepository.countByName(dept);

        if (deptCount == 0) {
            log.info("Department doesn't exist {}", dept);
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                    DEPT_NOT_EXISTS));
        }
        final var savedAdminUserMakerChecker = adminUserMakerCheckerRepository.save(
                getAdminUserMakerChecker(deptUser, sessionUsername));

        final String userId = savedAdminUserMakerChecker.getId();
        log.debug("User added successfully {}", username);

        if (ROLE_ADMIN.equalsIgnoreCase(sessionRole)) {
            ApiResponse<Void> response = approveDepartmentUser(List.of(new ApproveRequest(userId, sessionUsername,
                    null,ADMIN_DEPT)));
            if (!response.isSuccessfulResponse()) return ApiResponse.error(response.getError());
            log.info("User is approved {}", username);
            return ApiResponse.success(new AddUserResponse(userId, SUCCESS, USER_ADDED_AND_APPROVED));
        } else {
            log.info("Request sent for approval {}", username);
            return ApiResponse.success(new AddUserResponse(userId, SUCCESS,
                    serviceProperties.getMsgSentApproval()));
        }
    }

    @Transactional
    public ApiResponse<Void> approveDepartmentUser(@Valid List<ApproveRequest> request) {
        try {
            return adminUserMakerCheckerManager.approveBulk(request);
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ApiResponse<List<ListUserResponse>> getUserList() {
        try {
            final var users = adminUserDeptRolesRepository.getUsers();
            return ApiResponse.success(users.stream().map(ListUserResponse::getUserResponse).toList());
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ApiResponse<List<UserPendingAction>> getPendingActions(PendingActionsRequest request) {
        try {
            log.debug("UserName {}", request.sessionUsername());
            if (request.sessionRole().equalsIgnoreCase(Role.CHECKER.name())
                    && StringUtils.isEmpty(request.sessionDeptName())) {
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                        "session_dept_name " + SHOULD_NOT_BE_BLANK));
            }
            return ApiResponse.success(adminUserMakerCheckerManager.getPendingActions(request));
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    @Transactional
    public ApiResponse<EditUserResponse> editDepartmentUser(EditUserRequest request) {
        final String sessionUsername = request.sessionUsername();
        try {
            final String username = request.username();
            if (Objects.equals(username, sessionUsername)) {
                log.info("You cannot edit your details {}", username);
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                        CANNOT_EDIT_OWN_DETAILS));
            }
            final ApiResponse<EditDepartmentUserProjection> apiResponse = validateEditUser(request);

            if (!apiResponse.isSuccessfulResponse()) return ApiResponse.error(apiResponse.getError());
            final AdminUserMakerChecker adminUserMakerCheckerForEditUser =
                    getAdminUserMakerCheckerForEditUser(apiResponse.getData(), request);
            final AdminUserMakerChecker saved = adminUserMakerCheckerRepository.save(adminUserMakerCheckerForEditUser);
            final String userMakerCheckerId = saved.getId();
            log.debug("Entry added successfully for edit user with id: {}", userMakerCheckerId);

            final String sessionRole = request.sessionRole();
            if (ROLE_ADMIN.equalsIgnoreCase(sessionRole)) {
                final ApiResponse<Void> response = adminUserMakerCheckerManager.approve(new ApproveRequest(
                        userMakerCheckerId, sessionUsername, null,ADMIN_DEPT));
                if (!response.isSuccessfulResponse()) return ApiResponse.error(response.getError());
                log.info("User is Edited successfully {}", request.username());
                return ApiResponse.success(new EditUserResponse(userMakerCheckerId, SUCCESS,
                        USER_EDITED_SUCCESSFULLY));
            } else {
                log.info("Edit request sent for approval");
                return ApiResponse.success(new EditUserResponse(userMakerCheckerId, SUCCESS,
                        serviceProperties.getMsgSentApproval()));
            }
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    private ApiResponse<EditDepartmentUserProjection> validateEditUser(EditUserRequest request) {
        final String username = request.username();
        final String dept = request.dept();

        final Long pendingCount = adminUserMakerCheckerRepository.countByUsernameAndCheckerNullAndDeletedByNull(
                username);

        if (pendingCount > 0) {
            log.info("Request already pending for approval");
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                    REQUEST_ALREADY_PENDING_FOR_APPROVAL));
        }
        final EditDepartmentUserProjection userDetails = adminUserDeptRolesRepository.editDepartmentUser(
                dept, username);

        if (Objects.isNull(dept) && !userDetails.getCurrDeptStatus().equals(STATUS_ACTIVE)) {
            log.info("Invalid Department");
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                    INVALID_DEPT));
        }

        if (Objects.nonNull(dept) && !userDetails.getReqDeptStatus().equals(STATUS_ACTIVE)) {
            log.info("Invalid Department");
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                    INVALID_DEPT));
        }
        return ApiResponse.success(userDetails);
    }

    @Transactional
    public ApiResponse<DeleteUserResponse> deleteDepartmentUser(DeleteUserRequest request) {
        final String sessionUsername = request.sessionUsername();
        final String sessionRole = request.sessionRole();
        try {
            log.debug("userId:: {}", request.userId());
            final Optional<AdminUserDeptRoles> userById = adminUserDeptRolesRepository.findById(request.userId());

            if (userById.isEmpty()) {
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(), String.valueOf(FAILED),
                        USER_NOT_FOUND + "with" + request.userId()));
            }
            final var savedAdminUserMakerChecker = adminUserMakerCheckerRepository.save(
                    deleteAdminUserMakerChecker(userById.get(), sessionUsername));
            final String userMakerCheckerId = savedAdminUserMakerChecker.getId();
            log.debug("Entry added successfully for delete user with id: {}", userMakerCheckerId);

            if (ROLE_ADMIN.equalsIgnoreCase(sessionRole)) {
                final ApiResponse<Void> response = adminUserMakerCheckerManager.approve(new ApproveRequest(
                        userMakerCheckerId, sessionUsername, null,ADMIN_DEPT));
                if (!response.isSuccessfulResponse()) return ApiResponse.error(response.getError());
                log.info("User is deleted successfully with id: {}", request.userId());
                return ApiResponse.success(new DeleteUserResponse(userMakerCheckerId, SUCCESS,
                        USER_DELETED_SUCCESSFULLY));
            } else {
                log.info("Delete request sent for approval");
                return ApiResponse.success(new DeleteUserResponse(userMakerCheckerId, SUCCESS,
                        serviceProperties.getMsgSentApproval()));
            }
        } catch (Exception e) {
            log.error("Exception::", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    @Transactional
    public ApiResponse<Void> rejectDepartmentUser(@Valid List<RejectRequest> request) {
        try {
            return adminUserMakerCheckerManager.rejectBulk(request);
        } catch (Exception e) {
            log.error("Exception", e);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }

    public ApiResponse<ActivateDelUserResponse> activateDelUser(ActivateDelUserRequest activateDelUserRequest) {
        try {

            final var userId = activateDelUserRequest.userId();
            final var users = adminUserDeptRolesRepository.findById(userId);
            if (users.isEmpty()) {
                log.info("User does not exist {}", userId);
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                        String.valueOf(FAILED), USER_NOT_FOUND));
            }
            final var adminUsers = users.get();
            final var userName = adminUsers.getUsername();
            final var pendingCount = adminUserMakerCheckerRepository.countByUsernameAndCheckerNullAndDeletedByNull(
                    userName);

            if (pendingCount > 0) {
                log.info("Request already pending for approval");
                return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                        String.valueOf(FAILED), USER_ALREADY_PENDING_FOR_APPROVAL));
            }

            final var sessionRole = activateDelUserRequest.sessionRole();
            final var response = adminUserMakerCheckerRepository.save(
                    getAdminUserMakerCheckerForActivateDelUser(users.get(), activateDelUserRequest));

            log.debug("User edit successful");
            if (ROLE_ADMIN.equalsIgnoreCase(sessionRole)) {
                final ApiResponse<Void> approveResponse = adminUserMakerCheckerManager.approve(
                        new ApproveRequest(response.getId(), activateDelUserRequest.sessionUsername(),
                                activateDelUserRequest.description(),ADMIN_DEPT));
                if (approveResponse.isSuccessfulResponse()) {
                    log.info("User is updated.. {}", userId);
                    return ApiResponse.success(new ActivateDelUserResponse(SUCCESS, SUCCESS_DESC));
                } else {
                    return ApiResponse.error(approveResponse.getError());
                }
            } else {
                return ApiResponse.success(new ActivateDelUserResponse(SUCCESS, serviceProperties.getMsgSentApproval()));
            }

        } catch (Exception ex) {
            log.error("Exception", ex);
            return ApiResponse.error(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                    INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }

    }

    public ApiResponse<ShowPendingUserDetails> ShowDetails(ShowDetailsRequest request) {
        final var userId = request.id();
        final var userName = request.name();
        final Optional<AdminUserMakerChecker> adminUserMakerChecker = adminUserMakerCheckerRepository.findById(userId);
        if(adminUserMakerChecker.isEmpty()){
            log.info("User does not exist {}", userId);
            return ApiResponse.error(new ErrorResponse(BAD_REQUEST.value(),
                    String.valueOf(FAILED), USER_NOT_FOUND));
        }
        final Optional<AdminUserMakerChecker> existingUserMakerChecker =
                adminUserMakerCheckerRepository.findFirstByUsernameAndCheckerNotNullOrderByCreatedAtDesc(userName);

        final var requestedData = adminUserMakerChecker.stream().map(aumc -> from(aumc,REQUESTED_DETAILS));
        final var existingData = existingUserMakerChecker.stream().map(aumc -> from(aumc,EXISTING_DETAILS));

        return ApiResponse.success(new ShowPendingUserDetails(existingData,requestedData));
    }
}
