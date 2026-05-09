package com.library;

import com.library.repository.impl.JsonLoanRepository;
import com.library.repository.impl.JsonBookRepository;
import com.library.repository.impl.JsonUserRepository;

import com.library.service.LoanService;
import com.library.ui.console.LoanConsole;

public class Maine {
    public static void main(String[] args) {

        JsonUserRepository userRepository = new JsonUserRepository();
        JsonBookRepository bookRepository = new JsonBookRepository();
        JsonLoanRepository loanRepository = new JsonLoanRepository();

        LoanService loanService = new LoanService(userRepository, bookRepository, loanRepository);

        LoanConsole loanConsole = new LoanConsole(loanService);

        loanConsole.showLoanMenu();
    }
}