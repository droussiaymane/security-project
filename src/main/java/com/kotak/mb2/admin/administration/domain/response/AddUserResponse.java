package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserResponse {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("status_code")
    private int statusCode;
    @JsonProperty("status_desc")
    private String statusDesc;
}
