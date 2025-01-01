package MainCode;

import java.sql.*;
import java.util.Scanner;
import static MainCode.ErrorLogger.logError;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to our Online Store!");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    registerNewUser();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void login() {
        System.out.println("Enter your username:");
        String name = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                int userId = resultSet.getInt("id");
                if (role.equals("seller")) {
                    sellerMenu(userId);
                } else {
                    buyerMenu(userId);
                }
            } else {
                System.out.println("Invalid credentials!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isUsernameExists(Connection connection, String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            logError("Error checking username existence", e);
        }
        return false;
    }


    

    private static void registerNewUser() {

        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        // التحقق من اسم المستخدم
        try (Connection connection = DatabaseConnection.connect()) {
            if (isUsernameExists(connection, username)) {
                System.out.println("The username is already taken. Please choose another one.");
                return;
            }
            
            String query = "INSERT INTO users (username, password, balance) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setDouble(3, 0.0); // الرصيد الافتراضي
                stmt.executeUpdate();
                System.out.println("Registration successful!");
            }
        } catch (SQLException e) {
            logError("Error registering new user", e);
        }

    }

    private static void sellerMenu(int sellerId) {
        String sellerName = getUserName(sellerId);
        System.out.println("Welcome, " + sellerName + "!");

        while (true) {
            System.out.println("1. Add a product");
            System.out.println("2. View your products");
            System.out.println("3. View your balance");
            System.out.println("4. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    SellerService.addProduct(sellerId);
                    break;
                case 2:
                    SellerService.viewProducts(sellerId);
                    break;
                case 3:
                    try {
                        double balance = getBalanceForUser(sellerId);
                        System.out.println("Your balance is: " + balance + "TL");
                    } catch (SQLException e) {
                        logError("Error retrieving balance for seller", e);
                    }
                    break;
                case 4:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }


    private static void buyerMenu(int buyerId) {
        String buyerName = getUserName(buyerId);
        System.out.println("Welcome, " + buyerName + "!");

        while (true) {
            System.out.println("1. View products");
            System.out.println("2. Add product to cart");
            System.out.println("3. Add balance");
            System.out.println("4. View your balance");
            System.out.println("5. Confirm purchase");
            System.out.println("6. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    BuyerService.viewProducts();
                    break;
                case 2:
                    BuyerService.addToCart(buyerId);
                    break;
                case 3:
                    addBalance(buyerId);
                    break;
                case 4:
                    try {
                        double balance = getBalanceForUser(buyerId);
                        System.out.println("Your balance is: " + balance + "TL");
                    } catch (SQLException e) {
                        logError("Error retrieving balance for buyer", e);
                    }
                    break;
                case 5:
                    BuyerService.confirmPurchase(buyerId);
                    break;
                case 6:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }


    private static String getUserName(int userId) {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT username FROM users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "User";
    }

    private static void addBalance(int buyerId) {
        System.out.println("Enter the amount to add to your balance:");
        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid amount. Please enter a valid number.");
            scanner.nextLine(); 
            return;
        }
        double amount = scanner.nextDouble();
        scanner.nextLine(); 

        if (amount > 0) {
            try (Connection connection = DatabaseConnection.connect()) {
                String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setDouble(1, amount);
                statement.setInt(2, buyerId);
                statement.executeUpdate();

                System.out.println("Balance added successfully!");
            } catch (Exception e) {
                logError("Error adding balance", e);
            }
        } else {
            System.out.println("Amount must be greater than zero.");
        }
    }

    
    public static double getBalanceForUser(int userId) throws SQLException {
        String query = "SELECT balance FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0.0;
    }

}