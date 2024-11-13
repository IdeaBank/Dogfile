package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
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
    @Column(unique = true, nullable = false, name = "account_name")
    private String accountName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(nullable = false, name = "real_name")
    private String realName;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date birthday;

    @Getter
    @Setter
    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Getter
    @Setter
    @Column(nullable = false, name = "profile_image_url")
    private String profileImageUrl;

    @Getter
    @Setter
    @Column(nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

//    @Getter
//    @Setter
//    @Column
//    private String address; 주소삭제

    @Getter
    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    private Deleted deleted;

    @Timestamp
    @Column(name = "withdraw_request_at")
    private LocalDateTime withdraw_request_at;

    public User(String email, String password, String realName, GenderType gender, Date birthday, String phoneNumber, String address, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.realName = realName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
//        this.address = address;
        this.profileImageUrl = profileImageUrl; // TODO: 유저 등록 시 프로필 이미지 없으면 default 이미지 url을 저장하도록 하는 if-else 추가.
        this.role = Role.USER;
        this.userStatus = UserStatus.PRIVATE;
        this.createdAt = LocalDateTime.now();
        this.deleted = Deleted.FALSE;
    }

    public User(String email) {
        this.email = email;
    }

    public void resetUser() {
        this.email = null;
        this.password = null;
        this.realName = null;
        this.gender = null;
        this.birthday = null;
        this.phoneNumber = null;
//        this.address = null;
        this.profileImageUrl = null;
        this.role = Role.GUEST;
    } // TODO: null이 되면 안됨. 삭제 시 null 로 바꾸지 않고 지울 수 있도록 수정 바람.

    public void registerKakaoUser(CreateKakaoAccountDto createKakaoAccountDto) {
        this.realName = createKakaoAccountDto.realName();
        this.gender = createKakaoAccountDto.gender();
        this.birthday = createKakaoAccountDto.birthday();
        this.phoneNumber = createKakaoAccountDto.phoneNumber();
//        this.address = createKakaoAccountDto.address(); // 주소 삭제.
        this.profileImageUrl = createKakaoAccountDto.profileImageUrl();
        this.userStatus = User.UserStatus.PUBLIC;
        this.role = User.Role.USER;
        this.createdAt = LocalDateTime.now();
        this.deleted = Deleted.FALSE;
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

    public enum Deleted { TRUE, FALSE }
}
