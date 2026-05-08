package com.library;

import com.library.config.AppConfig;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.StatisticsService;
import com.library.service.UserService;
import com.library.ui.console.AuthConsole;
import com.library.ui.console.BookConsole;
import com.library.ui.console.LoanConsole;
import com.library.ui.console.MainMenu;
import com.library.ui.console.StatisticsConsole;
import com.library.ui.console.UserConsole;

import java.util.Scanner;

/**
 * Point d'entrée unique de l'application.
 * Instancie tous les services via AppConfig et lance MainMenu.
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ── Services ──────────────────────────────────────────────────────────
        AuthService       authService       = AppConfig.authService();
        BookService       bookService       = AppConfig.bookService();
        UserService       userService       = AppConfig.userService();
        LoanService       loanService       = AppConfig.loanService();
        StatisticsService statisticsService = AppConfig.statisticsService();

        // ── Consoles UI ───────────────────────────────────────────────────────
        AuthConsole       authConsole       = new AuthConsole(authService);
        BookConsole       bookConsole       = new BookConsole(bookService, scanner);
        UserConsole       userConsole       = new UserConsole(userService);
        LoanConsole       loanConsole       = new LoanConsole(loanService);
        StatisticsConsole statisticsConsole = new StatisticsConsole(statisticsService);

        // ── Lancement ─────────────────────────────────────────────────────────
        MainMenu mainMenu = new MainMenu(authConsole, bookConsole, userConsole, loanConsole, statisticsConsole);
        mainMenu.start();

        scanner.close();
    }
}
