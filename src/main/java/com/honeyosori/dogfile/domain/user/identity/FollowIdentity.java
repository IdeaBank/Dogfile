package com.honeyosori.dogfile.domain.user.identity;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class FollowIdentity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    private User followee;

    public FollowIdentity(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }
}
