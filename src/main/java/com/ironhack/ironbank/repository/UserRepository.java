package com.ironhack.ironbank.repository;

import com.ironhack.ironbank.enums.UserType;
import com.ironhack.ironbank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByUserType(UserType userType);
}
