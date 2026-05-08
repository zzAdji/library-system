package com.library.ui.console;

import com.library.model.Role;
import com.library.model.User;
import com.library.model.UserStatus;
import com.library.service.UserService;
import com.library.util.ConsoleUtils;
import com.library.util.PasswordUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserConsole {

    private final UserService userService;

    public UserConsole(UserService userService) {
        this.userService = userService;
    }

    public void showUserMenu() {
        boolean retour = false;
        while (!retour) {
            ConsoleUtils.clearScreen();
            ConsoleUtils.printHeader("Gestion des utilisateurs");
            System.out.println();
            System.out.println(ConsoleUtils.BLUE + "  ╔══════════════════════════════════════╗" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  1. Créer un utilisateur             " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  2. Modifier un utilisateur          " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  3. Désactiver un utilisateur        " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  4. Rechercher un utilisateur        " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  5. Lister tous les utilisateurs     " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ║" + ConsoleUtils.RESET + "  0. Retour                           " + ConsoleUtils.BLUE + "║" + ConsoleUtils.RESET);
            System.out.println(ConsoleUtils.BLUE + "  ╚══════════════════════════════════════╝" + ConsoleUtils.RESET);
            System.out.println();

            String choix = ConsoleUtils.readLine(ConsoleUtils.BOLD + "    Votre choix : " + ConsoleUtils.RESET);

            switch (choix) {
                case "1" -> handleCreateUser();
                case "2" -> handleUpdateUser();
                case "3" -> handleDeactivateUser();
                case "4" -> handleFindUser();
                case "5" -> handleListUsers();
                case "0" -> retour = true;
                default  -> {
                    ConsoleUtils.printError("Option invalide.");
                    ConsoleUtils.pause();
                }
            }
        }
    }

    // ── Créer un utilisateur ──────────────────────────────────────────────────

    private void handleCreateUser() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Créer un utilisateur");
        try {
            String firstName   = ConsoleUtils.readNonEmptyString("  Prénom       : ");
            String lastName    = ConsoleUtils.readNonEmptyString("  Nom          : ");
            String email       = ConsoleUtils.readNonEmptyString("  Email        : ");
            String phone       = ConsoleUtils.readLine("  Téléphone    : ");
            String cardNumber  = ConsoleUtils.readNonEmptyString("  N° de carte  : ");
            String rawPassword = ConsoleUtils.readNonEmptyString("  Mot de passe : ");

            System.out.println("  Rôle — (1) MEMBER  (2) LIBRARIAN  (3) ADMIN");
            String roleChoix = ConsoleUtils.readLine("  Choix rôle   : ");
            Role role = switch (roleChoix) {
                case "2" -> Role.LIBRARIAN;
                case "3" -> Role.ADMIN;
                default  -> Role.MEMBER;
            };

            String id = UUID.randomUUID().toString();
            String hashedPassword = PasswordUtils.hash(rawPassword);

            User user = new User(id, cardNumber, hashedPassword, firstName, lastName,
                    email, phone, role, UserStatus.ACTIVE, LocalDate.now());

            userService.createUser(user);
            System.out.println(ConsoleUtils.GREEN + "\n  ✔ Utilisateur créé avec succès. ID : " + id + ConsoleUtils.RESET);
        } catch (IllegalArgumentException e) {
            ConsoleUtils.printError(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    // ── Modifier un utilisateur ───────────────────────────────────────────────

    private void handleUpdateUser() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Modifier un utilisateur");

        String id = ConsoleUtils.readNonEmptyString("  ID utilisateur : ");
        Optional<User> opt = userService.findById(id);

        if (opt.isEmpty()) {
            ConsoleUtils.printError("Utilisateur introuvable avec l'ID : " + id);
            ConsoleUtils.pause();
            return;
        }

        User u = opt.get();
        System.out.println("  Utilisateur : " + u.getFirstName() + " " + u.getLastName() + " (" + u.getEmail() + ")");
        System.out.println("  (Entrée = conserver la valeur actuelle)");
        System.out.println();

        String firstName = lireOuGarder("  Prénom    [" + u.getFirstName() + "] : ", u.getFirstName());
        String lastName  = lireOuGarder("  Nom       [" + u.getLastName()  + "] : ", u.getLastName());
        String email     = lireOuGarder("  Email     [" + u.getEmail()     + "] : ", u.getEmail());
        String phone     = lireOuGarder("  Téléphone [" + u.getPhone()     + "] : ", u.getPhone());

        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPhone(phone);

        userService.updateUser(u);
        System.out.println(ConsoleUtils.GREEN + "\n  ✔ Utilisateur mis à jour." + ConsoleUtils.RESET);
        ConsoleUtils.pause();
    }

    // ── Désactiver un utilisateur ─────────────────────────────────────────────

    private void handleDeactivateUser() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Désactiver un utilisateur");

        String id = ConsoleUtils.readNonEmptyString("  ID utilisateur : ");
        Optional<User> opt = userService.findById(id);

        if (opt.isEmpty()) {
            ConsoleUtils.printError("Utilisateur introuvable.");
            ConsoleUtils.pause();
            return;
        }

        User u = opt.get();
        System.out.println("  Utilisateur : " + u.getFirstName() + " " + u.getLastName());
        String confirm = ConsoleUtils.readLine("  Confirmer la désactivation ? (o/N) : ");

        if (confirm.equalsIgnoreCase("o")) {
            userService.deactivateUser(id);
            System.out.println(ConsoleUtils.YELLOW + "\n  ✔ Utilisateur désactivé." + ConsoleUtils.RESET);
        } else {
            System.out.println("  ↩ Opération annulée.");
        }
        ConsoleUtils.pause();
    }

    // ── Rechercher un utilisateur ─────────────────────────────────────────────

    private void handleFindUser() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Rechercher un utilisateur");

        String id = ConsoleUtils.readNonEmptyString("  ID utilisateur : ");
        userService.findById(id).ifPresentOrElse(
            this::afficherDetail,
            () -> ConsoleUtils.printError("Aucun utilisateur trouvé avec l'ID : " + id)
        );
        ConsoleUtils.pause();
    }

    // ── Lister tous les utilisateurs ──────────────────────────────────────────

    private void handleListUsers() {
        ConsoleUtils.clearScreen();
        ConsoleUtils.printHeader("Liste des utilisateurs");

        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("  Aucun utilisateur enregistré.");
        } else {
            System.out.println("  " + users.size() + " utilisateur(s) :\n");
            System.out.println(ConsoleUtils.BOLD
                + "  ┌─────────────────────────────────────────────────────────────────────┐"
                + ConsoleUtils.RESET);
            System.out.printf(ConsoleUtils.BOLD + "  │ %-36s %-20s %-10s │%n" + ConsoleUtils.RESET,
                "ID", "Nom complet", "Rôle");
            System.out.println("  ├─────────────────────────────────────────────────────────────────────┤");
            for (User u : users) {
                System.out.printf("  │ %-36s %-20s %-10s │%n",
                    u.getId(),
                    tronquer(u.getFirstName() + " " + u.getLastName(), 20),
                    u.getRole());
            }
            System.out.println("  └─────────────────────────────────────────────────────────────────────┘");
        }
        ConsoleUtils.pause();
    }

    // ── Affichage détail ──────────────────────────────────────────────────────

    private void afficherDetail(User u) {
        System.out.println();
        System.out.println("  ┌─ Détail utilisateur ──────────────────────────");
        System.out.println("  │ ID          : " + u.getId());
        System.out.println("  │ N° carte    : " + u.getCardNumber());
        System.out.println("  │ Prénom      : " + u.getFirstName());
        System.out.println("  │ Nom         : " + u.getLastName());
        System.out.println("  │ Email       : " + u.getEmail());
        System.out.println("  │ Téléphone   : " + (u.getPhone() != null ? u.getPhone() : "-"));
        System.out.println("  │ Rôle        : " + u.getRole());
        System.out.println("  │ Statut      : " + u.getStatus());
        System.out.println("  │ Inscription : " + u.getRegistrationDate());
        System.out.println("  └────────────────────────────────────────────────");
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private String lireOuGarder(String prompt, String actuel) {
        System.out.print(ConsoleUtils.CYAN + prompt + ConsoleUtils.RESET);
        String s = new java.util.Scanner(System.in).nextLine().trim();
        return s.isBlank() ? actuel : s;
    }

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}
