package com.kotak.mb2.admin.administration.controller;

import com.kotak.mb2.admin.administration.config.JWTUtils;
import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.*;
import com.kotak.mb2.admin.administration.service.DepartmentService;
import com.kotak.mb2.admin.administration.service.UserService;
import com.kotak.mb2.rest.commons.ApiResponse;
import com.kotak.mb2.rest.commons.ErrorResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kotak.mb2.admin.administration.constants.AdministrationTimedConstants.*;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Tag(name = "User Controller")
@OpenAPIDefinition(tags = {
        @Tag(name = "User Controller",
                description = "This controller handle all the user related operations")
})
@RequestMapping("/administration/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final JWTUtils jwtUtils;

    @PostMapping("/is-valid")
    @Timed(CHECK_VALID_USER)
    @Operation(description = "isValidUser", summary = "Perform isValidUser functionality")
    public ApiResponse<CheckValidUserResponse> isValidUser(@Valid @RequestBody CheckValidUserRequest request,
                                                           @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);

            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());

            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.isValidUser(new CheckValidUserAPIRequest(request.username()));

        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/add")
    @Timed(ADD_DEPARTMENT_USER)
    @Operation(description = "addDepartmentUser", summary = "Perform addDepartmentUser functionality")
    public ApiResponse<AddUserResponse> addDepartmentUser(@Valid @RequestBody AddUserRequest addUserRequest,
                                                          @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", addUserRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken, addUserRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.addUser(addUserRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/list")
    @Timed(LIST_ALL_USERS)
    @Operation(description = "listAllUsers", summary = "Perform listAllUsers functionality")
    public ApiResponse<List<ListUserResponse>> listAllUsers(@Valid @RequestBody ListUserRequest request,
                                                            @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received");
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.getUserList();
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/pending-actions")
    @Timed(GET_PENDING_ACTIONS)
    @Operation(description = "getPendingActions", summary = "Perform getPendingActions functionality")
    public ApiResponse<List<PendingAction>> getPendingActions(@Valid @RequestBody
                                                                      PendingActionsRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            List<DeptPendingAction> departmentPendingActions = departmentService.getPendingActions(request).getData();
            List<UserPendingAction> userPendingActions =  userService.getPendingActions(request).getData();
            log.info("data fetched successfully");
            return ApiResponse.success(Stream.of(departmentPendingActions,userPendingActions)
                    .flatMap(Collection::stream).collect(Collectors.toList()));
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));

        }
    }

    @PostMapping("/approve")
    @Timed(APPROVE_DEPT_USER)
    @Operation(description = "approveDepartmentUser", summary = "Perform approveDepartmentUser functionality")
    public ApiResponse<Void> approveDepartmentUser(@Valid @RequestBody List<ApproveRequest> request,
                                                   @RequestHeader("Authorization") String bearerToken) {
        try {
            String sessionUsername = null;
            log.info("Request received {}", request);
            for (ApproveRequest approveRequest : request) {
                sessionUsername = approveRequest.sessionUsername();
            }
            final var username = jwtUtils.validateJwtToken(bearerToken, sessionUsername);
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.approveDepartmentUser(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/edit")
    @Timed(EDIT_DEPT_USER)
    @Operation(description = "editDepartmentUser", summary = "Perform editDepartmentUser functionality")
    public ApiResponse<EditUserResponse> editDepartmentUser(@Valid @RequestBody EditUserRequest request,
                                                            @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.editDepartmentUser(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/delete")
    @Timed(DELETE_DEPT_USER)
    @Operation(description = "deleteDepartmentUser", summary = "Perform deleteDepartmentUser functionality")
    public ApiResponse<DeleteUserResponse> deleteDepartmentUser(@Valid @RequestBody DeleteUserRequest request,
                                                                @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.deleteDepartmentUser(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/reject")
    @Timed(REJECT_DEPT_USER)
    @Operation(description = "rejectDepartmentUser", summary = "Perform rejectDepartmentUser functionality")
    public ApiResponse<Void> rejectDepartmentUser(@Valid @RequestBody List<RejectRequest> request,
                                                  @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            String sessionUsername = null;
            for (RejectRequest rejectRequest: request) {
                sessionUsername = rejectRequest.sessionUsername();
            }
            final var username = jwtUtils.validateJwtToken(bearerToken, sessionUsername);
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.rejectDepartmentUser(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/activateDelUser")
    @Timed(ACTIVATE_DEL_USER)
    @Operation(description = "activateDelUser", summary = "Perform activateDelUser functionality")
    public ApiResponse<ActivateDelUserResponse> activateDelUser(
            @Valid @RequestBody ActivateDelUserRequest activateDelUserRequest,
            @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", activateDelUserRequest);
            final var username = jwtUtils.validateJwtToken(bearerToken, activateDelUserRequest.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.activateDelUser(activateDelUserRequest);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }

    @PostMapping("/addOnboardUser")
    @Operation(description = "addOnboardUser", summary = "Perform addDepartmentUser functionality")
    public String addOnboardUser(@RequestBody String kmbRequests) {
        JSONObject response = null;
        System.out.println("kmb : "+kmbRequests);

        try {
            log.info("Request received {}", kmbRequests);
            JSONObject jsonObject = new JSONObject(kmbRequests);
            System.out.println("req : "+jsonObject);
            response = userService.addOnboardingUser(jsonObject);
            return response.toString();
            //ApiResponse.success(response);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
//            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
//                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
        return null;
    }

    @PostMapping("/showDetails")
    @Timed(SHOW_DETAILS)
    @Operation(description = "showDetails", summary = "Perform showDetails functionality")
    public ApiResponse<ShowPendingUserDetails> showDetails(@Valid @RequestBody ShowDetailsRequest request,
                                                           @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return userService.ShowDetails(request);
        } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }
}