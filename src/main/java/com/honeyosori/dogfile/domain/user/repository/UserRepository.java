package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(Long id);
    Optional<User> findUserByUsername(String username);
}
