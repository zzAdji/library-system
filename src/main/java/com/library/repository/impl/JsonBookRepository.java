package com.library.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.library.model.Book;
import com.library.repository.BookRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonBookRepository implements BookRepository {

    private static final String DATA_FILE = "src/main/resources/data/books.json";

    private final ObjectMapper mapper;
    private final File dataFile;

    public JsonBookRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.dataFile = new File(DATA_FILE);
    }

    @Override
    public void save(Book book) {
        List<Book> livres = loadAll();
        boolean exists = livres.stream()
                .anyMatch(b -> b.getIsbn().equalsIgnoreCase(book.getIsbn()));
        if (exists)
            throw new IllegalArgumentException("ISBN déjà existant : " + book.getIsbn());
        livres.add(book);
        writeAll(livres);
    }

    @Override
    public void update(Book book) {
        List<Book> livres = loadAll();
        boolean found = false;
        for (int i = 0; i < livres.size(); i++) {
            if (livres.get(i).getIsbn().equalsIgnoreCase(book.getIsbn())) {
                livres.set(i, book);
                found = true;
                break;
            }
        }
        if (!found)
            throw new IllegalArgumentException("Livre introuvable : " + book.getIsbn());
        writeAll(livres);
    }

    @Override
    public void deleteByIsbn(String isbn) {
        List<Book> livres = loadAll();
        boolean removed = livres.removeIf(b -> b.getIsbn().equalsIgnoreCase(isbn));
        if (!removed)
            throw new IllegalArgumentException("Livre introuvable : " + isbn);
        writeAll(livres);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return loadAll().stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    @Override
    public List<Book> findByCategory(String category) {
        if (category == null || category.isBlank()) return loadAll();
        return loadAll().stream()
                .filter(b -> category.equalsIgnoreCase(b.getCategory()))
                .toList();
    }

    @Override
    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return loadAll();
        String kw = keyword.toLowerCase();
        return loadAll().stream()
                .filter(b ->
                    contains(b.getTitle(),    kw) ||
                    contains(b.getAuthor(),   kw) ||
                    contains(b.getCategory(), kw) ||
                    contains(b.getIsbn(),     kw))
                .toList();
    }

    @Override
    public List<Book> findAll() {
        return loadAll();
    }

    private List<Book> loadAll() {
        if (!dataFile.exists()) return new ArrayList<>();
        try {
            List<Book> livres = mapper.readValue(dataFile, new TypeReference<List<Book>>() {});
            return livres != null ? livres : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture JSON : " + dataFile.getPath(), e);
        }
    }

    private void writeAll(List<Book> livres) {
        try {
            dataFile.getParentFile().mkdirs();
            mapper.writeValue(dataFile, livres);
        } catch (IOException e) {
            throw new RuntimeException("Erreur écriture JSON : " + dataFile.getPath(), e);
        }
    }

    private boolean contains(String field, String keyword) {
        return field != null && field.toLowerCase().contains(keyword);
    }
}
