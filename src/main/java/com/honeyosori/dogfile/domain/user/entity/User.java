package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String accountName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(nullable = false)
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
    @Column(nullable = false)
    private String phoneNumber;

    @Getter
    @Setter
    @Column(nullable = false)
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

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean deleted;


    @Timestamp
    @Getter
    @Setter
    @Column
    private LocalDateTime withdrawRequestAt = null;

    public User(String email, String password, String realName, GenderType gender, Date birthday, String phoneNumber, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.realName = realName;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl; // TODO: 유저 등록 시 프로필 이미지 없으면 default 이미지 url을 저장하도록 하는 if-else 추가.
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    public User(String email) {
        this.email = email;
    }

    public void registerKakaoUser(CreateKakaoAccountDto createKakaoAccountDto) {
        this.realName = createKakaoAccountDto.realName();
        this.gender = createKakaoAccountDto.gender();
        this.birthday = createKakaoAccountDto.birthday();
        this.phoneNumber = createKakaoAccountDto.phoneNumber();
//        this.address = createKakaoAccountDto.address(); // 주소 삭제.
        this.profileImageUrl = createKakaoAccountDto.profileImageUrl();
        this.role = User.Role.USER;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    public enum GenderType {
        MALE, FEMALE, OTHER
    }

    public enum Role {
        GUEST, USER, ADMIN
    }
}
