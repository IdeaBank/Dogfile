package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, String> {
    List<Dog> findAllByDogfileUserEmail(String dogfileUserEmail);
    Dog findByNameAndDogfileUserEmail(String name, String dogfileUserEmail);
    @Query("SELECT d FROM Dog d WHERE d.deleted = true")
    List<Dog> findByDeleted();
    List<Dog> findAllByDogfileUserId(String dogfileUserId);
    Dog findByDogfileUserIdAndName(String dogfileUserId, String name);
}