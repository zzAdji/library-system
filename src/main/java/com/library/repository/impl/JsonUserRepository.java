package com.library.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.config.JsonConfig;
import com.library.model.User;
import com.library.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUserRepository implements UserRepository {

    private static final String FILE_PATH = "src/main/resources/data/users.json";
    private final ObjectMapper mapper = JsonConfig.getObjectMapper();

    private List<User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeData(List<User> users) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try {
            mapper.writeValue(file, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findAll() {
        return loadData();
    }

    @Override
    public Optional<User> findById(String id) {
        return loadData().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByCardNumber(String cardNumber) {
        return loadData().stream().filter(u -> u.getCardNumber().equals(cardNumber)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return loadData().stream().filter(u -> u.getEmail() != null && u.getEmail().equals(email)).findFirst();
    }

    @Override
    public void save(User user) {
        List<User> users = loadData();
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        writeData(users);
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void deleteById(String id) {
        List<User> users = loadData();
        users.removeIf(u -> u.getId().equals(id));
        writeData(users);
    }
}
