package com.library.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanStatus;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;

// Les services de statistiques

public class StatisticsService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public StatisticsService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public long countAvailableBooks() {
        List<Book> allBooks = bookRepository.findAll();
        long count = 0;

        for (Book book : allBooks) {
            count += book.getAvailableCopies();
        }

        return count;
    }

    public long countBorrowedBooks() {
        List<Book> allBooks = bookRepository.findAll();
        long count = 0;

        for (Book book : allBooks) {
            count += (book.getTotalCopies() - book.getAvailableCopies());
        }

        return count;
    }

    public List<Book> topBorrowedBooks(int limit) {
        List<Loan> allLoans = loanRepository.findAll();
        Map<String, Long> counts = allLoans.stream()
            .collect(Collectors.groupingBy(Loan::getBookIsbn, Collectors.counting()));

        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> bookRepository.findByIsbn(entry.getKey()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public List<Loan> overdueLoans() {
        LocalDate today = LocalDate.now();
        return loanRepository.findAll().stream()
            .filter(loan -> loan.getStatus() != LoanStatus.RETURNED)
            .filter(loan -> loan.getDueDate() != null && loan.getDueDate().isBefore(today))
            .collect(Collectors.toList());
    }
}
