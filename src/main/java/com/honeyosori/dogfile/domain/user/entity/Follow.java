package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.user.identity.FollowIdentity;
import jakarta.persistence.*;

@Entity
public class Follow {
    @EmbeddedId
    private FollowIdentity followIdentity;
}
