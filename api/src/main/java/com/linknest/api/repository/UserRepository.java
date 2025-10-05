package com.linknest.api.repository;

import com.linknest.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/*
By extending JpaRepository<User, Long>, we instantly get methods
like save(), findById(), findAll(), and delete() for our User entity
without writing any code.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // This method allows us to find a user by their username
    Optional<User> findByUsername(String username);
}