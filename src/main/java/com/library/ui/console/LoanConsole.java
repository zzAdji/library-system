package com.library.ui.console;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.LoanStatus;
import com.library.model.Role;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.util.ConsoleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LoanConsole {

    private final LoanService loanService;
    private final AuthService authService;
    private final BookService bookService;

    public LoanConsole(LoanService loanService, AuthService authService, BookService bookService) {
        this.loanService = loanService;
        this.authService = authService;
        this.bookService = bookService;
    }

    public void showMyLoansMenu() {
        Optional<User> currentUserOpt = authService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            ConsoleUtils.printErrorCentered("Vous devez être connecté pour voir vos emprunts.");
            ConsoleUtils.pause();
            return;
        }

        User currentUser = currentUserOpt.get();

        while (true) {
            List<String> options = Arrays.asList(
                "1:Voir mes emprunts actifs",
                "2:Retourner un livre",
                "3:Historique de mes emprunts",
                " ",
                "0:Retour au menu principal"
            );

            String choice = ConsoleUtils.displayMenu("MES EMPRUNTS", options);
            switch (choice) {
                case "1" -> listMyActiveLoans(currentUser.getId());
                case "2" -> handleReturnBook(currentUser.getId());
                case "3" -> listMyHistoryLoans(currentUser.getId());
                case "0" -> { return; }
                default  -> {
                    ConsoleUtils.printErrorCentered("Choix invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    public void showLoanManagementMenu() {
        Optional<User> currentUserOpt = authService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            ConsoleUtils.printErrorCentered("Vous devez être connecté.");
            ConsoleUtils.pause();
            return;
        }

        User currentUser = currentUserOpt.get();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.LIBRARIAN) {
            ConsoleUtils.printErrorCentered("Accès refusé : menu réservé à l'administration.");
            ConsoleUtils.pause();
            return;
        }

        while (true) {
            List<String> options = Arrays.asList(
                "1:Voir tous les emprunts en cours",
                "2:Voir l'historique complet des emprunts",
                "3:Retourner un livre (opération de comptoir)",
                "4:Prolonger un emprunt",
                " ",
                "0:Retour au menu principal"
            );

            String choice = ConsoleUtils.displayMenu("GESTION DES EMPRUNTS", options);
            switch (choice) {
                case "1" -> listAllActiveLoans();
                case "2" -> listAllLoans();
                case "3" -> handleReturnLoanByStaff();
                case "4" -> handleExtendLoanByStaff();
                case "0" -> { return; }
                default  -> {
                    ConsoleUtils.printErrorCentered("Choix invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    private void listMyActiveLoans(String userId) {
        ConsoleUtils.printPageHeader("Mes emprunts actifs");
        List<Loan> loans = loanService.getLoansByUser(userId).stream()
                .filter(l -> l.getStatus() == LoanStatus.ONGOING || l.getStatus() == LoanStatus.OVERDUE)
                .toList();
        
        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Vous n'avez aucun emprunt en cours.");
        } else {
            afficherListeEmprunts(loans);
        }
        ConsoleUtils.pause();
    }

    private void listMyHistoryLoans(String userId) {
        ConsoleUtils.printPageHeader("Historique complet");
        List<Loan> loans = loanService.getLoansByUser(userId);
        
        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Vous n'avez aucun historique d'emprunt.");
        } else {
            afficherListeEmprunts(loans);
        }
        ConsoleUtils.pause();
    }

    private void handleReturnBook(String userId) {
        ConsoleUtils.printPageHeader("Retourner un livre");
        List<Loan> loans = loanService.getLoansByUser(userId).stream()
                .filter(l -> l.getStatus() == LoanStatus.ONGOING || l.getStatus() == LoanStatus.OVERDUE)
                .toList();
        
        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Vous n'avez aucun livre à retourner.");
            ConsoleUtils.pause();
            return;
        }

        afficherListeEmprunts(loans);
        System.out.println();
        String loanId = ConsoleUtils.readNonEmptyStringCentered("Entrez l'ID de l'emprunt à retourner (ou laissez vide pour annuler)");
        
        if (!loanId.isEmpty()) {
            // Vérifier que cet emprunt appartient bien à l'utilisateur et est en cours
            boolean isValid = loans.stream().anyMatch(l -> Objects.equals(l.getId(), loanId));
            if (isValid) {
                try {
                    loanService.returnBook(loanId);
                    System.out.println();
                    ConsoleUtils.printSuccessCentered("Livre retourné avec succès !");
                } catch (Exception e) {
                    System.out.println();
                    ConsoleUtils.printErrorCentered(e.getMessage());
                }
            } else {
                System.out.println();
                ConsoleUtils.printErrorCentered("ID d'emprunt invalide ou ne vous appartient pas.");
            }
        }
        ConsoleUtils.pause();
    }

    private void listAllActiveLoans() {
        ConsoleUtils.printPageHeader("Tous les emprunts en cours");
        List<Loan> loans = loanService.getActiveLoans();

        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucun emprunt actif.");
        } else {
            afficherListeEmprunts(loans);
        }
        ConsoleUtils.pause();
    }

    private void listAllLoans() {
        ConsoleUtils.printPageHeader("Historique global des emprunts");
        List<Loan> loans = loanService.getAllLoans();

        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucun emprunt enregistré.");
        } else {
            afficherListeEmprunts(loans);
        }
        ConsoleUtils.pause();
    }

    private void handleReturnLoanByStaff() {
        ConsoleUtils.printPageHeader("Retourner un livre (staff)");
        List<Loan> loans = loanService.getActiveLoans();

        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucun emprunt actif à retourner.");
            ConsoleUtils.pause();
            return;
        }

        afficherListeEmprunts(loans);
        System.out.println();
        String loanId = ConsoleUtils.readNonEmptyStringCentered("ID de l'emprunt à clôturer");

        try {
            loanService.returnBook(loanId);
            System.out.println();
            ConsoleUtils.printSuccessCentered("Emprunt clôturé avec succès.");
        } catch (Exception e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void handleExtendLoanByStaff() {
        ConsoleUtils.printPageHeader("Prolonger un emprunt");
        List<Loan> loans = loanService.getActiveLoans();

        if (loans.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucun emprunt actif à prolonger.");
            ConsoleUtils.pause();
            return;
        }

        afficherListeEmprunts(loans);
        System.out.println();
        String loanId = ConsoleUtils.readNonEmptyStringCentered("ID de l'emprunt à prolonger");
        int extraDays = ConsoleUtils.readIntCentered("Nombre de jours à ajouter");
        if (extraDays <= 0) {
            ConsoleUtils.printErrorCentered("Le nombre de jours doit être supérieur à 0.");
            ConsoleUtils.pause();
            return;
        }

        try {
            loanService.extendLoan(loanId, extraDays);
            System.out.println();
            ConsoleUtils.printSuccessCentered("Emprunt prolongé avec succès.");
        } catch (Exception e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void afficherListeEmprunts(List<Loan> loans) {
        System.out.println();
        String headerFormat = ConsoleUtils.MARGIN + ConsoleUtils.BOLD + " %-36s | %-25s | %-12s | %s" + ConsoleUtils.RESET;
        String rowFormat = ConsoleUtils.MARGIN + " %-36s | %-25s | %-12s | %s";
        
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(95) + ConsoleUtils.RESET);
        System.out.printf(headerFormat + "%n", "ID EMPRUNT", "TITRE DU LIVRE", "DATE RETOUR", "STATUT");
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(95) + ConsoleUtils.RESET);
        
        for (Loan l : loans) {
            String title = "Livre inconnu";
            Optional<Book> bookOpt = bookService.findByIsbn(l.getBookIsbn());
            if (bookOpt.isPresent()) {
                title = bookOpt.get().getTitle();
            }
            
            String statusColor = switch (l.getStatus()) {
                case ONGOING -> ConsoleUtils.CYAN + "En cours" + ConsoleUtils.RESET;
                case RETURNED -> ConsoleUtils.GREEN + "Retourné" + ConsoleUtils.RESET;
                case OVERDUE -> ConsoleUtils.RED + "En retard" + ConsoleUtils.RESET;
            };

            System.out.printf(rowFormat + "%n", 
                l.getId(), tronquer(title, 25), l.getDueDate().toString(), statusColor);
        }
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(95) + ConsoleUtils.RESET);
    }

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}
