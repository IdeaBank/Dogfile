package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.badge.entity.OwnBadge;
import com.honeyosori.dogfile.domain.dog.entity.Dog;
import com.honeyosori.dogfile.global.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Role role;

    @CreationTimestamp
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dog> dogs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OwnBadge> ownBadgeList;

    @OneToMany(targetEntity = Block.class, mappedBy = "blockIdentity.blocker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> blockerList;

    @OneToMany(targetEntity = Block.class, mappedBy = "blockIdentity.blockee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> blockeeList;

    @OneToMany(targetEntity = Follow.class, mappedBy = "followIdentity.follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> followerList;

    @OneToMany(targetEntity = Follow.class, mappedBy = "followIdentity.followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> followeeList;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
