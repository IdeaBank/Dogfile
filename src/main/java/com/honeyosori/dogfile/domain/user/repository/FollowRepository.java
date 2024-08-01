package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.Follow;
import com.honeyosori.dogfile.domain.user.identity.FollowIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowIdentity> {
    Follow findByFollowIdentity_FollowerIdAndFollowIdentity_FolloweeId(Long followerId, Long followeeId);
    List<Follow> getFollowsByFollowIdentityFollowerId(Long followerId);
    List<Follow> getFollowsByFollowIdentityFolloweeId(Long followerId);
}
