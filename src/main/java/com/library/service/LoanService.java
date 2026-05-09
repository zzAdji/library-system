package com.library.service;

import com.library.model.Loan;
import com.library.model.LoanStatus;
import com.library.model.User;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Les services d'emprunts

public class LoanService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public LoanService(UserRepository userRepository, BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public void borrowBook(String userId, String isbn) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }

        if (optionalBook.isEmpty()) {
            throw new IllegalArgumentException("Livre introuvable");
        }

        Book book = optionalBook.get();
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("Livre non disponible");
        }

        Loan loan = new Loan(UUID.randomUUID().toString(), userId, isbn, LocalDate.now(), LocalDate.now().plusDays(14), null, LoanStatus.ONGOING, 0);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.update(book);
        loanRepository.save(loan);
    }

    public void returnBook(String loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isEmpty()) {
            throw new IllegalArgumentException("Emprunt inexistant");
        }

        Loan loan = optionalLoan.get();
        if (loan.getStatus().equals(LoanStatus.RETURNED)) {
            throw new IllegalArgumentException("Livre déjà remis");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.update(loan);

        Optional<Book> optionalBook = bookRepository.findByIsbn(loan.getBookIsbn());
        if (optionalBook.isEmpty()) {
            throw new IllegalArgumentException("Livre introuvable");
        }

        Book book = optionalBook.get();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.update(book);
    }

    public void extendLoan(String loanId, int extraDays) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            LocalDate newDueDate = loan.getDueDate();
            loan.setDueDate(newDueDate.plusDays(extraDays));
            loan.setRenewalsCount(loan.getRenewalsCount() + 1);
            loanRepository.update(loan);
        }
    }

    public List<Loan> getLoansByUser(String userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getActiveLoans() {
        List<Loan> activeLoans = loanRepository.findAll();

        activeLoans.removeIf(loan -> !loan.getStatus().equals(LoanStatus.ONGOING));

        return activeLoans;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
