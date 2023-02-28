package com.kotak.mb2.admin.administration.controller;

import com.kotak.mb2.admin.administration.config.JWTUtils;
import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.*;
import com.kotak.mb2.admin.administration.service.DepartmentService;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.kotak.mb2.admin.administration.constants.AdministrationTimedConstants.*;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Tag(name = "Department Controller")
@OpenAPIDefinition(tags = {
        @Tag(name = "Department Controller",
                description = "This controller handle all the department related operations")
})
@RequiredArgsConstructor
@RequestMapping("/administration/department")
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;
    private final JWTUtils jwtUtils;

    @PostMapping("/getDepartments")
    @Timed(GET_DEPARTMENTS)
    @Operation(description = "getDepartments", summary = "Perform getDepartments functionality")
    public ApiResponse<List<GetDepartmentResponse>> getDepartments(@Valid @RequestBody GetDepartmentRequest request,
                                                                   @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received");
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.getDepartments();
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/addDepartment")
    @Timed(ADD_DEPARTMENT)
    @Operation(description = "addDepartment", summary = "Perform addDepartment functionality")
    public ApiResponse<AddDepartmentResponse> addDepartment(@Valid @RequestBody AddDepartmentRequest
                                                                    addDepartmentRequest,
                                                            @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", addDepartmentRequest);
            log.info("bearerToken {} ", bearerToken);
            final var username = jwtUtils.validateJwtToken(bearerToken, addDepartmentRequest.sessionUsername());
            log.info("username {} ", username);

            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.addDepartment(addDepartmentRequest);

        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/editDepartment")
    @Timed(EDIT_DEPARTMENT)
    @Operation(description = "editDepartment", summary = "Perform editDepartment functionality")
    public ApiResponse<EditDepartmentResponse> editDepartment(@Valid @RequestBody EditDepartmentRequest
                                                                      editDepartmentRequest,
                                                              @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", editDepartmentRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken, editDepartmentRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.editDepartment(editDepartmentRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/deleteDepartment")
    @Timed(DELETE_DEPARTMENT)
    @Operation(description = "deleteDepartment", summary = "Perform deleteDepartment functionality")
    public ApiResponse<DeleteDepartmentResponse> deleteDepartment(@Valid @RequestBody DeleteDepartmentRequest
                                                                          deleteDepartmentRequest, @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", deleteDepartmentRequest);
            final var username= jwtUtils.validateJwtToken(bearerToken,deleteDepartmentRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.deleteDepartment(deleteDepartmentRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/activateDelDept")
    @Timed(ACTIVATE_DEL_DEPARTMENT)
    @Operation(description = "activateDelDept", summary = "Perform activateDelDept functionality")
    public ApiResponse<ActivateDelDepartmentResponse> activateDelDepartment(
            @Valid @RequestBody ActivateDelDepartmentRequest activateDelDepartmentRequest,
            @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", activateDelDepartmentRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken,
                    activateDelDepartmentRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.activateDelDept(activateDelDepartmentRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/listDeptAccess")
    @Timed(LIST_DEPT_ACCESS)
    @Operation(description = "listDeptAccess", summary = "Perform listDeptAccess functionality")
    ApiResponse<ListDepartmentAccessResponse> listDeptAccess(@Valid @RequestBody ListDepartmentAccessRequest
                                                                     listDepartmentAccessRequest,
                                                             @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", listDepartmentAccessRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken, listDepartmentAccessRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.listDeptAccess(listDepartmentAccessRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/updateDeptAccess")
    @Timed(UPDATE_DEPT_ACCESS)
    @Operation(description = "updateDeptAccess", summary = "Perform updateDeptAccess functionality")
    ApiResponse<UpdateDepartmentAccessResponse> updateDeptAccess(@Valid @RequestBody UpdateDepartmentAccessRequest
                                                                         updateDepartmentAccessRequest,
                                                                 @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", updateDepartmentAccessRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken, updateDepartmentAccessRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.updateDeptAccess(updateDepartmentAccessRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/getDepartmentUsers")
    @Timed(GET_DEPT_USERS)
    @Operation(description = "getDepartmentUsers", summary = "Perform getDepartmentUsers functionality")
    ApiResponse<GetDeptUsersResponse> getDepartmentUsers(@Valid @RequestBody GetDeptUsersRequest getDeptUsersRequest,
                                                         @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", getDeptUsersRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken, getDeptUsersRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.getDepartmentUsers(getDeptUsersRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/pending-actions")
    @Timed(GET_PENDING_ACTIONS)
    @Operation(description = "getPendingActions", summary = "Perform getPendingActions functionality")
    public ApiResponse<List<DeptPendingAction>> getPendingActions(@Valid @RequestBody PendingActionsRequest request,
                                                                  @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.getPendingActions(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/approve")
    @Timed(APPROVE_DEPARTMENT)
    @Operation(description = "approveDepartment", summary = "Perform approveDepartment functionality")
    ApiResponse<Void> approveDepartment(@Valid @RequestBody List<ApproveRequest> request,
                                        @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            String sessionUsername = null;
            for (ApproveRequest approveRequest : request) {
                sessionUsername = approveRequest.sessionUsername();
            }
            final var username = jwtUtils.validateJwtToken(bearerToken, sessionUsername);
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.approveDepartment(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }


    @PostMapping("/reject")
    @Timed(REJECT_DEPARTMENT)
    @Operation(description = "rejectDepartment", summary = "Perform rejectDepartment functionality")
    ApiResponse<Void> rejectDepartment(@Valid @RequestBody List<RejectRequest> request,
                                       @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            String session_username = null;
            for (RejectRequest rejectRequest: request) {
                session_username = rejectRequest.sessionUsername();
            }
            final var username = jwtUtils.validateJwtToken(bearerToken, session_username);
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.rejectDepartment(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/showDetails")
    @Timed(SHOW_DETAILS)
    @Operation(description = "showDetails", summary = "Perform showDetails functionality")
    public ApiResponse<ShowPendingDeptDetails> showDetails(@Valid @RequestBody ShowDetailsRequest request,
                                                           @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.ShowDetails(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/showMenuDetails")
    @Timed(SHOW_MENU_DETAILS)
    @Operation(description = "showMenuDetails", summary = "Perform showMenuDetails functionality")
    public ApiResponse<List<ShowDetailsMenuResponse>> showMenuDetails(@Valid @RequestBody showMenuDetailsRequest request,
                                                                      @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return departmentService.showMenuDetails(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }
}
