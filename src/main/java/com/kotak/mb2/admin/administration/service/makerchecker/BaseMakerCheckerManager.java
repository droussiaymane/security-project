package com.kotak.mb2.admin.administration.service.makerchecker;

import com.kotak.mb2.admin.administration.domain.request.*;
import com.kotak.mb2.admin.administration.domain.response.CompletedActivity;
import com.kotak.mb2.rest.commons.ApiResponse;

import java.util.List;

public interface BaseMakerCheckerManager<T, U> {

    ApiResponse<Void> approve(ApproveRequest request);

    List<T> getPendingActions(PendingActionsRequest request);

    long getPendingActionsCount(PendingActionsCountRequest request);

    List<CompletedActivity> getCompletedActivities(CompletedActivitiesRequest request);

    ApiResponse<Void> reject(RejectRequest request);

    long getCustomerPendingActionsCount(PendingActionsCountRequest request ,List<String> type);

    ApiResponse<Void> approveBulk(List<ApproveRequest> request);

    ApiResponse<Void> rejectBulk(List<RejectRequest> request);
}
