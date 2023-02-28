package com.kotak.mb2.admin.administration.domain.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class CompletedActivityResponse {
        private String makerDate;
        private List<CompletedActivity> completedActivityList;
}
