package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

// Les services de gestion des livres

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(book.getIsbn());

        if (optionalBook.isPresent()) {
            throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà !");
        }

        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        bookRepository.update(book);
    }

    public void deleteBook(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    public List<Book> search(String keyword) {
        return bookRepository.search(keyword);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
