package com.library;

import com.library.config.AppConfig;
import com.library.model.Role;
import com.library.model.User;
import com.library.model.UserStatus;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.StatisticsService;
import com.library.service.UserService;
import com.library.ui.console.AuthConsole;
import com.library.ui.console.BookConsole;
import com.library.ui.console.MainMenu;
import com.library.ui.console.StatisticsConsole;
import com.library.ui.console.UserConsole;
import com.library.util.PasswordUtils;

import java.time.LocalDate;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        // Initialisation des dépendances
        UserService userService = AppConfig.userService();
        AuthService authService = AppConfig.authService();
        BookService bookService = AppConfig.bookService();
        com.library.service.LoanService loanService = AppConfig.loanService();
        StatisticsService statisticsService = AppConfig.statisticsService();

        // Création de l'admin par défaut si la base est vide
        if (userService.getAllUsers().isEmpty()) {
            User admin = new User(
                UUID.randomUUID().toString(),
                "admin",
                PasswordUtils.hash("admin"),
                "Admin",
                "System",
                "admin@library.com",
                "0000000000",
                Role.ADMIN,
                UserStatus.ACTIVE,
                LocalDate.now()
            );
            userService.createUser(admin);
        }

        // Instanciation des consoles
        AuthConsole authConsole = new AuthConsole(authService);
        BookConsole bookConsole = new BookConsole(bookService, authService, loanService);
        UserConsole userConsole = new UserConsole(userService);
        com.library.ui.console.LoanConsole loanConsole = new com.library.ui.console.LoanConsole(loanService, authService, bookService);
        StatisticsConsole statisticsConsole = new StatisticsConsole(statisticsService, bookService);
        MainMenu mainMenu = new MainMenu(authService, bookConsole, userConsole, loanConsole, statisticsConsole);

        while (true) {
            boolean isAuthenticated = authConsole.showLoginScreen();
            
            if (isAuthenticated) {
                mainMenu.start();
                // Si start() se termine, ça veut dire que l'utilisateur s'est déconnecté.
            } else {
                System.out.println("\nFermeture de l'application... Au revoir !");
                break;
            }
        }
    }
}
