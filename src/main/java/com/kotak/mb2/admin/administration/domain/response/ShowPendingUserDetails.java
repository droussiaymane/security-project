package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ShowPendingUserDetails(
        @JsonProperty("existing_Data") java.util.stream.Stream<ShowDetailsUserResponse> existingData,
        @JsonProperty("requested_data") java.util.stream.Stream<ShowDetailsUserResponse> requestedData) {

}
