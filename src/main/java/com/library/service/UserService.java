package com.library.service;

import com.library.model.User;
import com.library.model.UserStatus;
import com.library.repository.UserRepository;

import java.util.List;
import java.util.Optional;

// Les services de gestion des utilisateurs

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        if (optionalUser.isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.update(user);
    }

    public void deactivateUser(String userId) {
        Optional<User> optionalUser = findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(UserStatus.INACTIVE);
            updateUser(user);
        }
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
