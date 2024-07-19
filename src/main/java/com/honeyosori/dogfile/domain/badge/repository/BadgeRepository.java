package com.honeyosori.dogfile.domain.badge.repository;

import com.honeyosori.dogfile.domain.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    public boolean existsBadgeByTitle(String title);
}
