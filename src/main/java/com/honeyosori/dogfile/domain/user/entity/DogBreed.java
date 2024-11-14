package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Entity
@NoArgsConstructor
public class DogBreed {
    @Id
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
