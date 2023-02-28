package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PendingActionsCountResponse(
        @JsonProperty("total_count") long totalCount,
        @JsonProperty("other_request_count") long otherCount,
        @JsonProperty("block_count") long blockCount,
        @JsonProperty("unblock_count") long unblockCount,
        @JsonProperty("activate_count") long activateCount,
        @JsonProperty("deactivate_count") long deactivateCount,
        @JsonProperty("mpin_unlock_count") long mpinUnlockCount
        ) {
}
