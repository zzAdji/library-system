package com.library.ui.console;

import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner;
    private final AuthConsole authConsole;
    private final BookConsole bookConsole;
    private final UserConsole userConsole;
    private final LoanConsole loanConsole;
    private final StatisticsConsole statisticsConsole;

    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String BLUE   = "\u001B[34m";

    public MainMenu(AuthConsole authConsole,
                    BookConsole bookConsole,
                    UserConsole userConsole,
                    LoanConsole loanConsole,
                    StatisticsConsole statisticsConsole) {
        this.scanner            = new Scanner(System.in);
        this.authConsole        = authConsole;
        this.bookConsole        = bookConsole;
        this.userConsole        = userConsole;
        this.loanConsole        = loanConsole;
        this.statisticsConsole  = statisticsConsole;
    }

    public void start() {
        while (true) {
            clearConsole();
            printMenu();

            System.out.print(BOLD + CYAN + "  Choisissez une option : " + RESET);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> authConsole.showLoginScreen();
                case "2" -> bookConsole.showBookMenu();
                case "3" -> userConsole.showUserMenu();
                case "4" -> loanConsole.showLoanMenu();
                case "5" -> statisticsConsole.showStatisticsMenu();
                case "0" -> {
                    System.out.println(YELLOW + "\n [!] Fermeture du système... Au revoir !" + RESET);
                    return;
                }
                default -> {
                    System.out.println(RED + " ✘ Option invalide. Appuyez sur Entrée pour réessayer." + RESET);
                    scanner.nextLine();
                }
            }
        }
    }

    private void printMenu() {
        String border = BLUE + "║" + RESET;
        System.out.println(BLUE + "╔════════════════════════════════════════════╗" + RESET);
        System.out.println(border + BOLD + "          GESTION DE BIBLIOTHÈQUE           " + RESET + border);
        System.out.println(BLUE + "╠════════════════════════════════════════════╣" + RESET);
        printOption("1", "Authentification",          GREEN);
        printOption("2", "Gestion des livres",        GREEN);
        printOption("3", "Gestion des utilisateurs",  GREEN);
        printOption("4", "Gestion des emprunts",      GREEN);
        printOption("5", "Statistiques",              GREEN);
        System.out.println(border + "                                            " + border);
        printOption("0", "Quitter le programme",      RED);
        System.out.println(BLUE + "╚════════════════════════════════════════════╝" + RESET);
    }

    private void printOption(String key, String label, String color) {
        // Largeur intérieure visible = 44 caractères
        // Format : "  [X] label" puis espaces pour compléter jusqu'à 44
        String content = "  [" + key + "] " + label;
        int visibleLen = content.length();
        int padding    = 44 - visibleLen;
        String spaces  = " ".repeat(Math.max(0, padding));
        System.out.println(
            BLUE + "║" + RESET
            + "  " + color + BOLD + "[" + key + "]" + RESET
            + " " + label
            + spaces
            + BLUE + "║" + RESET
        );
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}