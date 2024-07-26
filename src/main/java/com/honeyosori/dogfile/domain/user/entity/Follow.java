package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.user.identity.FollowIdentity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Follow {
    @EmbeddedId
    private FollowIdentity followIdentity;

    public Follow(FollowIdentity followIdentity) {
        this.followIdentity = followIdentity;
    }
}
