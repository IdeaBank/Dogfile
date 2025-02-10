package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user", indexes = {
        @Index(name = "idx_email", columnList = "email", unique = true),
        @Index(name = "idx_account_name", columnList = "accountName", unique = true)
})
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @Column
    private String password;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date birthday;

    @Getter
    @Setter
    @Column(nullable = false)
    private String phoneNumber;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Getter
    @Setter
    @Column(nullable = false)
    private String realName;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String accountName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String profileImageUrl;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Column
    private LocalDateTime deletedAt;

    public User(String email, String password, Date birthday, String phoneNumber, GenderType gender, String realName, String accountName) {
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.realName = realName;
        this.accountName = accountName;
        this.profileImageUrl = "default"; // TODO: 유저 등록 시 프로필 이미지 없으면 default 이미지 url을 저장하도록 하는 if-else 추가.
        this.role = Role.USER;
    }

    public User(String email, Date birthday, String phoneNumber, GenderType gender, String realName, String accountName) {
        this.email = email;
        this.password = null;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.realName = realName;
        this.accountName = accountName;
        this.profileImageUrl = "default"; // TODO: 유저 등록 시 프로필 이미지 없으면 default 이미지 url을 저장하도록 하는 if-else 추가.
        this.role = Role.USER;
    }

    public User(String email) {
        this.email = email;
    }

    public enum GenderType {
        MALE, FEMALE, OTHER
    }

    public enum Role {
        GUEST, USER, ADMIN
    }
}
