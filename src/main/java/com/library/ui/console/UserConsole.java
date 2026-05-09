package com.library.ui.console;

import com.library.model.Role;
import com.library.model.User;
import com.library.model.UserStatus;
import com.library.service.UserService;
import com.library.util.ConsoleUtils;
import com.library.util.PasswordUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserConsole {

    private final UserService userService;

    public UserConsole(UserService userService) {
        this.userService = userService;
    }

    public void showUserMenu(Role actorRole) {
        while (true) {
            List<String> options;
            if (actorRole == Role.ADMIN) {
                options = Arrays.asList(
                    "1:Ajouter un utilisateur",
                    "2:Modifier un utilisateur",
                    "3:Désactiver un utilisateur",
                    "4:Réactiver un utilisateur",
                    "5:Rechercher un utilisateur",
                    "6:Liste complète des utilisateurs",
                    " ",
                    "0:Retour au menu principal"
                );
            } else {
                options = Arrays.asList(
                    "1:Ajouter un utilisateur (MEMBER uniquement)",
                    "2:Modifier un utilisateur",
                    "3:Rechercher un utilisateur",
                    "4:Liste complète des utilisateurs",
                    " ",
                    "0:Retour au menu principal"
                );
            }

            String choice = ConsoleUtils.displayMenu("GESTION DES MEMBRES", options);
            if (actorRole == Role.ADMIN) {
                switch (choice) {
                    case "1" -> handleAddUser(actorRole);
                    case "2" -> handleUpdateUser(actorRole);
                    case "3" -> handleDeactivateUser();
                    case "4" -> handleReactivateUser();
                    case "5" -> handleSearchUser();
                    case "6" -> listUsers();
                    case "0" -> { return; }
                    default  -> {
                        ConsoleUtils.printErrorCentered("Choix invalide.");
                        ConsoleUtils.pause();
                    }
                }
            } else {
                switch (choice) {
                    case "1" -> handleAddUser(actorRole);
                    case "2" -> handleUpdateUser(actorRole);
                    case "3" -> handleSearchUser();
                    case "4" -> listUsers();
                    case "0" -> { return; }
                    default  -> {
                        ConsoleUtils.printErrorCentered("Choix invalide.");
                        ConsoleUtils.pause();
                    }
                }
            }
        }
    }

    private void handleAddUser(Role actorRole) {
        ConsoleUtils.printPageHeader("Ajouter un nouvel utilisateur");
        try {
            String cardNumber = ConsoleUtils.readNonEmptyStringCentered("Numéro de carte (ex: MEM-001)");
            
            if (userService.findByCardNumber(cardNumber).isPresent()) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Ce numéro de carte existe déjà.");
                ConsoleUtils.pause();
                return;
            }

            String firstName = ConsoleUtils.readNonEmptyStringCentered("Prénom");
            String lastName  = ConsoleUtils.readNonEmptyStringCentered("Nom");
            String email     = ConsoleUtils.readNonEmptyStringCentered("Email");
            String phone     = ConsoleUtils.readNonEmptyStringCentered("Téléphone");

            Role role;
            if (actorRole == Role.ADMIN) {
                System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "Rôles disponibles : " + ConsoleUtils.CYAN + "1. MEMBER, 2. LIBRARIAN, 3. ADMIN" + ConsoleUtils.RESET);
                role = lireRole();
            } else {
                role = Role.MEMBER;
                System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "Rôle appliqué : " + ConsoleUtils.CYAN + "MEMBER (restriction LIBRARIAN)" + ConsoleUtils.RESET);
            }

            String rawPassword = ConsoleUtils.readNonEmptyStringCentered("Mot de passe");
            String hashedPassword = PasswordUtils.hash(rawPassword);

            User user = new User(
                UUID.randomUUID().toString(),
                cardNumber,
                hashedPassword,
                firstName,
                lastName,
                email,
                phone,
                role,
                UserStatus.ACTIVE,
                LocalDate.now()
            );

            userService.createUser(user);
            System.out.println();
            ConsoleUtils.printSuccessCentered("Utilisateur créé avec succès !");
        } catch (IllegalArgumentException e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void handleUpdateUser(Role actorRole) {
        ConsoleUtils.printPageHeader("Modifier un utilisateur");
        try {
            String cardNumber = ConsoleUtils.readNonEmptyStringCentered("Numéro de carte de l'utilisateur à modifier");
            Optional<User> existant = userService.findByCardNumber(cardNumber);
            
            if (existant.isEmpty()) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Aucun utilisateur trouvé avec ce numéro de carte.");
            } else {
                User actuel = existant.get();
                System.out.println("\n  Modification de : " + ConsoleUtils.CYAN + actuel.getFirstName() + " " + actuel.getLastName() + ConsoleUtils.RESET);
                System.out.println("  (Laissez vide pour conserver la valeur actuelle)\n");

                String firstName = lireChampOuGarder("Prénom", actuel.getFirstName());
                String lastName  = lireChampOuGarder("Nom", actuel.getLastName());
                String email     = lireChampOuGarder("Email", actuel.getEmail());
                String phone     = lireChampOuGarder("Téléphone", actuel.getPhone());
                
                Role role = actuel.getRole();
                if (actorRole == Role.ADMIN) {
                    System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "Rôle actuel : " + ConsoleUtils.CYAN + actuel.getRole() + ConsoleUtils.RESET);
                    String newRoleStr = ConsoleUtils.readLineCentered("Nouveau rôle (1:MEMBER, 2:LIBRARIAN, 3:ADMIN) [" + actuel.getRole() + "]");
                    if (!newRoleStr.isEmpty()) {
                        role = switch (newRoleStr) {
                            case "2" -> Role.LIBRARIAN;
                            case "3" -> Role.ADMIN;
                            default -> Role.MEMBER;
                        };
                    }
                } else {
                    System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "Le rôle n'est pas modifiable par un LIBRARIAN." + ConsoleUtils.RESET);
                }

                actuel.setFirstName(firstName);
                actuel.setLastName(lastName);
                actuel.setEmail(email);
                actuel.setPhone(phone);
                actuel.setRole(role);

                userService.updateUser(actuel);
                System.out.println();
                ConsoleUtils.printSuccessCentered("Utilisateur mis à jour avec succès.");
            }
        } catch (Exception e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void handleDeactivateUser() {
        ConsoleUtils.printPageHeader("Désactiver un utilisateur");
        try {
            String cardNumber = ConsoleUtils.readNonEmptyStringCentered("Numéro de carte de l'utilisateur à désactiver");
            Optional<User> userOpt = userService.findByCardNumber(cardNumber);
            
            if (userOpt.isEmpty()) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Utilisateur introuvable.");
            } else {
                User user = userOpt.get();
                System.out.println("\n  Confirmez-vous la désactivation de : " + ConsoleUtils.BOLD + user.getFirstName() + " " + user.getLastName() + ConsoleUtils.RESET);
                String confirm = ConsoleUtils.readLineCentered("Confirmer (o/N)");
                if (confirm.equalsIgnoreCase("o")) {
                    userService.deactivateUser(user.getId());
                    System.out.println();
                    ConsoleUtils.printSuccessCentered("Le compte a été désactivé.");
                } else {
                    System.out.println();
                    ConsoleUtils.printErrorCentered("Opération annulée.");
                }
            }
        } catch (Exception e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void handleReactivateUser() {
        ConsoleUtils.printPageHeader("Réactiver un utilisateur");
        try {
            String cardNumber = ConsoleUtils.readNonEmptyStringCentered("Numéro de carte de l'utilisateur à réactiver");
            Optional<User> userOpt = userService.findByCardNumber(cardNumber);
            
            if (userOpt.isEmpty()) {
                System.out.println();
                ConsoleUtils.printErrorCentered("Utilisateur introuvable.");
            } else {
                User user = userOpt.get();
                if (user.getStatus() == UserStatus.ACTIVE) {
                    System.out.println();
                    ConsoleUtils.printErrorCentered("Ce compte est déjà actif.");
                } else {
                    System.out.println("\n  Confirmez-vous la réactivation de : " + ConsoleUtils.BOLD + user.getFirstName() + " " + user.getLastName() + ConsoleUtils.RESET);
                    String confirm = ConsoleUtils.readLineCentered("Confirmer (o/N)");
                    if (confirm.equalsIgnoreCase("o")) {
                        userService.reactivateUser(user.getId());
                        System.out.println();
                        ConsoleUtils.printSuccessCentered("Le compte a été réactivé.");
                    } else {
                        System.out.println();
                        ConsoleUtils.printErrorCentered("Opération annulée.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println();
            ConsoleUtils.printErrorCentered(e.getMessage());
        }
        ConsoleUtils.pause();
    }

    private void handleSearchUser() {
        ConsoleUtils.printPageHeader("Recherche d'utilisateur");
        String query = ConsoleUtils.readLineCentered("Email ou Numéro de carte");
        
        Optional<User> byCard = userService.findByCardNumber(query);
        if (byCard.isPresent()) {
            System.out.println();
            afficherDetail(byCard.get());
        } else {
            Optional<User> byEmail = userService.findByEmail(query);
            if (byEmail.isPresent()) {
                System.out.println();
                afficherDetail(byEmail.get());
            } else {
                System.out.println();
                ConsoleUtils.printErrorCentered("Aucun utilisateur trouvé pour : " + query);
            }
        }
        ConsoleUtils.pause();
    }

    private void listUsers() {
        ConsoleUtils.printPageHeader("Liste des utilisateurs");
        List<User> users = userService.getAllUsers();
        
        if (users.isEmpty()) {
            ConsoleUtils.printErrorCentered("Aucun utilisateur enregistré.");
        } else {
            afficherListe(users);
        }
        ConsoleUtils.pause();
    }

    private void afficherListe(List<User> users) {
        System.out.println();
        String headerFormat = ConsoleUtils.MARGIN + ConsoleUtils.BOLD + " %-12s | %-20s | %-20s | %-10s | %s" + ConsoleUtils.RESET;
        String rowFormat = ConsoleUtils.MARGIN + " %-12s | %-20s | %-20s | %-10s | %s";
        
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(85) + ConsoleUtils.RESET);
        System.out.printf(headerFormat + "%n", "N° CARTE", "NOM COMPLET", "EMAIL", "ROLE", "STATUT");
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(85) + ConsoleUtils.RESET);
        
        for (User u : users) {
            String fullName = tronquer(u.getFirstName() + " " + u.getLastName(), 20);
            String status = u.getStatus() == UserStatus.ACTIVE ? ConsoleUtils.GREEN + "Actif" + ConsoleUtils.RESET : ConsoleUtils.RED + "Inactif" + ConsoleUtils.RESET;
            
            System.out.printf(rowFormat + "%n", 
                u.getCardNumber(), fullName, tronquer(u.getEmail(), 20), u.getRole().toString(), status);
        }
        System.out.println(ConsoleUtils.MARGIN + ConsoleUtils.BLUE + " " + "─".repeat(85) + ConsoleUtils.RESET);
    }

    private void afficherDetail(User u) {
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.CYAN + ConsoleUtils.BOLD + "DÉTAILS UTILISATEUR" + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "─".repeat(45) + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  ID unique   : " + ConsoleUtils.WHITE + u.getId() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  N° Carte    : " + ConsoleUtils.WHITE + u.getCardNumber() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Nom         : " + ConsoleUtils.WHITE + u.getFirstName() + " " + u.getLastName() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Email       : " + ConsoleUtils.WHITE + u.getEmail() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Téléphone   : " + ConsoleUtils.WHITE + u.getPhone() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Rôle        : " + ConsoleUtils.WHITE + u.getRole() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Statut      : " + (u.getStatus() == UserStatus.ACTIVE ? ConsoleUtils.GREEN + "Actif" : ConsoleUtils.RED + "Inactif") + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  Inscrit le  : " + ConsoleUtils.WHITE + u.getRegistrationDate() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.MARGIN + "  " + ConsoleUtils.BLUE + "─".repeat(45) + ConsoleUtils.RESET);
    }

    private String lireChampOuGarder(String label, String actuel) {
        String s = ConsoleUtils.readLineCentered(label + " [" + actuel + "]");
        return s.isEmpty() ? actuel : s;
    }

    private Role lireRole() {
        while (true) {
            String choix = ConsoleUtils.readLineCentered("Choix du rôle (1-3)");
            switch (choix) {
                case "1": return Role.MEMBER;
                case "2": return Role.LIBRARIAN;
                case "3": return Role.ADMIN;
                default:
                    ConsoleUtils.printErrorCentered("Veuillez choisir 1, 2 ou 3.");
            }
        }
    }

    private String tronquer(String t, int max) {
        if (t == null) return "";
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }
}
