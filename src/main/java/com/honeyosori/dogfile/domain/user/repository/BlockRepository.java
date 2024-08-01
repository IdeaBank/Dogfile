package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.identity.BlockIdentity;
import com.honeyosori.dogfile.domain.user.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockIdentity> {
    Block findBlockByBlockIdentity_BlockerIdAndBlockIdentity_BlockeeId(Long blockerId, Long blockeeId);
    List<Block> getBlocksByBlockIdentityBlockeeId(Long blockeeId);
    List<Block> getBlocksByBlockIdentityBlockerId(Long blockerId);
}
