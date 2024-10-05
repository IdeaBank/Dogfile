package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Getter
    @Setter
    @Column
    private String email;

    @Getter
    @Setter
    @Column
    private String password;

    @Getter
    @Column
    private String realName;

    @Getter
    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    private GenderType gender;

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
    private String address;

    @Getter
    @Setter
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Getter
    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User(String email, String password, String realName, GenderType gender, Date birthday, String phoneNumber, String address, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.realName = realName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
        this.role = Role.USER;
        this.userStatus = UserStatus.PRIVATE;
    }

    public void resetUser() {
        this.email = null;
        this.password = null;
        this.realName = null;
        this.gender = null;
        this.birthday = null;
        this.phoneNumber = null;
        this.address = null;
        this.profileImageUrl = null;
        this.role = null;
    }

    public enum GenderType {
        MALE, FEMALE
    }

    public enum Role {
        GUEST, USER, ADMIN
    }

    public enum UserStatus {
        PUBLIC, PRIVATE, WITHDRAW_REQUESTED, WITHDRAWN
    }
}
