package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.badge.entity.OwnBadge;
import com.honeyosori.dogfile.domain.dog.entity.Dog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Getter
    @Column
    private String username;

    @Getter
    @Column
    private String realName;

    @Getter
    @Setter
    @Column
    private String password;

    @Getter
    @Setter
    @Column
    private GenderType gender;

    @Getter
    @Setter
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Getter
    @Setter
    @Column
    private Date birthday;

    @Getter
    @Setter
    @Column(name = "phone_number")
    private String phoneNumber;

    @Getter
    @Setter
    @Column
    private String email;

    @Getter
    @Setter
    @Column
    private Role role;

    @Getter
    @Setter
    @Column(name = "user_status")
    private UserStatus userStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "owner")
    private List<Dog> dogs;

    @OneToMany(mappedBy = "user")
    private List<OwnBadge> ownBadgeList;

    @OneToMany(targetEntity = Block.class, mappedBy = "blockIdentity.blocker")
    private List<User> blockerList;

    @OneToMany(targetEntity = Block.class, mappedBy = "blockIdentity.blockee")
    private List<User> blockeeList;

    @OneToMany(targetEntity = Follow.class, mappedBy = "followIdentity.follower")
    private List<User> followerList;

    @OneToMany(targetEntity = Follow.class, mappedBy = "followIdentity.followee")
    private List<User> followeeList;

    public User(String username, String realName, String password, String profileImageUrl, Date birthday, String phoneNumber, String address, String email, GenderType gender) {
        this.username = username;
        this.realName = realName;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
    }

    public enum Role {
        GUEST, USER, ADMIN
    }

    public enum UserStatus {
        PUBLIC, PRIVATE, WITHDRAW_REQUESTED, WITHDRAW
    }

    public enum GenderType {
        MALE, FEMALE
    }
}
