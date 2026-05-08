package com.library.ui.console;

import com.library.model.Loan;
import com.library.service.LoanService;
import com.library.util.ConsoleUtils;

import java.util.List;

public class LoanConsole {

    private final LoanService loanService;

    public LoanConsole(LoanService loanService) {
        this.loanService = loanService;
    }

    public void showLoanMenu() {
        boolean retour = false;
        while (!retour) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("Gestion des emprunts");
            System.out.println();
            System.out.println(ConsoleUtils.BLUE + "  ╔══════════════════════════════════════╗" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  1. Emprunter un livre               " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  2. Retourner un livre               " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  3. Prolonger un emprunt             " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  4. Emprunts d'un utilisateur        " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  5. Emprunts actifs                  " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  0. Retour                           " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ╚══════════════════════════════════════╝" + ConsoleUtils.RESET);
            System.out.println();

            String choix = ConsoleUtils.readLine(ConsoleUtils.BOLD + "    Votre choix : " + ConsoleUtils.RESET);

            switch (choix) {
                case "1" -> handleBorrowBook();
                case "2" -> handleReturnBook();
                case "3" -> handleExtendLoan();
                case "4" -> handleLoansByUser();
                case "5" -> handleActiveLoans();
                case "0" -> retour = true;
                default  -> {
                    ConsoleUtils.printError("Option invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    // ── Emprunter un livre ────────────────────────────────────────────────────

    private void handleBorrowBook() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Emprunter un livre");
        try {
            String userId = ConsoleUtils.readNonEmptyString("  ID utilisateur : ");
            String isbn   = ConsoleUtils.readNonEmptyString("  ISBN du livre  : ");
            loanService.borrowBook(userId, isbn);
            System.out.println(ConsoleUtils.GREEN + "\n  ✔ Emprunt enregistré. Retour attendu dans 14 jours." + ConsoleUtils.RESET);
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Retourner un livre ────────────────────────────────────────────────────

    private void handleReturnBook() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Retourner un livre");
        try {
            String loanId = ConsoleUtils.readNonEmptyString("  ID de l'emprunt : ");
            loanService.returnBook(loanId);
            System.out.println(ConsoleUtils.GREEN + "\n  ✔ Retour enregistré avec succès." + ConsoleUtils.RESET);
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Prolonger un emprunt ──────────────────────────────────────────────────

    private void handleExtendLoan() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Prolonger un emprunt");
        try {
            String loanId   = ConsoleUtils.readNonEmptyString("  ID de l'emprunt     : ");
            int    extraDays = ConsoleUtils.readInt("  Nombre de jours supplémentaires : ");
            loanService.extendLoan(loanId, extraDays);
            System.out.println(ConsoleUtils.GREEN + "\n  ✔ Emprunt prolongé de " + extraDays + " jour(s)." + ConsoleUtils.RESET);
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Emprunts d'un utilisateur ─────────────────────────────────────────────

    private void handleLoansByUser() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Emprunts d'un utilisateur");

        String userId = ConsoleUtils.readNonEmptyString("  ID utilisateur : ");
        List<Loan> loans = loanService.getLoansByUser(userId);

        if (loans.isEmpty()) {
            System.out.println("  Aucun emprunt trouvé pour cet utilisateur.");
        } else {
            System.out.println("  " + loans.size() + " emprunt(s) :\n");
            afficherListeEmprunts(loans);
        }
        ConsoleUtils.pause();
    }

    // ── Emprunts actifs ───────────────────────────────────────────────────────

    private void handleActiveLoans() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Emprunts actifs");

        List<Loan> loans = loanService.getActiveLoans();

        if (loans.isEmpty()) {
            System.out.println("  Aucun emprunt actif en ce moment.");
        } else {
            System.out.println("  " + loans.size() + " emprunt(s) actif(s) :\n");
            afficherListeEmprunts(loans);
        }
        ConsoleUtils.pause();
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    private void afficherListeEmprunts(List<Loan> loans) {
        System.out.println("  " + "─".repeat(72));
        System.out.printf(ConsoleUtils.BOLD + "  %-36s %-14s %-10s %-10s%n" + ConsoleUtils.RESET,
            "ID emprunt", "ISBN", "Échéance", "Statut");
        System.out.println("  " + "─".repeat(72));
        for (Loan l : loans) {
            System.out.printf("  %-36s %-14s %-10s %-10s%n",
                l.getId(),
                l.getBookIsbn(),
                l.getDueDate(),
                l.getStatus());
        }
        System.out.println("  " + "─".repeat(72));
    }
}
