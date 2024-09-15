package com.honeyosori.dogfile.domain.badge.repository;

import com.honeyosori.dogfile.domain.badge.entity.OwnBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnBadgeRepository extends JpaRepository<OwnBadge, Long> {
    boolean existsByUserIdAndBadgeId(String userId, Long badgeId);
}
