package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivateDelUserResponse {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("status_desc")
    private String statusDesc;
}
