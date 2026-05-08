package com.library.ui.console;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.util.ConsoleUtils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BookConsole {

    private final BookService bookService;
    private final Scanner scanner;

    public BookConsole(BookService bookService, Scanner scanner) {
        this.bookService = bookService;
        this.scanner     = scanner;
    }

    public void showBookMenu() {
    boolean retour = false;
    while (!retour) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Gestion des livres");
        System.out.println();
        System.out.println(ConsoleUtils.BLUE + "  ╔══════════════════════════════════════╗" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[1]" + ConsoleUtils.RESET + " Ajouter un livre                " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[2]" + ConsoleUtils.RESET + " Modifier un livre               " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[3]" + ConsoleUtils.RESET + " Supprimer un livre              " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[4]" + ConsoleUtils.RESET + " Rechercher un livre             " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[5]" + ConsoleUtils.RESET + " Afficher tous les livres        " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[6]" + ConsoleUtils.RESET + " Detail d'un livre (ISBN)        " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║                                      ║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ║  " + ConsoleUtils.RESET + ConsoleUtils.RED   + ConsoleUtils.BOLD + "[0]" + ConsoleUtils.RESET + " Retour                          " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.BLUE + "  ╚══════════════════════════════════════╝" + ConsoleUtils.RESET);
        System.out.println();

        String choix = ConsoleUtils.readLine(ConsoleUtils.BOLD + "    Votre choix : " + ConsoleUtils.RESET);

        switch (choix) {
            case "1" -> handleAddBook();
            case "2" -> handleUpdateBook();
            case "3" -> handleDeleteBook();
            case "4" -> handleSearchBooks();
            case "5" -> listBooks();
            case "6" -> handleFindByIsbn();
            case "0" -> retour = true;
            default  -> {
                ConsoleUtils.printError("Option invalide.");
                ConsoleUtils.pause();
            }
        }
    }
}

    // ── Ajouter ───────────────────────────────────────────────────────────────

    private void handleAddBook() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Ajouter un livre");
        try {
            String isbn      = ConsoleUtils.readNonEmptyString("  ISBN          : ");
            String title     = ConsoleUtils.readNonEmptyString("  Titre         : ");
            String author    = ConsoleUtils.readNonEmptyString("  Auteur        : ");
            String publisher = ConsoleUtils.readLine(          "  Éditeur       : ");
            int    year      = ConsoleUtils.readInt(           "  Année         : ");
            String category  = ConsoleUtils.readLine(          "  Catégorie     : ");
            int    total     = ConsoleUtils.readInt(           "  Nb exemplaires: ");

            Book book = new Book(isbn, title, author, publisher, year, category, total, total);
            bookService.addBook(book);
            System.out.println(ConsoleUtils.GREEN + "\n  ✔ Livre ajouté : " + title + ConsoleUtils.RESET);
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Modifier ──────────────────────────────────────────────────────────────

    private void handleUpdateBook() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Modifier un livre");
        try {
            String isbn = ConsoleUtils.readNonEmptyString("  ISBN du livre à modifier : ");
            Optional<Book> existant = bookService.findByIsbn(isbn);
            if (existant.isEmpty()) {
                ConsoleUtils.printError("Aucun livre trouvé avec l'ISBN : " + isbn);
                ConsoleUtils.pause();
                return;
            }
            Book a = existant.get();
            System.out.println("  Livre : " + a.getTitle() + " — " + a.getAuthor());
            System.out.println("  (Entrée = conserver la valeur actuelle)\n");

            String title     = lireOuGarder("  Titre     [" + a.getTitle()     + "] : ", a.getTitle());
            String author    = lireOuGarder("  Auteur    [" + a.getAuthor()    + "] : ", a.getAuthor());
            String publisher = lireOuGarder("  Éditeur   [" + nvl(a.getPublisher()) + "] : ", nvl(a.getPublisher()));
            int    year      = lireEntierOuGarder("  Année     [" + a.getPublishYear() + "] : ", a.getPublishYear());
            String category  = lireOuGarder("  Catégorie [" + nvl(a.getCategory())  + "] : ", nvl(a.getCategory()));
            int    total     = lireEntierOuGarder("  Exemplaires [" + a.getTotalCopies() + "] : ", a.getTotalCopies());

            Book modifie = new Book(isbn, title, author, publisher, year, category, total, a.getAvailableCopies());
            bookService.updateBook(modifie);
            System.out.println(ConsoleUtils.GREEN + "\n  ✔ Livre modifié avec succès." + ConsoleUtils.RESET);
        } catch (IllegalArgumentException | IllegalStateException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Supprimer ─────────────────────────────────────────────────────────────

    private void handleDeleteBook() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Supprimer un livre");
        try {
            String isbn = ConsoleUtils.readNonEmptyString("  ISBN du livre à supprimer : ");
            Optional<Book> livre = bookService.findByIsbn(isbn);
            if (livre.isEmpty()) {
                ConsoleUtils.printError("Aucun livre trouvé avec l'ISBN : " + isbn);
                ConsoleUtils.pause();
                return;
            }
            System.out.println("  Livre : " + livre.get().getTitle());
            String confirm = ConsoleUtils.readLine("  Confirmer la suppression ? (o/N) : ");
            if (confirm.equalsIgnoreCase("o")) {
                bookService.deleteBook(isbn);
                System.out.println(ConsoleUtils.GREEN + "\n  ✔ Livre supprimé." + ConsoleUtils.RESET);
            } else {
                System.out.println("  ↩ Suppression annulée.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Rechercher ────────────────────────────────────────────────────────────

    private void handleSearchBooks() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Rechercher un livre");
        String keyword = ConsoleUtils.readLine("  Mot-clé (titre, auteur, catégorie, ISBN) : ");
        List<Book> resultats = bookService.search(keyword);
        if (resultats.isEmpty()) {
            System.out.println("  Aucun résultat pour : \"" + keyword + "\"");
        } else {
            System.out.println("\n  " + resultats.size() + " résultat(s) :");
            afficherListe(resultats);
        }
        ConsoleUtils.pause();
    }

    // ── Lister tous ───────────────────────────────────────────────────────────

    private void listBooks() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Catalogue complet");
        List<Book> livres = bookService.getAllBooks();
        if (livres.isEmpty()) {
            System.out.println("  Aucun livre enregistré.");
        } else {
            System.out.println("\n  " + livres.size() + " livre(s) :");
            afficherListe(livres);
        }
        ConsoleUtils.pause();
    }

    // ── Détail par ISBN ───────────────────────────────────────────────────────

    private void handleFindByIsbn() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Recherche par ISBN");
        String isbn = ConsoleUtils.readNonEmptyString("  ISBN : ");
        bookService.findByIsbn(isbn).ifPresentOrElse(
            this::afficherDetail,
            () -> ConsoleUtils.printError("Aucun livre trouvé avec l'ISBN : " + isbn)
        );
        ConsoleUtils.pause();
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    private void afficherListe(List<Book> livres) {
        System.out.println("\n  " + "─".repeat(75));
        System.out.printf(ConsoleUtils.BOLD + "  %-14s %-28s %-16s %-5s %s%n" + ConsoleUtils.RESET,
            "ISBN", "Titre", "Auteur", "Année", "Dispo");
        System.out.println("  " + "─".repeat(75));
        for (Book b : livres) {
            System.out.printf("  %-14s %-28s %-16s %-5d %d/%d%n",
                b.getIsbn(),
                tronquer(b.getTitle(), 28),
                tronquer(b.getAuthor(), 16),
                b.getPublishYear(),
                b.getAvailableCopies(),
                b.getTotalCopies());
        }
        System.out.println("  " + "─".repeat(75));
    }

    private void afficherDetail(Book b) {
        System.out.println();
        System.out.println("  ┌─ Détail du livre ──────────────────────────────");
        System.out.println("  │ ISBN        : " + b.getIsbn());
        System.out.println("  │ Titre       : " + b.getTitle());
        System.out.println("  │ Auteur      : " + b.getAuthor());
        System.out.println("  │ Éditeur     : " + nvl(b.getPublisher()));
        System.out.println("  │ Année       : " + b.getPublishYear());
        System.out.println("  │ Catégorie   : " + nvl(b.getCategory()));
        System.out.println("  │ Disponible  : " + b.getAvailableCopies() + "/" + b.getTotalCopies()
            + (b.getAvailableCopies() > 0
                ? "  " + ConsoleUtils.GREEN + "✔" + ConsoleUtils.RESET
                : "  " + ConsoleUtils.RED   + "✘" + ConsoleUtils.RESET));
        System.out.println("  └────────────────────────────────────────────────");
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private void printOption(String key, String label) {
    // Compter les caractères accentués qui faussent le padding
    int accents = 0;
    for (char c : label.toCharArray()) {
        if (c > 127) accents++;
    }
    int visibleLen = 2 + key.length() + 2 + label.length(); // "[X] label"
    int padding    = 40 - visibleLen + accents;
    String spaces  = " ".repeat(Math.max(0, padding));
    System.out.println(
        ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET
        + "  " + ConsoleUtils.GREEN + ConsoleUtils.BOLD + "[" + key + "]" + ConsoleUtils.RESET
        + " " + label
        + spaces
        + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET
    );
}

    private String lireOuGarder(String prompt, String actuel) {
        System.out.print(ConsoleUtils.CYAN + prompt + ConsoleUtils.RESET);
        String s = scanner.nextLine().trim();
        return s.isBlank() ? actuel : s;
    }

    private int lireEntierOuGarder(String prompt, int actuel) {
        System.out.print(ConsoleUtils.CYAN + prompt + ConsoleUtils.RESET);
        String s = scanner.nextLine().trim();
        if (s.isBlank()) return actuel;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return actuel; }
    }

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }

    private String nvl(String s) {
        return s != null ? s : "-";
    }
}