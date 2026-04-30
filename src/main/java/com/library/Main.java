package com.library;

import com.library.config.AppConfig;
import com.library.service.BookService;
import com.library.ui.console.BookConsole;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SYSTÈME DE GESTION DE BIBLIOTHÈQUE   ║");
        System.out.println("╚════════════════════════════════════════╝");

        Scanner scanner = new Scanner(System.in);
        BookService bookService = AppConfig.bookService();
        BookConsole bookConsole = new BookConsole(bookService, scanner);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║           MENU PRINCIPAL             ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Gestion des livres               ║");
            System.out.println("║  2. Gestion des utilisateurs         ║");
            System.out.println("║  3. Gestion des emprunts             ║");
            System.out.println("║  4. Statistiques                     ║");
            System.out.println("║  0. Quitter                          ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> bookConsole.showBookMenu();
                case "2" -> System.out.println("  [Module utilisateurs — en cours]");
                case "3" -> System.out.println("  [Module emprunts — en cours]");
                case "4" -> System.out.println("  [Module statistiques — en cours]");
                case "0" -> { System.out.println("  Au revoir !"); running = false; }
                default  -> System.out.println("  ❌ Choix invalide.");
            }
        }
        scanner.close();
    }
}
