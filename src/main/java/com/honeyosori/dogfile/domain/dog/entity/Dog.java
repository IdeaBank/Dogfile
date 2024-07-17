package com.honeyosori.dogfile.domain.dog.entity;

import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @Column
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private Breed breed;

    public Dog(String name, Breed breed) {
        this.name = name;
        this.breed = breed;
    }
}