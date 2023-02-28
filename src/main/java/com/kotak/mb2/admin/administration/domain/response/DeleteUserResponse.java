package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteUserResponse(
        @JsonProperty("request_id")
        String requestId,
        @JsonProperty("status_code")
        int statusCode,
        @JsonProperty("status_desc")
        String statusDesc) {
}
