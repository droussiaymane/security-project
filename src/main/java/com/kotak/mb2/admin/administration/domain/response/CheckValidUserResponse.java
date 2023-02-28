package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

public record CheckValidUserResponse(
        @JsonProperty("username") String username,
        @JsonProperty("emp_code") String empCode,
        @JsonProperty("display_name") String displayName,
        @JsonProperty("company") String company,
        @JsonProperty("department") String department,
        @JsonProperty("given_name") String givenName,
        @JsonProperty("sn") String sn,
        @JsonProperty("mail") String mail,
        @JsonProperty("mobile") String mobile,
        @JsonProperty("telephone_number") String telephoneNumber) {

    @Builder
    public CheckValidUserResponse {
    }

    public static CheckValidUserResponse from(String username, Map<String, String> userData) {
        return CheckValidUserResponse.builder()
                .username(username)
                .empCode(userData.get("employeeNumber"))
                .displayName(userData.get("displayName"))
                .company(userData.get("company"))
                .department(userData.get("department"))
                .givenName(userData.get("givenName"))
                .sn(userData.get("sn"))
                .mail(userData.get("mail"))
                .mobile(userData.get("mobile"))
                .telephoneNumber(userData.get("telephoneNumber"))
                .build();
    }
}
