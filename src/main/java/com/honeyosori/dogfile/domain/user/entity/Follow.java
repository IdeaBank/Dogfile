package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.user.identity.FollowIdentity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Follow {
    @EmbeddedId
    private FollowIdentity followIdentity;
}
