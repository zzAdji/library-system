package com.library.config;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import com.library.repository.impl.JsonBookRepository;
import com.library.repository.impl.JsonLoanRepository;
import com.library.repository.impl.JsonUserRepository;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.StatisticsService;
import com.library.service.UserService;

public final class AppConfig {

    private static final UserRepository userRepository = new JsonUserRepository();
    private static final BookRepository bookRepository = new JsonBookRepository();
    private static final LoanRepository loanRepository = new JsonLoanRepository();
    
    private AppConfig() {}

    public static AuthService authService() {
        return new AuthService(userRepository);
    }

    public static BookService bookService() {
        return new BookService(bookRepository);
    }

    public static UserService userService() {
        return new UserService(userRepository);
    }

    public static LoanService loanService() {
        return new LoanService(userRepository, bookRepository, loanRepository);
    }

    public static StatisticsService statisticsService() {
        return new StatisticsService(bookRepository, loanRepository);
    }
}
