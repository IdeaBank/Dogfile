package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record FollowDto(@JsonInclude Long followerId,
                        @JsonInclude Long followeeId) {
}
