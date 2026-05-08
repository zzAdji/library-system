package com.library.ui.console;

import com.library.model.User;
import com.library.service.AuthService;
import com.library.util.ConsoleUtils;

import java.util.Optional;

public class AuthConsole {

    private final AuthService authService;

    public AuthConsole(AuthService authService) {
        this.authService = authService;
    }

    public void showLoginScreen() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Authentification");

        System.out.println(ConsoleUtils.CYAN + "  Connectez-vous avec votre numéro de carte et mot de passe." + ConsoleUtils.RESET);
        System.out.println();

        String cardNumber = ConsoleUtils.readNonEmptyString("  Numéro de carte : ");
        String password   = ConsoleUtils.readNonEmptyString("  Mot de passe    : ");

        Optional<User> result = authService.login(cardNumber, password);

        if (result.isPresent()) {
            User user = result.get();
            System.out.println();
            System.out.println(ConsoleUtils.GREEN + ConsoleUtils.BOLD
                + "  ✔ Connexion réussie ! Bienvenue, "
                + user.getFirstName() + " " + user.getLastName()
                + " [" + user.getRole() + "]"
                + ConsoleUtils.RESET);
        } else {
            System.out.println();
            ConsoleUtils.printError("Numéro de carte ou mot de passe incorrect.");
        }

        ConsoleUtils.pause();
    }

    public void showLogoutScreen() {
        authService.logout();
        System.out.println(ConsoleUtils.YELLOW + "  Vous avez été déconnecté." + ConsoleUtils.RESET);
        ConsoleUtils.pause();
    }

    public Optional<User> getCurrentUser() {
        return authService.getCurrentUser();
    }
}
