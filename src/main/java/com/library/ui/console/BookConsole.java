package com.library.ui.console;

import com.library.config.FeatureFlags;
import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.util.ConsoleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookConsole {

    private final BookService bookService;
    private final AuthService authService;
    private final LoanService loanService;

    public BookConsole(BookService bookService, AuthService authService, LoanService loanService) {
        this.bookService = bookService;
        this.authService = authService;
        this.loanService = loanService;
    }

    public void showBookMenu() {
        while (true) {
            List<String> options = Arrays.asList(
                "1:Ajouter un livre",
                "2:Modifier un livre",
                "3:Supprimer un livre",
                "4:Rechercher un livre",
                "5:Catalogue complet",
                "6:Détails d'un livre",
                " ",
                "0:Retour au menu principal"
            );

            String choice = ConsoleUtils.displayMenu("GESTION DES LIVRES", options);
            switch (choice) {
                case "1" -> handleAddBook();
                case "2" -> handleUpdateBook();
                case "3" -> handleDeleteBook();
                case "4" -> handleSearchBooks();
                case "5" -> listBooks();
                case "6" -> handleFindByIsbn();
                case "0" -> { return; }
                default  -> {
                    ConsoleUtils.printErrorCentered("Choix invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    public void handleAddBook() {
        ConsoleUtils.printPageHeader("Ajouter un nouveau livre");
        try {
            String isbn      = ConsoleUtils.readNonEmptyStringCentered("ISBN");
            String title     = ConsoleUtils.readNonEmptyStringCentered("Titre");
            String author    = ConsoleUtils.readNonEmptyStringCentered("Auteur");
            String publisher = ConsoleUtils.readLineCentered("Éditeur (optionnel)");
            int    year      = ConsoleUtils.readIntCentered("Année de publication");
            String category  = ConsoleUtils.readLineCentered("Catégorie (optionnel)");
            int    total     = lireEntierPositif("Nombre d'exemplaires");

            Book book = new Book(isbn, title, author, publisher, year, category, total, total);
            bookService.addBook(book);
            System.out.println();
            ConsoleUtils.printSuccessCentered("Livre ajouté : " + title);
        } catch (IllegalArgumentException e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void handleUpdateBook() {
        ConsoleUtils.printPageHeader("Modifier un livre");
        try {
            String isbn = ConsoleUtils.readNonEmptyStringCentered("ISBN du livre à modifier");
            Optional<Book> existant = bookService.findByIsbn(isbn);
            if (existant.isEmpty()) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Aucun livre trouvé avec l'ISBN : " + isbn);
            } else {
                Book actuel = existant.get();
                System.out.println("\n  Modification de : " + ConsoleUtils.CYAN + actuel.getTitle() + ConsoleUtils.RESET);
                System.out.println("  (Laissez vide pour conserver la valeur actuelle)\n");

                String title     = lireChampOuGarder("Titre",    actuel.getTitle());
                String author    = lireChampOuGarder("Auteur",   actuel.getAuthor());
                String publisher = lireChampOuGarder("Éditeur",  actuel.getPublisher() != null ? actuel.getPublisher() : "");
                int    year      = lireEntierOuGarder("Année",   actuel.getPublishYear());
                String category  = lireChampOuGarder("Catégorie", actuel.getCategory() != null ? actuel.getCategory() : "");
                int    total     = lireEntierPositifOuGarder("Nb exemplaires", actuel.getTotalCopies());

                Book modifie = new Book(isbn, title, author, publisher, year, category, total, actuel.getAvailableCopies());
                bookService.updateBook(modifie);
                System.out.println();
                ConsoleUtils.printSuccessCentered("Livre mis à jour avec succès.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void handleDeleteBook() {
        ConsoleUtils.printPageHeader("Supprimer un livre");
        try {
            String isbn = ConsoleUtils.readNonEmptyStringCentered("ISBN du livre à supprimer");
            Optional<Book> livre = bookService.findByIsbn(isbn);
            if (livre.isEmpty()) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Aucun livre trouvé.");
            } else {
                System.out.println("\n  Confirmez-vous la suppression de : " + ConsoleUtils.BOLD + livre.get().getTitle() + ConsoleUtils.RESET);
                String confirm = ConsoleUtils.readLineCentered("Confirmer (o/N)");
                if (confirm.equalsIgnoreCase("o")) {
                    bookService.deleteBook(isbn);
                    System.out.println();
                    ConsoleUtils.printSuccessCentered("Le livre a été supprimé.");
                } else {
                    System.out.println();
                    ConsoleUtils.printErrorCentered("Opération annulée.");
                }
            }
        } catch (Exception e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    public void handleSearchBooks() {
        ConsoleUtils.printPageHeader("Rechercher dans le catalogue");
        String keyword = ConsoleUtils.readLineCentered("Mot-clé");
        List<Book> resultats = bookService.search(keyword);
        System.out.println();
        if (resultats.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucun résultat trouvé pour : " + keyword);
        } else {
            System.out.println("  " + resultats.size() + " livre(s) trouvé(s) :");
            afficherListe(resultats);
            
            if (isMemberConnected()) {
                // Proposition d'emprunt reservee aux membres
                System.out.println();
                String isbnToBorrow = ConsoleUtils.readLineCentered("Entrez l'ISBN du livre à emprunter (ou laissez vide pour annuler)");
                if (!isbnToBorrow.isEmpty()) {
                    Optional<Book> bookOpt = bookService.findByIsbn(isbnToBorrow);
                    if (bookOpt.isPresent()) {
                        Book book = bookOpt.get();
                        if (book.getAvailableCopies() > 0) {
                            tryBorrowBook(book);
                        } else {
                            System.out.println();
                            ConsoleUtils.printErrorCentered("Ce livre n'est plus disponible.");
                        }
                    } else {
                        System.out.println();
                        ConsoleUtils.printErrorCentered("ISBN introuvable dans cette liste.");
                    }
                }
            }
        }
        ConsoleUtils.pause();
    }

    public void listBooks() {
        ConsoleUtils.printPageHeader("Catalogue complet");
        List<Book> livres = bookService.getAllBooks();
        if (livres.isEmpty()) {
            ConsoleUtils.printErrorCentered("La bibliothèque est vide.");
        } else {
            afficherListe(livres);
        }
        ConsoleUtils.pause();
    }

    public void handleFindByIsbn() {
        ConsoleUtils.printPageHeader("Détails du livre");
        String isbn = ConsoleUtils.readNonEmptyStringCentered("ISBN");
        System.out.println();
        Optional<Book> bookOpt = bookService.findByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            afficherDetail(book);
            
            if (book.getAvailableCopies() > 0) {
                if (isMemberConnected()) {
                    System.out.println();
                    String confirm = ConsoleUtils.readLineCentered("Voulez-vous emprunter ce livre ? (o/N)");
                    if (confirm.equalsIgnoreCase("o")) {
                        tryBorrowBook(book);
                    }
                } else {
                    System.out.println();
                }
            } else {
                System.out.println();
                ConsoleUtils.printErrorCentered("Ce livre est actuellement indisponible ou vous n'êtes pas connecté.");
            }
        }
        ConsoleUtils.pause();
    }

    private void tryBorrowBook(Book book) {
        Optional<User> currentUserOpt = authService.getCurrentUser();
        if (currentUserOpt.isPresent()) {
            if (currentUserOpt.get().getRole() != Role.MEMBER) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Seuls les membres peuvent emprunter depuis ce menu.");
                return;
            }
            try {
                loanService.borrowBook(currentUserOpt.get().getId(), book.getIsbn());
                System.out.println();
                ConsoleUtils.printSuccessCentered("Emprunt réussi ! Vous avez 14 jours pour le retourner.");
            } catch (Exception e) {
                System.out.println();
                ConsoleUtils.printErrorCentered(e.getMessage());
            }
        } else {
            System.out.println();
            ConsoleUtils.printErrorCentered("Vous devez être connecté pour emprunter.");
        }
    }

    private boolean isMemberConnected() {
        if (!FeatureFlags.FULL_CONSOLE_MODE) {
            return false;
        }
        return authService.getCurrentUser()
            .map(user -> user.getRole() == Role.MEMBER)
            .orElse(false);
    }

    private void afficherListe(List<Book> livres) {
        System.out.println();
        String headerFormat = ConsoleUtils.MARGIN + ConsoleUtils.BOLD + " %-15s | %-25s | %-15s | %-10s" + ConsoleUtils.RESET;
        String rowFormat = ConsoleUtils.MARGIN + " %-15s | %-25s | %-15s | %s";
        
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(75) + ConsoleUtils.RESET);
        System.out.printf(headerFormat + "%n", "ISBN", "TITRE", "AUTEUR", "STATUT");
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(75) + ConsoleUtils.RESET);
        
        for (Book b : livres) {
            System.out.printf(rowFormat + "%n", 
                b.getIsbn(), tronquer(b.getTitle(), 25), tronquer(b.getAuthor(), 15),
                (b.getAvailableCopies() > 0 ? ConsoleUtils.GREEN + "Disponible" + ConsoleUtils.RESET : ConsoleUtils.RED + "Emprunté  " + ConsoleUtils.RESET));
        }
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(75) + ConsoleUtils.RESET);
    }

    private void afficherDetail(Book b) {
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.CYAN + ConsoleUtils.BOLD + "DÉTAILS DU LIVRE" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "─".repeat(40) + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  ISBN        : " + ConsoleUtils.WHITE + b.getIsbn() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Titre       : " + ConsoleUtils.WHITE + b.getTitle() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Auteur      : " + ConsoleUtils.WHITE + b.getAuthor() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Éditeur     : " + ConsoleUtils.WHITE + (b.getPublisher() == null || b.getPublisher().isEmpty() ? "-" : b.getPublisher()) + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Année       : " + ConsoleUtils.WHITE + b.getPublishYear() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Catégorie   : " + ConsoleUtils.WHITE + (b.getCategory() == null || b.getCategory().isEmpty() ? "-" : b.getCategory()) + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Stock       : " + ConsoleUtils.WHITE + b.getAvailableCopies() + " / " + b.getTotalCopies() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "─".repeat(40) + ConsoleUtils.RESET);
    }

    private String lireChampOuGarder(String label, String actuel) {
        String s = ConsoleUtils.readLineCentered(label + " [" + actuel + "]");
        return s.isEmpty() ? actuel : s;
    }

    private int lireEntierPositif(String label) {
        while (true) {
            int v = ConsoleUtils.readIntCentered(label);
            if (v >= 1) return v;
            ConsoleUtils.printErrorCentered("La valeur doit être supérieure à 0.");
        }
    }

    private int lireEntierOuGarder(String label, int actuel) {
        String s = ConsoleUtils.readLineCentered(label + " [" + actuel + "]");
        if (s.isEmpty()) return actuel;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return actuel; }
    }

    private int lireEntierPositifOuGarder(String label, int actuel) {
        while (true) {
            int v = lireEntierOuGarder(label, actuel);
            if (v >= 1) return v;
            ConsoleUtils.printErrorCentered("La valeur doit être supérieure à 0.");
        }
    }

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}