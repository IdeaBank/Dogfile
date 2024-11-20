package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.dto.UserDogDto;
import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DogRepository extends JpaRepository<Dog, String> {
    List<Dog> findAllByDogfileUserEmail(String dogfileUserEmail);
}
