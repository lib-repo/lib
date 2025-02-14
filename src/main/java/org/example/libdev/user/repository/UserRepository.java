package org.example.libdev.user.repository;

import org.example.libdev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    Optional<User> findByUserNameAndEmail(String userName, String email);
    Optional<User> findByUserIdAndEmail(String userId, String email);
}