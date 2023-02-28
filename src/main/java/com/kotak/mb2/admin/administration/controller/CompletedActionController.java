package com.kotak.mb2.admin.administration.controller;

import com.kotak.mb2.admin.administration.config.JWTUtils;
import com.kotak.mb2.admin.administration.domain.request.CompletedActivitiesRequest;
import com.kotak.mb2.admin.administration.domain.response.CompletedActivity;
import com.kotak.mb2.admin.administration.domain.response.DeptCompletedActivity;
import com.kotak.mb2.admin.administration.service.CompletedActivityService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kotak.mb2.admin.administration.constants.AdministrationTimedConstants.COMPLETED_ACTIVITIES;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@Tag(name = "Completed Action Controller")
@OpenAPIDefinition(tags = {
        @Tag(name = "Completed Action Controller",
                description = "This controller handle all the completed action related operations")
})
@RequiredArgsConstructor
@RequestMapping("/administration/completed-action")
@Slf4j
public class CompletedActionController {
    private final CompletedActivityService completedActivityService;
    private final JWTUtils jwtUtils;


    @PostMapping("/completed-activities")
    @Timed(COMPLETED_ACTIVITIES)
    @Operation(description = "getCompletedActivities", summary = "Perform getCompletedActivities functionality")
    public ApiResponse<List<CompletedActivity>> getCompletedActivities(@Valid @RequestBody
                                                                           CompletedActivitiesRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            log.info("Request received {}", request);
            final var username = jwtUtils.validateJwtToken(bearerToken, request.sessionUsername());
            if (StringUtils.isBlank(username)) {
                return ApiResponse.error(new ErrorResponse(UNAUTHORIZED.value(),
                        UNAUTHORIZED.getReasonPhrase()));
            }

            List<CompletedActivity> userCompletedData = completedActivityService.getUserCompletedActivities(request).getData();
            List<CompletedActivity> departmentCompletedData = completedActivityService.getDeptCompletedActivities(request).getData();
            List<CompletedActivity> customerCompletedData = completedActivityService.getCustomerCompletedActivities(request).getData();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            Comparator<CompletedActivity> comparator = Comparator.comparing(myData -> LocalDate.parse(myData.getMakerDate(), formatter).atStartOfDay(ZoneOffset.UTC)
                    .toOffsetDateTime());

            List<CompletedActivity> customerCompletedSortedData = Stream.of(userCompletedData,departmentCompletedData,customerCompletedData)
                    .flatMap(Collection :: stream).sorted(comparator.reversed()).collect(Collectors.toList());

            return ApiResponse.success(customerCompletedSortedData);
         } catch (Exception ex) {
            log.error("Exception occurred", ex);
            return ApiResponse.error(new ErrorResponse(SERVICE_UNAVAILABLE.value(),
                    SERVICE_UNAVAILABLE.getReasonPhrase()));
        }
    }
}
