package com.honeyosori.dogfile.domain.dog.entity;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column
    private String name;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Breed breed;

    @Column(name = "image_url")
    private String imageUrl;

    public Dog(String name) {
        this.name = name;
    }
}