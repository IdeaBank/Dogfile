package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.Follow;
import com.honeyosori.dogfile.domain.user.identity.FollowIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowIdentity> {
}
