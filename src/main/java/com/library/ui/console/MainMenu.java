package com.library.ui.console;

import com.library.config.FeatureFlags;
import com.library.model.Role;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.util.ConsoleUtils;
import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    private final AuthService authService;
    private final BookConsole bookConsole;
    private final UserConsole userConsole;
    private final LoanConsole loanConsole;
    private final StatisticsConsole statisticsConsole;

    public MainMenu(AuthService authService, BookConsole bookConsole, UserConsole userConsole, LoanConsole loanConsole, StatisticsConsole statisticsConsole) {
        this.authService = authService;
        this.bookConsole = bookConsole;
        this.userConsole = userConsole;
        this.loanConsole = loanConsole;
        this.statisticsConsole = statisticsConsole;
    }

    public void start() {
        while (true) {
            User currentUser = authService.getCurrentUser().orElse(null);
            if (currentUser == null) {
                // Par sécurité, si pas d'utilisateur connecté, on retourne à l'écran de connexion
                return;
            }

            List<String> options = new ArrayList<>();
            Role role = currentUser.getRole();

            if (!FeatureFlags.FULL_CONSOLE_MODE) {
                options.add("1:Gestion des livres");
            } else if (role == Role.ADMIN || role == Role.LIBRARIAN) {
                options.add("1:Gestion des livres");
                options.add("2:Gestion des membres");
                options.add("3:Gestion des emprunts");
                options.add("4:Statistiques & Rapports");
            } else {
                // Utilisateur standard (MEMBER)
                options.add("1:Rechercher un livre");
                options.add("2:Mes emprunts");
            }
            
            options.add(" ");
            options.add("0:Se déconnecter");

            String title = "BIBLIOTHÈQUE - " + currentUser.getFirstName().toUpperCase();
            String choice = ConsoleUtils.displayMenu(title, options);

            if (!FeatureFlags.FULL_CONSOLE_MODE) {
                switch (choice) {
                    case "1" -> bookConsole.showBookMenu();
                    case "0" -> {
                        authService.logout();
                        ConsoleUtils.printSuccessCentered("Déconnexion réussie.");
                        ConsoleUtils.pause();
                        return;
                    }
                    default -> {
                        ConsoleUtils.printErrorCentered("Option indisponible en mode limité.");
                        ConsoleUtils.pause();
                    }
                }
            } else if (role == Role.ADMIN || role == Role.LIBRARIAN) {
                switch (choice) {
                    case "1" -> bookConsole.showBookMenu();
                    case "2" -> userConsole.showUserMenu(role);
                    case "3" -> loanConsole.showLoanManagementMenu();
                    case "4" -> statisticsConsole.showStatisticsMenu();
                    case "0" -> {
                        authService.logout();
                        ConsoleUtils.printSuccessCentered("Déconnexion réussie.");
                        ConsoleUtils.pause();
                        return; // Retourne à Main (qui reboucle sur AuthConsole)
                    }
                    default -> { ConsoleUtils.printErrorCentered("Option invalide."); ConsoleUtils.pause(); }
                }
            } else {
                switch (choice) {
                    case "1" -> bookConsole.handleSearchBooks();
                    case "2" -> loanConsole.showMyLoansMenu();
                    case "0" -> {
                        authService.logout();
                        ConsoleUtils.printSuccessCentered("Déconnexion réussie.");
                        ConsoleUtils.pause();
                        return;
                    }
                    default -> { ConsoleUtils.printErrorCentered("Option invalide."); ConsoleUtils.pause(); }
                }
            }
        }
    }
}
