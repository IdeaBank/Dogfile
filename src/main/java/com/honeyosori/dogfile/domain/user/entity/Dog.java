package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dogs")
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "users_id")
    private User dogfileUser;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private DogBreed dogBreed;

    @Setter
    @Column(nullable = false)
    private Date birthday;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Size size;

    @Setter
    @Column
    private String dogImage = null;

    @Setter

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean deleted;

    @Setter
    @Column
    private LocalDateTime withdrawRequestAt;

    public Dog(
            User dogfileUser,
            String name,
            DogBreed dogBreed,
            Date birthday,
            Size size,
            String dogImage
            ) {
        this.dogfileUser = dogfileUser;
        this.name = name;
        this.dogBreed = dogBreed;
        this.birthday = birthday;
        this.size = size;
        this.dogImage = dogImage;
        this.deleted = false;
    }

    public enum Size {Small, Medium, Large}

}


