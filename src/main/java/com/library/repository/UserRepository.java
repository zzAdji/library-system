package com.library.repository;

import com.library.model.User;
import com.library.model.UserStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByCardNumber(String cardNumber);
    Optional<User> findByEmail(String email);
    void save(User user);
    void update(User user ,String id, String cardNumber, String password, String firstName, String lastName, String email, String phone, Role role, UserStatus status, LocalDate registrationDate)
    void deleteById(String id);
}
