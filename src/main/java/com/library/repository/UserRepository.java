package com.library.repository;

import com.library.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByCardNumber(String cardNumber);
    Optional<User> findByEmail(String email);
    void save(User user);
    void update(User user);
    void deleteById(String id);
}
