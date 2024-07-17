package com.honeyosori.dogfile.domain.dog.repository;

import com.honeyosori.dogfile.domain.dog.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
}