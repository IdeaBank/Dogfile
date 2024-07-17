package com.honeyosori.dogfile.domain.dog.repository;

import com.honeyosori.dogfile.domain.dog.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
}
