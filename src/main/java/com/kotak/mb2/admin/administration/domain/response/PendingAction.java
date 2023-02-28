package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PendingAction {
    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("id")
    private String id;
    @JsonProperty("maker")
    private String maker;
    @JsonProperty("maker_ate")
    private String makerDate;
    @JsonProperty("action_type")
    private String actionType;
    @JsonProperty("status")
    private String status;
}
