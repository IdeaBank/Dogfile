package com.honeyosori.dogfile.domain.user.dto;

import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.DogBreed;
import com.honeyosori.dogfile.domain.user.entity.User;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.time.LocalDateTime;

public record DogInfoDto(String id,
                         User dogFileUser,
                         String name,
                         DogBreed dogBreed,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date birthday,
                         Dog.Size size,
                         String dogImage,
                         Boolean deleted,
                         LocalDateTime withdrawRequestAt) {
    public static DogInfoDto of(Dog dog) {
        return new DogInfoDto(
                dog.getId(),
                dog.getDogfileUser(),
                dog.getName(),
                dog.getDogBreed(),
                dog.getBirthday(),
                dog.getSize(),
                dog.getDogImage(),
                dog.getDeleted(),
                dog.getWithdrawRequestAt()
        );
    }
}
