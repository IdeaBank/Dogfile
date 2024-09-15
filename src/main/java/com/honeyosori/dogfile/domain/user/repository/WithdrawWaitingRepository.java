package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.WithdrawWaiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawWaitingRepository extends JpaRepository<WithdrawWaiting, String> {
    Boolean existsByUserId(String id);
}
