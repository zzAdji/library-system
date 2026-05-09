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

    /**
     * Affiche l'écran de connexion et retourne true si l'utilisateur est authentifié avec succès,
     * ou false s'il décide de quitter l'application.
     */
    public boolean showLoginScreen() {
        while (true) {
            ConsoleUtils.printPageHeader("Connexion au Système");
            
            System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.WHITE + "Entrez vos identifiants (ou laissez vide pour quitter)." + ConsoleUtils.RESET);
            System.out.println();

            String cardNumber = ConsoleUtils.readLineCentered("Numéro de carte");
            if (cardNumber.isEmpty()) {
                return false; // Quitte
            }

            String password = ConsoleUtils.readLineCentered("Mot de passe");
            if (password.isEmpty()) {
                return false; // Quitte
            }

            Optional<User> userOpt = authService.login(cardNumber, password);
            if (userOpt.isPresent()) {
                System.out.println();
                ConsoleUtils.printSuccessCentered("Bienvenue, " + userOpt.get().getFirstName() + " !");
                ConsoleUtils.pause();
                return true;
            } else {
                System.out.println();
                ConsoleUtils.printErrorCentered("Identifiants incorrects. Veuillez réessayer.");
                ConsoleUtils.pause();
            }
        }
    }
}
