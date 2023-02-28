package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.domain.entity.CustomerMakerChecker;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;

@SuperBuilder
@Getter
public class CustomerCompletedActivity extends CompletedActivity{
    private String crn;

    public static CompletedActivity from(CustomerMakerChecker customerMakerChecker) {
        return CustomerCompletedActivity.builder()
                .maker(customerMakerChecker.getMaker())
                .crn(customerMakerChecker.getCrn())
                .makerDate(customerMakerChecker.getMakerDate().format(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss")))
                .makerComments(customerMakerChecker.getMakerComments())
                .makerAction(getMakerAction(customerMakerChecker.getAction()))
                .authorizedDate(Objects.nonNull(customerMakerChecker.getCheckerDate())
                        ? customerMakerChecker.getCheckerDate().toString()
                        : Objects.nonNull(customerMakerChecker.getDeletedAt())
                        ? customerMakerChecker.getDeletedAt().toString() : null)
                .authorizedBy(Objects.nonNull(customerMakerChecker.getChecker())
                        ? customerMakerChecker.getChecker() : customerMakerChecker.getDeletedBy())
                .authorizedAction(getAuthorizedAction(customerMakerChecker.getStatus()))
                .authorizedComments(customerMakerChecker.getCheckerComments())
                .crn(customerMakerChecker.getCrn())
                .type(customerMakerChecker.getType())
                .module(CUSTOMER_MANAGEMENT)
                .build();
    }

    private static String getMakerAction(String action) {
        if (Objects.isNull(action)) return null;
        if (action.equals(ADD_CRN)) return "ADD";
        if (action.equals(ACTIVATE_DEVICE) || action.equals(BLOCK_DEVICE) || action.equals(CRN_GN_MPIN_ACT)) return "REQ";
        else return "UPD";
    }

    private static String getAuthorizedAction(String status) {
        if (Objects.isNull(status)) return "WITH";
        if (status.equals(STATUS_ACTIVE) || status.equals(STATUS_DELETE)) return STATUS_ACTIVE;
        else return "REJ";
    }
}
