package com.library.util;

import java.util.Scanner;
import java.util.List;

public class ConsoleUtils {

    private static final Scanner scanner = new Scanner(System.in);

    // Paramètres d'affichage
    public static final int TERMINAL_WIDTH = 100;
    public static final int BOX_WIDTH = 60;
    public static final String MARGIN = " ".repeat(Math.max(0, (TERMINAL_WIDTH - BOX_WIDTH) / 2));

    // Codes couleurs ANSI
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    private ConsoleUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ── TEMPLATES UI GÉNÉRIQUES ─────────────────────────────────────────────

    /**
     * Affiche un menu complet, centré et stylisé, et renvoie le choix de l'utilisateur.
     */
    public static String displayMenu(String title, List<String> options) {
        clearScreen();
        System.out.println("\n".repeat(3)); // Centrage vertical approximatif
        
        // Bordure haute
        System.out.println(MARGIN + BLUE + "╭" + "─".repeat(BOX_WIDTH - 2) + "╮" + RESET);
        
        // Titre centré
        printCenteredLine(BOLD + WHITE + title.toUpperCase() + RESET);
        
        // Séparateur
        System.out.println(MARGIN + BLUE + "├" + "─".repeat(BOX_WIDTH - 2) + "┤" + RESET);
        
        // Ligne vide
        System.out.println(MARGIN + BLUE + "│" + " ".repeat(BOX_WIDTH - 2) + "│" + RESET);

        // Options
        for (String option : options) {
            if (option.trim().isEmpty()) {
                System.out.println(MARGIN + BLUE + "│" + " ".repeat(BOX_WIDTH - 2) + "│" + RESET);
            } else {
                printMenuOption(option);
            }
        }

        // Ligne vide
        System.out.println(MARGIN + BLUE + "│" + " ".repeat(BOX_WIDTH - 2) + "│" + RESET);
        
        // Bordure basse
        System.out.println(MARGIN + BLUE + "╰" + "─".repeat(BOX_WIDTH - 2) + "╯" + RESET);
        System.out.println();

        return readLineCentered("Votre choix");
    }

    /**
     * Affiche un en-tête simple et centré
     */
    public static void printPageHeader(String title) {
        clearScreen();
        System.out.println();
        String header = "❖  " + title.toUpperCase() + "  ❖";
        System.out.println(MARGIN + CYAN + BOLD + centerText(header, BOX_WIDTH) + RESET);
        System.out.println(MARGIN + BLUE + "─".repeat(BOX_WIDTH) + RESET);
        System.out.println();
    }

    // ── METHODES UTILITAIRES INTERNES POUR LE STYLE ─────────────────────────

    private static void printCenteredLine(String content) {
        // Calcule l'espace réel sans les codes ANSI
        String cleanContent = content.replaceAll("\u001B\\[[;\\d]*m", "");
        int paddingLength = (BOX_WIDTH - 2 - cleanContent.length()) / 2;
        String paddingLeft = " ".repeat(Math.max(0, paddingLength));
        String paddingRight = " ".repeat(Math.max(0, BOX_WIDTH - 2 - cleanContent.length() - paddingLength));
        
        System.out.println(MARGIN + BLUE + "│" + RESET + paddingLeft + content + paddingRight + BLUE + "│" + RESET);
    }

    private static void printMenuOption(String option) {
        String[] parts = option.split(":", 2);
        if (parts.length == 2) {
            String key = parts[0].trim();
            String label = parts[1].trim();
            
            // Format de la ligne: "    [key] label"
            String keyStr = CYAN + BOLD + "[" + key + "]" + RESET;
            String textToDisplay = "    " + keyStr + "  " + label;
            
            // Calcul de la longueur visible pour fermer la bordure
            int visibleLen = 4 + 2 + key.length() + 2 + label.length();
            String padding = " ".repeat(Math.max(0, BOX_WIDTH - 2 - visibleLen));
            
            System.out.println(MARGIN + BLUE + "│" + RESET + textToDisplay + padding + BLUE + "│" + RESET);
        } else {
            String textToDisplay = "    " + option;
            String padding = " ".repeat(Math.max(0, BOX_WIDTH - 2 - textToDisplay.length()));
            System.out.println(MARGIN + BLUE + "│" + RESET + textToDisplay + padding + BLUE + "│" + RESET);
        }
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text + " ".repeat(width - text.length() - pad);
    }

    // ── SAISIES ET INTERACTIONS ─────────────────────────────────────────────

    public static String readLineCentered(String prompt) {
        System.out.print(MARGIN + BOLD + CYAN + " ➤ " + RESET + prompt + " : ");
        return scanner.nextLine().trim();
    }

    public static String readNonEmptyStringCentered(String prompt) {
        while (true) {
            String input = readLineCentered(prompt);
            if (!input.isEmpty()) return input;
            printErrorCentered("Ce champ est obligatoire.");
        }
    }

    public static int readIntCentered(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLineCentered(prompt));
            } catch (NumberFormatException e) {
                printErrorCentered("Entier invalide.");
            }
        }
    }

    // ── NOTIFICATIONS ───────────────────────────────────────────────────────

    public static void printSuccessCentered(String message) {
        System.out.println(MARGIN + GREEN + " ✓ " + message + RESET);
    }

    public static void printErrorCentered(String message) {
        System.out.println(MARGIN + RED + " ✗ " + message + RESET);
    }

    public static void pause() {
        System.out.println("\n" + MARGIN + WHITE + "(Appuyez sur Entrée pour continuer)" + RESET);
        scanner.nextLine();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}