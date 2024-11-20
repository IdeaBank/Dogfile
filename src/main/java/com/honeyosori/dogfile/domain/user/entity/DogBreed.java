package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class DogBreed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String breed;

    @Column
    @Enumerated(EnumType.STRING)
    private Size size;

    public DogBreed(Long id, String breed, Size size) {
        this.id = id;
        this.breed = breed;
        this.size = size;
    }

    public enum Size { Small, Medium, Large }
}
