package com.library.ui.console;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.service.StatisticsService;
import com.library.util.ConsoleUtils;

import java.util.List;

public class StatisticsConsole {

    private final StatisticsService statisticsService;

    public StatisticsConsole(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public void showStatisticsMenu() {
        boolean retour = false;
        while (!retour) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("Statistiques");
            System.out.println();
            System.out.println(ConsoleUtils.BLUE + "  ╔══════════════════════════════════════╗" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  1. Vue d'ensemble                   " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  2. Top 5 livres les plus empruntés  " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  3. Emprunts en retard               " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  0. Retour                           " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ╚══════════════════════════════════════╝" + ConsoleUtils.RESET);
            System.out.println();

            String choix = ConsoleUtils.readLine(ConsoleUtils.BOLD + "    Votre choix : " + ConsoleUtils.RESET);

            switch (choix) {
                case "1" -> handleOverview();
                case "2" -> handleTopBooks();
                case "3" -> handleOverdueLoans();
                case "0" -> retour = true;
                default  -> {
                    ConsoleUtils.printError("Option invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    // ── Vue d'ensemble ────────────────────────────────────────────────────────

    private void handleOverview() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Vue d'ensemble");

        long disponibles = statisticsService.countAvailableBooks();
        long empruntes   = statisticsService.countBorrowedBooks();
        long total       = disponibles + empruntes;

        System.out.println();
        System.out.println(ConsoleUtils.BLUE + "  ┌─────────────────────────────────────┐" + ConsoleUtils.RESET);
        System.out.printf(ConsoleUtils.BLUE  + "  │" + ConsoleUtils.RESET
            + ConsoleUtils.BOLD + "  Total exemplaires    : " + ConsoleUtils.RESET
            + ConsoleUtils.CYAN + "%-14d" + ConsoleUtils.RESET
            + ConsoleUtils.BLUE + "│%n" + ConsoleUtils.RESET, total);
        System.out.printf(ConsoleUtils.BLUE  + "  │" + ConsoleUtils.RESET
            + "  Disponibles           : "
            + ConsoleUtils.GREEN + "%-14d" + ConsoleUtils.RESET
            + ConsoleUtils.BLUE + "│%n" + ConsoleUtils.RESET, disponibles);
        System.out.printf(ConsoleUtils.BLUE  + "  │" + ConsoleUtils.RESET
            + "  Empruntés             : "
            + ConsoleUtils.YELLOW + "%-14d" + ConsoleUtils.RESET
            + ConsoleUtils.BLUE + "│%n" + ConsoleUtils.RESET, empruntes);
        System.out.println(ConsoleUtils.BLUE + "  └─────────────────────────────────────┘" + ConsoleUtils.RESET);

        ConsoleUtils.pause();
    }

    // ── Top livres ────────────────────────────────────────────────────────────

    private void handleTopBooks() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Top 5 livres les plus empruntés");

        List<Book> topBooks = statisticsService.topBorrowedBooks(5);

        if (topBooks.isEmpty()) {
            System.out.println("  Aucune donnée d'emprunt disponible.");
        } else {
            System.out.println();
            int rang = 1;
            for (Book b : topBooks) {
                String medal = switch (rang) {
                    case 1 -> ConsoleUtils.YELLOW + "🥇";
                    case 2 -> ConsoleUtils.CYAN   + "🥈";
                    case 3 -> ConsoleUtils.GREEN  + "🥉";
                    default -> "   " + rang + ".";
                };
                System.out.printf("  %s  %-30s — %s (%d)%n" + ConsoleUtils.RESET,
                    medal,
                    tronquer(b.getTitle(), 30),
                    b.getAuthor(),
                    b.getPublishYear());
                rang++;
            }
        }
        ConsoleUtils.pause();
    }

    // ── Emprunts en retard ────────────────────────────────────────────────────

    private void handleOverdueLoans() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Emprunts en retard");

        List<Loan> overdue = statisticsService.overdueLoans();

        if (overdue.isEmpty()) {
            System.out.println(ConsoleUtils.GREEN + "  ✔ Aucun emprunt en retard." + ConsoleUtils.RESET);
        } else {
            System.out.println(ConsoleUtils.RED + "  ⚠ " + overdue.size() + " emprunt(s) en retard :\n" + ConsoleUtils.RESET);
            System.out.println("  " + "─".repeat(65));
            System.out.printf(ConsoleUtils.BOLD + "  %-36s %-14s %-10s%n" + ConsoleUtils.RESET,
                "ID emprunt", "ISBN", "Échéance");
            System.out.println("  " + "─".repeat(65));
            for (Loan l : overdue) {
                System.out.printf("  " + ConsoleUtils.RED + "%-36s %-14s %-10s%n" + ConsoleUtils.RESET,
                    l.getId(), l.getBookIsbn(), l.getDueDate());
            }
            System.out.println("  " + "─".repeat(65));
        }
        ConsoleUtils.pause();
    }

    // ── Utilitaire ────────────────────────────────────────────────────────────

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}
