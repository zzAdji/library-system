package com.library.ui.console;

import com.library.service.LoanService;
import java.util.Scanner;

public class LoanConsole {

    private LoanService loanService;
    private Scanner scanner;

    public LoanConsole(LoanService loanService) {
        this.loanService = loanService;
        this.scanner = new Scanner(System.in);
    }

    public void showLoanMenu() {
        int choice;

        do {
            System.out.println("\n=== MENU EMPRUNTS ===");
            System.out.println("1. Emprunter un livre");
            System.out.println("2. Retourner un livre");
            System.out.println("3. Prolonger un emprunt");
            System.out.println("4. Voir emprunts actifs");
            System.out.println("0. Retour");

            System.out.print("Choix : ");

            while (!scanner.hasNextInt()) {
                System.out.println("Entrée invalide !");
                scanner.next();
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleBorrowBook();
                    break;
                case 2:
                    handleReturnBook();
                    break;
                case 3:
                    handleExtendLoan();
                    break;
                case 4:
                    listActiveLoans();
                    break;
                case 0:
                    System.out.println("Retour...");
                    break;
                default:
                    System.out.println("Choix invalide");
            }

        } while (choice != 0);
    }

    private void handleBorrowBook() {
        System.out.print("ID utilisateur : ");
        String userId = scanner.nextLine();

        System.out.print("ISBN livre : ");
        String isbn = scanner.nextLine();

        loanService.borrowBook(userId, isbn);

        System.out.println("Emprunt effectué !");
    }

    private void handleReturnBook() {
        System.out.print("ID emprunt : ");
        String loanId = scanner.nextLine();

        loanService.returnBook(loanId);

        System.out.println("Livre retourné !");
    }

    private void handleExtendLoan() {
        System.out.print("ID emprunt : ");
        String loanId = scanner.nextLine();

        System.out.print("Nombre de jours supplémentaires : ");
        int days = scanner.nextInt();
        scanner.nextLine();

        loanService.extendLoan(loanId, days);

        System.out.println("Emprunt prolongé !");
    }

    private void listActiveLoans() {
        System.out.println("Liste des emprunts actifs :");
        loanService.getActiveLoans().forEach(System.out::println);
    }
}