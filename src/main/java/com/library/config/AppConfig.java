package com.library.config;

import com.library.repository.BookRepository;
import com.library.repository.impl.JsonBookRepository;
import com.library.service.BookService;

public final class AppConfig {

    private AppConfig() {}

    public static BookRepository bookRepository() {
        return new JsonBookRepository();
    }

    public static BookService bookService() {
        return new BookService(bookRepository());
    }

    // Les autres services seront câblés par leurs développeurs respectifs
}