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
    @Column(unique = true)
    private Long id;

    @Column
    private String breed;

    @Column
    private Size size;

    public DogBreed(@RequestParam Long id, String breed, Size size) {
        this.id = id;
        this.breed = breed;
        this.size = size;
    }

    public enum Size { Small, Medium, Large }
}
