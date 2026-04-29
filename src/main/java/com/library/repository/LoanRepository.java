package com.library.repository;

import com.library.model.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    List<Loan> findAll();
    Optional<Loan> findById(String id);
    List<Loan> findByUserId(String userId);
    List<Loan> findOverdueLoans();
    void save(Loan loan);
    void update(Loan loan);
    void deleteById(String id);
}
