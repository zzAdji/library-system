package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;

import java.util.Optional;

// Les services d'Authentification

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
    }

    public Optional<User> login(String login, String password) {
        Optional<User> optionalUser = userRepository.findByCardNumber(login);
    
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
        
            if (user.getPassword().equals(password)) {
                this.currentUser = user;
            
                return Optional.of(user); 
            }
        }

        return Optional.empty();
    }


    public void logout() {
        currentUser = null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(this.currentUser); // Retourne une empty si currentUser = null
    }
}
