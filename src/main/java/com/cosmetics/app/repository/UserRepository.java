package com.cosmetics.app.repository;

import com.cosmetics.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used by login
    Optional<User> findByEmailAndPassword(String email, String password);

    // Used by profile load and update
    Optional<User> findByEmail(String email);

}