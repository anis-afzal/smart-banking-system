import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankingService bankingService = new BankingService();
        Account loggedInAccount = null;

        System.out.println("==========================================");
        System.out.println("  Welcome to Smart Banking Management  ");
        System.out.println("==========================================");


        while (true) {


            if (loggedInAccount == null) {

                System.out.println("\n1. Create New Account");
                System.out.println("2. Login to Existing Account");
                System.out.println("3. Exit System");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.print("Enter Account Holder Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Create a 4-digit PIN: ");
                    String pin = scanner.nextLine();
                    bankingService.createAccount(name, pin);

                } else if (choice == 2) {
                    System.out.print("Enter Account Number: ");
                    int accNum = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter PIN: ");
                    String pin = scanner.nextLine();


                    loggedInAccount = bankingService.login(accNum, pin);

                    if (loggedInAccount != null) {
                        System.out.println("\n>>> Login successful! Welcome, " + loggedInAccount.getAccountHolder() + " <<<");
                    } else {
                        System.out.println("\n[Error] Invalid Account Number or PIN.");
                    }

                } else if (choice == 3) {
                    System.out.println("Thank you for using Smart Banking. Goodbye!");
                    break; //


                } else if (choice == 99) {
                    System.out.print("Enter Admin Password: ");
                    String adminPass = scanner.nextLine();


                    if (adminPass.equals("admin123")) {
                        System.out.println("\n*** ADMIN ACCESS GRANTED ***");
                        boolean adminSession = true;

                        while (adminSession) {
                            System.out.println("\n--- ADMIN DASHBOARD ---");
                            System.out.println("1. View All Accounts & Bank Liquidity");
                            System.out.println("2. Delete Suspicious Account");
                            System.out.println("3. Exit Admin Panel");
                            System.out.print("Admin Choice: ");

                            int adminChoice = scanner.nextInt();

                            if (adminChoice == 1) {
                                bankingService.viewAllAccounts();
                            } else if (adminChoice == 2) {
                                System.out.print("Enter Account Number to PURGE: ");
                                int purgeAcc = scanner.nextInt();
                                bankingService.deleteAccount(purgeAcc);
                            } else if (adminChoice == 3) {
                                adminSession = false;
                                System.out.println("Locking Admin Panel...");
                            } else {
                                System.out.println("Invalid admin command.");
                            }
                        }
                    } else {
                        System.out.println("ACCESS DENIED. Intruder attempt logged.");
                    }
                    // --- END HIDDEN ADMIN PORTAL ---

                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }


            else {
                System.out.println("\n--- Account Dashboard ---");
                System.out.println("Current Balance: $" + loggedInAccount.getBalance());
                System.out.println("1. Deposit Funds");
                System.out.println("2. Withdraw Funds");
                System.out.println("3. Transfer Funds");
                System.out.println("4. View Transaction History");
                System.out.println("5. Download Bank Statement (TXT)");
                System.out.println("6. Logout");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();

                if (choice == 1) {
                    System.out.print("Enter deposit amount: $");
                    double amount = scanner.nextDouble();
                    bankingService.deposit(loggedInAccount, amount);

                } else if (choice == 2) {
                    System.out.print("Enter withdrawal amount: $");
                    double amount = scanner.nextDouble();
                    bankingService.withdraw(loggedInAccount, amount);

                } else if (choice == 3) {
                    // Transfer Logic
                    System.out.print("Enter Receiver's Account Number: ");
                    int receiverAccNum = scanner.nextInt();
                    System.out.print("Enter amount to transfer: $");
                    double amount = scanner.nextDouble();
                    bankingService.transfer(loggedInAccount, receiverAccNum, amount);

                } else if (choice == 4) {
                    bankingService.printTransactionHistory(loggedInAccount.getAccountNumber());

                }
                else if (choice == 5) {

                    System.out.println("Generating your official statement...");
                    bankingService.exportBankStatement(loggedInAccount);
                }

                else if (choice == 6) {
                    loggedInAccount = null; // Wipes the session
                    System.out.println("Logged out successfully.");

                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
        }
        scanner.close();
    }
}