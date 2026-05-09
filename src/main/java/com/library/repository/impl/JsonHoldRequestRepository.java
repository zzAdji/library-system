package com.library.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.config.JsonConfig;
import com.library.model.HoldRequest;
import com.library.repository.HoldRequestRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonHoldRequestRepository implements HoldRequestRepository {

    private static final String FILE_PATH = "src/main/resources/data/holdrequests.json";
    private final ObjectMapper mapper = JsonConfig.getObjectMapper();

    private List<HoldRequest> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<HoldRequest>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeData(List<HoldRequest> requests) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try {
            mapper.writeValue(file, requests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HoldRequest> findAll() {
        return loadData();
    }

    @Override
    public Optional<HoldRequest> findById(String id) {
        return loadData().stream().filter(hr -> hr.getId().equals(id)).findFirst();
    }

    @Override
    public List<HoldRequest> findByBookIsbn(String bookIsbn) {
        return loadData().stream().filter(hr -> hr.getBookIsbn().equals(bookIsbn)).collect(Collectors.toList());
    }

    @Override
    public List<HoldRequest> findByUserId(String userId) {
        return loadData().stream().filter(hr -> hr.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public void save(HoldRequest holdRequest) {
        List<HoldRequest> requests = loadData();
        requests.removeIf(hr -> hr.getId().equals(holdRequest.getId()));
        requests.add(holdRequest);
        writeData(requests);
    }

    @Override
    public void update(HoldRequest holdRequest) {
        save(holdRequest);
    }

    @Override
    public void deleteById(String id) {
        List<HoldRequest> requests = loadData();
        requests.removeIf(hr -> hr.getId().equals(id));
        writeData(requests);
    }
}
