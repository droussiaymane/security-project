package com.kotak.mb2.admin.administration.controller;

import com.kotak.mb2.admin.administration.config.JWTUtils;
import com.kotak.mb2.admin.administration.domain.request.PendingActionsCountRequest;
import com.kotak.mb2.admin.administration.domain.response.PendingActionsCountResponse;
import com.kotak.mb2.admin.administration.service.PendingActionCountService;
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

import static com.kotak.mb2.admin.administration.constants.AdministrationTimedConstants.PENDING_ACTIONS_COUNT;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Tag(name = "Pending Action Count Controller")
@OpenAPIDefinition(tags = {
        @Tag(name = "PendingActionCount Controller",
                description = "This controller handle all the Pending Action Count related operations")
})
@RequestMapping("/administration/pending-actions")
@RequiredArgsConstructor
@Slf4j
public class PendingActionCountController {

    private final PendingActionCountService pendingActionCountService;
    private final JWTUtils jwtUtils;

    @PostMapping("/count")
    @Timed(PENDING_ACTIONS_COUNT)
    @Operation(description = "isValidUser", summary = "Perform isValidUser functionality")
    public ApiResponse<PendingActionsCountResponse> getPendingActionsCount(@Valid @RequestBody
                                                                           PendingActionsCountRequest request,
                                                          @RequestHeader(name="Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }
            return pendingActionCountService.getPendingActionsCount(request);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }
}
