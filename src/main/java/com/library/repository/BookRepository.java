package com.library.repository;

import com.library.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> findAll();
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByCategory(String category);
    List<Book> search(String keyword);
    void save(Book book);
    void update(Book book);
    void deleteByIsbn(String isbn);
}
