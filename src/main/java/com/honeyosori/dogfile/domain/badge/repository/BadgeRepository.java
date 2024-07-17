package com.honeyosori.dogfile.domain.badge.repository;

import com.honeyosori.dogfile.domain.dog.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Breed, Long> {
}
