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
public class BlockIdentity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    private User blockee;

    public BlockIdentity(User blocker, User blockee) {
        this.blocker = blocker;
        this.blockee = blockee;
    }
}
