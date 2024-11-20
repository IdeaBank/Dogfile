package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.Dog;
import com.honeyosori.dogfile.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, String> {
    Dog getDogByDogfileUser(User user);
    Optional<Dog> findDogByDogfileUser(User user);
    List<Dog> findAllByDogfileUserContaining(User user);
}
