package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dogs")
public class Dog {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Getter
    @Setter
    @JoinColumn(nullable = false, name = "dogfile_user_id")
    @ManyToOne
    private User dogfileUserId;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(nullable = false)
    private Long breed;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date birthday;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Size size;

    @Getter
    @Setter
    @Column(name = "dog_image")
    private String dogImage;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private Short deleted;

    @Getter
    @Setter
    @Column(name = "withdraw_request_at")
    private LocalDateTime withdrawRequestAt;

    public Dog(
            @RequestParam User dogfileUserId,
            @RequestParam String name,
            @RequestParam Long breed,
            @RequestParam Date birthday,
            @RequestParam Size size,
            String dogImage
            ) {
        this.dogfileUserId = dogfileUserId;
        this.name = name;
        this.breed = breed;
        this.birthday = birthday;
        this.size = size;
        this.dogImage = dogImage;
        this.deleted = 0;
    }

    public enum Size {Small, Medium, Large}
}


