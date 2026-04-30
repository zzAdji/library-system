package com.library.ui.console;

import com.library.model.Book;
import com.library.service.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BookConsole {

    private final BookService bookService;
    private final Scanner scanner;

    public BookConsole(BookService bookService, Scanner scanner) {
        this.bookService = bookService;
        this.scanner = scanner;
    }

    public void showBookMenu() {
        boolean retour = false;
        while (!retour) {
            System.out.println();
            System.out.println("╔══════════════════════════════════╗");
            System.out.println("║       GESTION DES LIVRES         ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Ajouter un livre             ║");
            System.out.println("║  2. Modifier un livre            ║");
            System.out.println("║  3. Supprimer un livre           ║");
            System.out.println("║  4. Rechercher un livre          ║");
            System.out.println("║  5. Afficher tous les livres     ║");
            System.out.println("║  6. Détail d'un livre (ISBN)     ║");
            System.out.println("║  0. Retour au menu principal     ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" -> handleAddBook();
                case "2" -> handleUpdateBook();
                case "3" -> handleDeleteBook();
                case "4" -> handleSearchBooks();
                case "5" -> listBooks();
                case "6" -> handleFindByIsbn();
                case "0" -> retour = true;
                default  -> System.out.println("  ❌ Choix invalide.");
            }
        }
    }

    public void handleAddBook() {
        System.out.println("\n── Ajouter un livre ──");
        try {
            String isbn      = lireChamp("ISBN");
            String title     = lireChamp("Titre");
            String author    = lireChamp("Auteur");
            String publisher = lireChampOptional("Éditeur (optionnel)");
            int    year      = lireEntier("Année de publication");
            String category  = lireChampOptional("Catégorie (optionnel)");
            int    total     = lireEntierPositif("Nombre total d'exemplaires");

            Book book = new Book(isbn, title, author, publisher, year, category, total, total);
            bookService.addBook(book);
            System.out.println("  ✅ Livre ajouté : " + title);
        } catch (IllegalArgumentException e) {
            System.out.println("  ❌ Erreur : " + e.getMessage());
        }
    }

    public void handleUpdateBook() {
        System.out.println("\n── Modifier un livre ──");
        try {
            String isbn = lireChamp("ISBN du livre à modifier");
            Optional<Book> existant = bookService.findByIsbn(isbn);
            if (existant.isEmpty()) {
                System.out.println("  ❌ Aucun livre trouvé avec l'ISBN : " + isbn);
                return;
            }
            Book actuel = existant.get();
            System.out.println("  Livre actuel : " + actuel.getTitle() + " — " + actuel.getAuthor());
            System.out.println("  (Entrée = conserver la valeur actuelle)");

            String title     = lireChampOuGarder("Titre",    actuel.getTitle());
            String author    = lireChampOuGarder("Auteur",   actuel.getAuthor());
            String publisher = lireChampOuGarder("Éditeur",  actuel.getPublisher() != null ? actuel.getPublisher() : "");
            int    year      = lireEntierOuGarder("Année",   actuel.getPublishYear());
            String category  = lireChampOuGarder("Catégorie", actuel.getCategory() != null ? actuel.getCategory() : "");
            int    total     = lireEntierPositifOuGarder("Nb exemplaires", actuel.getTotalCopies());

            Book modifie = new Book(isbn, title, author, publisher, year, category, total, actuel.getAvailableCopies());
            bookService.updateBook(modifie);
            System.out.println("  ✅ Livre modifié avec succès.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  ❌ Erreur : " + e.getMessage());
        }
    }

    public void handleDeleteBook() {
        System.out.println("\n── Supprimer un livre ──");
        try {
            String isbn = lireChamp("ISBN du livre à supprimer");
            Optional<Book> livre = bookService.findByIsbn(isbn);
            if (livre.isEmpty()) {
                System.out.println("  ❌ Aucun livre trouvé avec l'ISBN : " + isbn);
                return;
            }
            System.out.println("  Livre trouvé : " + livre.get().getTitle());
            System.out.print("  Confirmer la suppression ? (o/N) : ");
            if (scanner.nextLine().trim().equalsIgnoreCase("o")) {
                bookService.deleteBook(isbn);
                System.out.println("  ✅ Livre supprimé.");
            } else {
                System.out.println("  ↩ Suppression annulée.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("  ❌ Erreur : " + e.getMessage());
        }
    }

    public void handleSearchBooks() {
        System.out.println("\n── Rechercher un livre ──");
        System.out.print("  Mot-clé (titre, auteur, catégorie, ISBN) : ");
        String keyword = scanner.nextLine().trim();
        List<Book> resultats = bookService.search(keyword);
        if (resultats.isEmpty()) {
            System.out.println("  Aucun résultat pour : \"" + keyword + "\"");
        } else {
            System.out.println("  " + resultats.size() + " résultat(s) :");
            afficherListe(resultats);
        }
    }

    public void listBooks() {
        System.out.println("\n── Catalogue complet ──");
        List<Book> livres = bookService.getAllBooks();
        if (livres.isEmpty()) {
            System.out.println("  Aucun livre enregistré.");
        } else {
            System.out.println("  " + livres.size() + " livre(s) :");
            afficherListe(livres);
        }
    }

    public void handleFindByIsbn() {
        System.out.println("\n── Recherche par ISBN ──");
        String isbn = lireChamp("ISBN");
        bookService.findByIsbn(isbn).ifPresentOrElse(
            this::afficherDetail,
            () -> System.out.println("  ❌ Aucun livre trouvé avec l'ISBN : " + isbn)
        );
    }

    // ── Affichage ──────────────────────────────────────────────────────────

    private void afficherListe(List<Book> livres) {
        System.out.println("  " + "─".repeat(75));
        for (Book b : livres) {
            System.out.printf("  [%s] %-28s %-18s (%d) | Dispo: %d/%d%n",
                b.getIsbn(), tronquer(b.getTitle(), 28),
                tronquer(b.getAuthor(), 18), b.getPublishYear(),
                b.getAvailableCopies(), b.getTotalCopies());
        }
        System.out.println("  " + "─".repeat(75));
    }

    private void afficherDetail(Book b) {
        System.out.println("  ┌─ Détail ───────────────────────────────────");
        System.out.println("  │ ISBN       : " + b.getIsbn());
        System.out.println("  │ Titre      : " + b.getTitle());
        System.out.println("  │ Auteur     : " + b.getAuthor());
        System.out.println("  │ Éditeur    : " + (b.getPublisher() != null ? b.getPublisher() : "-"));
        System.out.println("  │ Année      : " + b.getPublishYear());
        System.out.println("  │ Catégorie  : " + (b.getCategory() != null ? b.getCategory() : "-"));
        System.out.println("  │ Disponible : " + b.getAvailableCopies() + "/" + b.getTotalCopies()
            + (b.getAvailableCopies() > 0 ? "  ✅" : "  ❌"));
        System.out.println("  └────────────────────────────────────────────");
    }

    // ── Saisie ─────────────────────────────────────────────────────────────

    private String lireChamp(String label) {
        String v;
        do {
            System.out.print("  " + label + " : ");
            v = scanner.nextLine().trim();
            if (v.isBlank()) System.out.println("  ⚠ Champ obligatoire.");
        } while (v.isBlank());
        return v;
    }

    private String lireChampOptional(String label) {
        System.out.print("  " + label + " : ");
        return scanner.nextLine().trim();
    }

    private String lireChampOuGarder(String label, String actuel) {
        System.out.print("  " + label + " [" + actuel + "] : ");
        String s = scanner.nextLine().trim();
        return s.isBlank() ? actuel : s;
    }

    private int lireEntier(String label) {
        while (true) {
            System.out.print("  " + label + " : ");
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  ⚠ Entier requis."); }
        }
    }

    private int lireEntierPositif(String label) {
        while (true) {
            int v = lireEntier(label);
            if (v >= 1) return v;
            System.out.println("  ⚠ La valeur doit être ≥ 1.");
        }
    }

    private int lireEntierOuGarder(String label, int actuel) {
        System.out.print("  " + label + " [" + actuel + "] : ");
        String s = scanner.nextLine().trim();
        if (s.isBlank()) return actuel;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return actuel; }
    }

    private int lireEntierPositifOuGarder(String label, int actuel) {
        while (true) {
            int v = lireEntierOuGarder(label, actuel);
            if (v >= 1) return v;
            System.out.println("  ⚠ La valeur doit être ≥ 1.");
        }
    }

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}