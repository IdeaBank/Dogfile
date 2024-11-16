package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User getUserByEmail(String email);
    Optional<User> findUserByEmail(String email);
    List<User> findAllByEmailContaining(String email);
    Optional<User> findByAccountName(String accountName);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<List<User>> findByAccountNameStartingWith(String partialAccountName);
}
