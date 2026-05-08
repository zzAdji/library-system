package com.library;

import com.library.repository.impl.JsonBookRepository;
import com.library.repository.impl.JsonUserRepository;
import com.library.repository.impl.JsonLoanRepository;
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

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Repositories
        JsonUserRepository userRepository = new JsonUserRepository();
        JsonBookRepository bookRepository = new JsonBookRepository();
        JsonLoanRepository loanRepository = new JsonLoanRepository();

        // Services
        AuthService      authService      = new AuthService(userRepository);
        BookService      bookService      = new BookService(bookRepository);
        UserService      userService      = new UserService(userRepository);
        LoanService      loanService      = new LoanService(userRepository, bookRepository, loanRepository);
        StatisticsService statisticsService = new StatisticsService(bookRepository, loanRepository);

        // Consoles UI
        AuthConsole       authConsole       = new AuthConsole(authService, scanner);
        BookConsole       bookConsole       = new BookConsole(bookService, scanner);
        UserConsole       userConsole       = new UserConsole(userService, scanner);
        LoanConsole       loanConsole       = new LoanConsole(loanService);
        StatisticsConsole statisticsConsole = new StatisticsConsole(statisticsService);

        // Lancement
        MainMenu mainMenu = new MainMenu(authConsole, bookConsole, userConsole, loanConsole, statisticsConsole);
        mainMenu.start();
    }
}