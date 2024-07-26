package com.honeyosori.dogfile.domain.badge.entity;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class OwnBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Badge badge;

    public OwnBadge(User user, Badge badge) {
        this.user = user;
        this.badge = badge;
    }
}
