package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        validerBook(book);
        if (book.getTotalCopies() < 1)
            throw new IllegalArgumentException("Le nombre d'exemplaires doit être au moins 1.");
        book.setAvailableCopies(book.getTotalCopies());
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        validerBook(book);
        Book existant = bookRepository.findByIsbn(book.getIsbn())
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable : " + book.getIsbn()));
        int enEmprunt = existant.getTotalCopies() - existant.getAvailableCopies();
        if (book.getTotalCopies() < enEmprunt)
            throw new IllegalArgumentException(
                "Impossible : " + enEmprunt + " exemplaire(s) actuellement empruntés.");
        book.setAvailableCopies(book.getTotalCopies() - enEmprunt);
        bookRepository.update(book);
    }

    public void deleteBook(String isbn) {
        if (isbn == null || isbn.isBlank())
            throw new IllegalArgumentException("L'ISBN ne peut pas être vide.");
        Book livre = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable : " + isbn));
        int enEmprunt = livre.getTotalCopies() - livre.getAvailableCopies();
        if (enEmprunt > 0)
            throw new IllegalStateException(
                "Impossible de supprimer : " + enEmprunt + " exemplaire(s) empruntés.");
        bookRepository.deleteByIsbn(isbn);
    }

    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return Optional.empty();
        return bookRepository.findByIsbn(isbn.trim());
    }

    public List<Book> search(String keyword) {
        return bookRepository.search(keyword == null ? "" : keyword.trim());
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public void marquerCommeEmprunte(String isbn) {
        Book livre = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable : " + isbn));
        if (livre.getAvailableCopies() <= 0)
            throw new IllegalStateException("Aucun exemplaire disponible : " + isbn);
        livre.setAvailableCopies(livre.getAvailableCopies() - 1);
        bookRepository.update(livre);
    }

    public void marquerCommeRetourne(String isbn) {
        Book livre = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Livre introuvable : " + isbn));
        if (livre.getAvailableCopies() >= livre.getTotalCopies())
            throw new IllegalStateException("Tous les exemplaires déjà retournés : " + isbn);
        livre.setAvailableCopies(livre.getAvailableCopies() + 1);
        bookRepository.update(livre);
    }

    private void validerBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Le livre ne peut pas être null.");
        if (book.getIsbn() == null || book.getIsbn().isBlank())
            throw new IllegalArgumentException("L'ISBN est obligatoire.");
        if (book.getTitle() == null || book.getTitle().isBlank())
            throw new IllegalArgumentException("Le titre est obligatoire.");
        if (book.getAuthor() == null || book.getAuthor().isBlank())
            throw new IllegalArgumentException("L'auteur est obligatoire.");
        if (book.getPublishYear() < 1000 || book.getPublishYear() > 2100)
            throw new IllegalArgumentException("L'année doit être entre 1000 et 2100.");
    }


    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
}