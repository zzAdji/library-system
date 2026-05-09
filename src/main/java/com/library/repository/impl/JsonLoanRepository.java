package com.library.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.config.JsonConfig;
import com.library.model.Loan;
import com.library.model.LoanStatus;
import com.library.repository.LoanRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonLoanRepository implements LoanRepository {

    private static final String FILE_PATH = "src/main/resources/data/loans.json";
    private final ObjectMapper mapper = JsonConfig.getObjectMapper();

    private List<Loan> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            List<Loan> loans = mapper.readValue(file, new TypeReference<List<Loan>>() {});
            boolean hasChanges = false;
            for (Loan loan : loans) {
                if (loan.getId() == null || loan.getId().isBlank()) {
                    loan.setId(UUID.randomUUID().toString());
                    hasChanges = true;
                }
            }
            if (hasChanges) {
                writeData(loans);
            }
            return loans;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeData(List<Loan> loans) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try {
            mapper.writeValue(file, loans);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Loan> findAll() {
        return loadData();
    }

    @Override
    public Optional<Loan> findById(String id) {
        return loadData().stream().filter(l -> Objects.equals(l.getId(), id)).findFirst();
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return loadData().stream().filter(l -> Objects.equals(l.getUserId(), userId)).collect(Collectors.toList());
    }

    @Override
    public List<Loan> findOverdueLoans() {
        return loadData().stream()
                .filter(l -> l.getStatus() == LoanStatus.OVERDUE)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Loan loan) {
        List<Loan> loans = loadData();
        if (loan.getId() == null || loan.getId().isBlank()) {
            loan.setId(UUID.randomUUID().toString());
        }
        loans.removeIf(l -> Objects.equals(l.getId(), loan.getId()));
        loans.add(loan);
        writeData(loans);
    }

    @Override
    public void update(Loan loan) {
        save(loan);
    }

    @Override
    public void deleteById(String id) {
        List<Loan> loans = loadData();
        loans.removeIf(l -> Objects.equals(l.getId(), id));
        writeData(loans);
    }
}