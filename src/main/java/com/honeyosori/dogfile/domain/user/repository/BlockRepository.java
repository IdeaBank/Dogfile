package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.identity.BlockIdentity;
import com.honeyosori.dogfile.domain.user.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockIdentity> {
}
