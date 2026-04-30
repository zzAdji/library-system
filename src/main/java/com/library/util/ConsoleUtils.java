package com.library.util;

import java.util.Scanner;

public class ConsoleUtils {

    // Un seul Scanner pour toute l'application pour éviter les fuites de mémoire
    private static final Scanner scanner = new Scanner(System.in);

    // Codes couleurs rendus PUBLIC pour être utilisés dans toute l'application
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    private ConsoleUtils() {
        throw new UnsupportedOperationException("Cette classe ne peut pas être instanciée");
    }

    
    public static void printColored(String text, String colorCode) {
        System.out.println(colorCode + text + RESET);
    }

    /**
     * Affiche un message et lit une ligne de texte.
     */
    public static String readLine(String prompt) {
        System.out.print(CYAN + prompt + RESET);
        return scanner.nextLine().trim();
    }

    /**
     * Boucle jusqu'à ce que l'utilisateur entre une chaîne non vide.
     */
    public static String readNonEmptyString(String prompt) {
        String input;
        do {
            input = readLine(prompt);
            if (input.isEmpty()) {
                printError("Ce champ ne peut pas être vide.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Lit un entier en gérant les erreurs de saisie (ex: lettres à la place de chiffres).
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                String input = readLine(prompt);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Veuillez entrer un nombre valide.");
            }
        }
    }

    /**
     * Affiche un bel en-tête pour les sous-menus.
     */
    public static void printHeader(String title) {
        System.out.println("\n" + BLUE + "=== " + BOLD + title.toUpperCase() + RESET + BLUE + " ===" + RESET);
    }

    /**
     * Affiche un message d'erreur en rouge.
     */
    public static void printError(String message) {
        System.out.println(RED + "✘ Erreur : " + message + RESET);
    }

    /**
     * Met le programme en pause jusqu'à ce que l'utilisateur appuie sur Entrée.
     */
    public static void pause() {
        System.out.println("\n" + CYAN + "Appuyez sur Entrée pour continuer..." + RESET);
        scanner.nextLine();
    }

    /**
     * Nettoie l'écran de la console.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}