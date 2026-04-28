package com.library.service;

import java.util.List;

// Les services d'emprunts

public class LoanService {

    public void borrowBook(String userId, String isbn) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void returnBook(String loanId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void extendLoan(String loanId, int extraDays) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Object> getLoansByUser(String userId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Object> getActiveLoans() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
