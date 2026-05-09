package com.library.ui.console;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.service.BookService;
import com.library.service.StatisticsService;
import com.library.util.ConsoleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StatisticsConsole {

    private final StatisticsService statisticsService;
    private final BookService bookService;

    public StatisticsConsole(StatisticsService statisticsService, BookService bookService) {
        this.statisticsService = statisticsService;
        this.bookService = bookService;
    }

    public void showStatisticsMenu() {
        while (true) {
            List<String> options = Arrays.asList(
                "1:Vue globale",
                "2:Top livres les plus empruntés",
                "3:Emprunts en retard",
                " ",
                "0:Retour au menu principal"
            );

            String choice = ConsoleUtils.displayMenu("STATISTIQUES & RAPPORTS", options);
            switch (choice) {
                case "1" -> showGlobalStats();
                case "2" -> showTopBooks();
                case "3" -> showOverdueLoans();
                case "0" -> { return; }
                default -> {
                    ConsoleUtils.printErrorCentered("Choix invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    public void showGlobalStats() {
        ConsoleUtils.printPageHeader("Vue globale");

        List<Book> books = bookService.getAllBooks();
        long titlesCount = books.size();
        long totalCopies = books.stream().mapToLong(Book::getTotalCopies).sum();
        long availableCopies = statisticsService.countAvailableBooks();
        long borrowedCopies = statisticsService.countBorrowedBooks();
        long overdueCount = statisticsService.overdueLoans().size();

        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.CYAN + ConsoleUtils.BOLD + "INDICATEURS PRINCIPAUX" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "─".repeat(45) + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Titres au catalogue   : " + ConsoleUtils.WHITE + titlesCount + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Exemplaires totaux    : " + ConsoleUtils.WHITE + totalCopies + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Exemplaires disponibles: " + ConsoleUtils.GREEN + availableCopies + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Exemplaires empruntés : " + ConsoleUtils.YELLOW + borrowedCopies + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Emprunts en retard    : " + (overdueCount > 0 ? ConsoleUtils.RED : ConsoleUtils.GREEN) + overdueCount + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "─".repeat(45) + ConsoleUtils.RESET);

        ConsoleUtils.pause();
    }

    public void showTopBooks() {
        ConsoleUtils.printPageHeader("Top livres les plus empruntés");

        int limit = ConsoleUtils.readIntCentered("Nombre de livres à afficher");
        if (limit <= 0) {
            ConsoleUtils.printErrorCentered("Le nombre doit être supérieur à 0.");
            ConsoleUtils.pause();
            return;
        }

        List<Book> topBooks = statisticsService.topBorrowedBooks(limit);
        if (topBooks.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucune donnée d'emprunt disponible.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println();
        String headerFormat = ConsoleUtils.MARGIN + ConsoleUtils.BOLD + " %-4s | %-35s | %-15s | %s" + ConsoleUtils.RESET;
        String rowFormat = ConsoleUtils.MARGIN + " %-4s | %-35s | %-15s | %s";

        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(85) + ConsoleUtils.RESET);
        System.out.printf(headerFormat + "%n", "RANG", "TITRE", "AUTEUR", "ISBN");
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(85) + ConsoleUtils.RESET);

        int rank = 1;
        for (Book b : topBooks) {
            System.out.printf(rowFormat + "%n",
                rank++,
                truncate(b.getTitle(), 35),
                truncate(b.getAuthor(), 15),
                b.getIsbn());
        }
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(85) + ConsoleUtils.RESET);

        ConsoleUtils.pause();
    }

    public void showOverdueLoans() {
        ConsoleUtils.printPageHeader("Emprunts en retard");
        List<Loan> overdueLoans = statisticsService.overdueLoans();

        if (overdueLoans.isEmpty()) {
            ConsoleUtils.printSuccessCentered("Aucun emprunt en retard.");
            ConsoleUtils.pause();
            return;
        }

        System.out.println();
        String headerFormat = ConsoleUtils.MARGIN + ConsoleUtils.BOLD + " %-36s | %-25s | %-12s | %s" + ConsoleUtils.RESET;
        String rowFormat = ConsoleUtils.MARGIN + " %-36s | %-25s | %-12s | %s";

        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(95) + ConsoleUtils.RESET);
        System.out.printf(headerFormat + "%n", "ID EMPRUNT", "TITRE", "DATE RETOUR", "ID UTILISATEUR");
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(95) + ConsoleUtils.RESET);

        for (Loan loan : overdueLoans) {
            String title = "Livre inconnu";
            Optional<Book> bookOpt = bookService.findByIsbn(loan.getBookIsbn());
            if (bookOpt.isPresent()) {
                title = bookOpt.get().getTitle();
            }
            System.out.printf(rowFormat + "%n",
                loan.getId(),
                truncate(title, 25),
                String.valueOf(loan.getDueDate()),
                loan.getUserId());
        }
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(95) + ConsoleUtils.RESET);

        ConsoleUtils.pause();
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max - 1) + ".";
    }
}
