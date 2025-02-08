package com.honeyosori.dogfile.domain.user.repository;

import com.honeyosori.dogfile.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByIdAndDeletedAtIsNull(String id);
    Optional<User> getUserByEmailAndDeletedAtIsNull(String email);
    Optional<User> findUserByEmailAndDeletedAtIsNull(String email);
    List<User> findAllByEmailContainingAndDeletedAtIsNull(String email);
    Optional<User> findByAccountNameAndDeletedAtIsNull(String accountName);
    Optional<User> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
    List<User> findByAccountNameContainingAndDeletedAtIsNull(String partialAccountName);
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL")
    List<User> findByDeleted();

    Optional<User> findUserByEmail(String email);

    Optional<User> getUserByEmailAndDeletedAtIsNotNull(String email);
}
