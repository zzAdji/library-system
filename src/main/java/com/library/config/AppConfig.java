package com.library.config;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.StatisticsService;
import com.library.service.UserService;

public final class AppConfig {

    private AppConfig() {
    }

    public static AuthService authService() {
        return new AuthService();
    }

    public static BookService bookService() {
        return new BookService();
    }

    public static UserService userService() {
        return new UserService();
    }

    public static LoanService loanService() {
        return new LoanService();
    }

    public static StatisticsService statisticsService() {
        return new StatisticsService();
    }
}
