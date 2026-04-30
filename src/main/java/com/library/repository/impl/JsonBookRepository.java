package com.library.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.config.JsonConfig;
import com.library.model.Book;
import com.library.repository.BookRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonBookRepository implements BookRepository {

    private static final String FILE_PATH = "data/books.json";
    private final ObjectMapper mapper = JsonConfig.getObjectMapper();

    private List<Book> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Book>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void writeData(List<Book> books) {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try {
            mapper.writeValue(file, books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> findAll() {
        return loadData();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return loadData().stream().filter(b -> b.getIsbn().equals(isbn)).findFirst();
    }

    @Override
    public List<Book> findByCategory(String category) {
        return loadData().stream()
                .filter(b -> b.getCategory() != null && b.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) return new ArrayList<>();
        String lowerKw = keyword.toLowerCase();
        return loadData().stream()
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(lowerKw)) ||
                             (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(lowerKw)))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Book book) {
        List<Book> books = loadData();
        books.removeIf(b -> b.getIsbn().equals(book.getIsbn()));
        books.add(book);
        writeData(books);
    }

    @Override
    public void update(Book book) {
        save(book);
    }

    @Override
    public void deleteByIsbn(String isbn) {
        List<Book> books = loadData();
        books.removeIf(b -> b.getIsbn().equals(isbn));
        writeData(books);
    }
}
