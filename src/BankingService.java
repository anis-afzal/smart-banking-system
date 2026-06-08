import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankingService {

    //  Create a New Account
    public void createAccount(String name, String pin) {
        String query = "INSERT INTO accounts (account_holder, pin) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
            System.out.println("Account created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Login
    public Account login(int accountNumber, String pin) {
        String query = "SELECT * FROM accounts WHERE account_number = ? AND pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, accountNumber);
            pstmt.setString(2, pin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("account_number"),
                        rs.getString("account_holder"),
                        rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    //  Deposit Money
    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        String updateBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logTxQuery = "INSERT INTO transactions (account_number, tx_type, amount) VALUES (?, 'DEPOSIT', ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceQuery);
                 PreparedStatement logStmt = conn.prepareStatement(logTxQuery)) {

                //  Add money to balance
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, account.getAccountNumber());
                updateStmt.executeUpdate();

                logStmt.setInt(1, account.getAccountNumber());
                logStmt.setDouble(2, amount);
                logStmt.executeUpdate();

                conn.commit();
                account.setBalance(account.getBalance() + amount); // Update local Java object
                System.out.println("Successfully deposited: $" + amount);

            } catch (SQLException e) {

                conn.rollback();
                System.out.println("Transaction failed. Rolling back.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- 4. Withdraw Money ---
    public void withdraw(Account account, double amount) {
        if (amount <= 0 || amount > account.getBalance()) {
            System.out.println("Invalid amount or insufficient funds.");
            return;
        }

        String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String logTxQuery = "INSERT INTO transactions (account_number, tx_type, amount) VALUES (?, 'WITHDRAWAL', ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceQuery);
                 PreparedStatement logStmt = conn.prepareStatement(logTxQuery)) {

                // Update Balance
                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, account.getAccountNumber());
                updateStmt.executeUpdate();

                // Log Transaction
                logStmt.setInt(1, account.getAccountNumber());
                logStmt.setDouble(2, amount);
                logStmt.executeUpdate();

                conn.commit();
                account.setBalance(account.getBalance() - amount);
                System.out.println("Successfully withdrew: $" + amount);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed. Rolling back.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // --- 5. View Transaction History ---
    public void printTransactionHistory(int accountNumber) {
        // Query to get history, ordered by the newest transaction first
        String query = "SELECT tx_type, amount, tx_date FROM transactions WHERE account_number = ? ORDER BY tx_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery(); // executeQuery is used when we EXPECT data back

            System.out.println("\n========== TRANSACTION HISTORY ==========");
            System.out.printf("%-15s | %-12s | %s%n", "Type", "Amount", "Date");
            System.out.println("-----------------------------------------");

            boolean hasHistory = false;


            while (rs.next()) {
                hasHistory = true;
                String type = rs.getString("tx_type");
                double amount = rs.getDouble("amount");
                String date = rs.getTimestamp("tx_date").toString();

                // Formats the output into neat columns
                System.out.printf("%-15s | $%-11.2f | %s%n", type, amount, date);
            }

            if (!hasHistory) {
                System.out.println("No transactions found for this account.");
            }
            System.out.println("=========================================");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transfer(Account sender, int receiverAccNum, double amount) {

        if (amount <= 0 || amount > sender.getBalance()) {
            System.out.println("Invalid amount or insufficient funds.");
            return;
        }
        if (sender.getAccountNumber() == receiverAccNum) {
            System.out.println("You cannot transfer money to your own account.");
            return;
        }

        // SQL Queries needed for the transfer
        String checkReceiverQuery = "SELECT * FROM accounts WHERE account_number = ?";
        String updateSenderQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String updateReceiverQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logTxQuery = "INSERT INTO transactions (account_number, tx_type, amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {


            try (PreparedStatement checkStmt = conn.prepareStatement(checkReceiverQuery)) {
                checkStmt.setInt(1, receiverAccNum);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("Transfer failed: Receiver account number not found.");
                    return;
                }
            }


            conn.setAutoCommit(false);

            try (PreparedStatement senderStmt = conn.prepareStatement(updateSenderQuery);
                 PreparedStatement receiverStmt = conn.prepareStatement(updateReceiverQuery);
                 PreparedStatement logStmt = conn.prepareStatement(logTxQuery)) {


                senderStmt.setDouble(1, amount);
                senderStmt.setInt(2, sender.getAccountNumber());
                senderStmt.executeUpdate();


                receiverStmt.setDouble(1, amount);
                receiverStmt.setInt(2, receiverAccNum);
                receiverStmt.executeUpdate();


                logStmt.setInt(1, sender.getAccountNumber());
                logStmt.setString(2, "TRANSFER_OUT");
                logStmt.setDouble(3, amount);
                logStmt.executeUpdate();


                logStmt.setInt(1, receiverAccNum);
                logStmt.setString(2, "TRANSFER_IN");
                logStmt.setDouble(3, amount);
                logStmt.executeUpdate();


                conn.commit();


                sender.setBalance(sender.getBalance() - amount);
                System.out.println("Successfully transferred $" + amount + " to Account #" + receiverAccNum);

            } catch (SQLException e) {

                conn.rollback();
                System.out.println("Transfer failed. Transaction rolled back safely.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // --- 7. ADMIN: View All Accounts & Bank Liquidity ---
    public void viewAllAccounts() {
        String query = "SELECT * FROM accounts";
        double totalLiquidity = 0; // The sum of all money in the bank


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n================ ADMIN: ALL ACCOUNTS ================");
            System.out.printf("%-10s | %-20s | %s%n", "Acc Number", "Account Holder", "Balance");
            System.out.println("-----------------------------------------------------");

            while (rs.next()) {
                int accNum = rs.getInt("account_number");
                String name = rs.getString("account_holder");
                double balance = rs.getDouble("balance");

                totalLiquidity += balance; // Add to the bank's total vault

                System.out.printf("%-10d | %-20s | $%.2f%n", accNum, name, balance);
            }
            System.out.println("-----------------------------------------------------");
            System.out.printf("TOTAL BANK LIQUIDITY: $%.2f%n", totalLiquidity);
            System.out.println("=====================================================\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  ADMIN: Delete Suspicious Account ---
    public void deleteAccount(int accountNumber) {

        String deleteTxQuery = "DELETE FROM transactions WHERE account_number = ?";
        String deleteAccQuery = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement txStmt = conn.prepareStatement(deleteTxQuery);
                 PreparedStatement accStmt = conn.prepareStatement(deleteAccQuery)) {


                txStmt.setInt(1, accountNumber);
                txStmt.executeUpdate();


                accStmt.setInt(1, accountNumber);
                int rowsAffected = accStmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    System.out.println("SUCCESS: Account #" + accountNumber + " and its history have been permanently purged.");
                } else {
                    System.out.println("Account not found.");
                    conn.rollback();
                }

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Failed to delete account. Rolled back.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportBankStatement(Account account) {
        String query = "SELECT tx_type, amount, tx_date FROM transactions WHERE account_number = ? ORDER BY tx_date DESC";


        String filename = "BankStatement_" + account.getAccountNumber() + ".txt";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {

            pstmt.setInt(1, account.getAccountNumber());
            ResultSet rs = pstmt.executeQuery();


            writer.println("=========================================");
            writer.println("             SMART BANKING               ");
            writer.println("          OFFICIAL BANK STATEMENT        ");
            writer.println("=========================================");
            writer.println("Account Holder: " + account.getAccountHolder());
            writer.println("Account Number: " + account.getAccountNumber());
            writer.println("Current Balance: $" + String.format("%.2f", account.getBalance()));
            writer.println("-----------------------------------------");
            writer.printf("%-15s | %-12s | %s%n", "Type", "Amount", "Date");
            writer.println("-----------------------------------------");

            boolean hasHistory = false;
            while (rs.next()) {
                hasHistory = true;
                String type = rs.getString("tx_type");
                double amount = rs.getDouble("amount");
                String date = rs.getTimestamp("tx_date").toString();


                writer.printf("%-15s | $%-11.2f | %s%n", type, amount, date);
            }

            if (!hasHistory) {
                writer.println("No transactions found for this account.");
            }
            writer.println("=========================================");
            writer.println("          End of Statement Document      ");
            // ---------------------------

            System.out.println("\n>>> SUCCESS! Your bank statement has been downloaded. <<<");
            System.out.println("File saved as: " + filename);
            System.out.println("Check your IntelliJ project folder to open it.");

        } catch (SQLException e) {
            System.out.println("Database error while generating statement.");
            e.printStackTrace();
        } catch (java.io.IOException e) {
            System.out.println("File writing error. Could not save statement.");
            e.printStackTrace();
        }
    }
}