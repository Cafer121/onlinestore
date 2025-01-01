package MainCode;

import java.sql.*;
import java.util.Scanner;

public class BuyerService {
    static Scanner scanner = new Scanner(System.in);

    
    public static void viewProducts() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM products";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("Available Products:");
            while (resultSet.next()) {
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");

                System.out.printf("ID: %d | Name: %s | Price: %.2f TL | Stock: %d%n", productId, productName, price, stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void addToCart(int buyerId) {
        System.out.println("Enter the product ID you want to add to your cart:");
        int productId = scanner.nextInt();

        System.out.println("Enter the quantity:");
        int quantity = scanner.nextInt();
        scanner.nextLine(); 

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "INSERT INTO cart (userId, productId, quantity) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, buyerId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            statement.executeUpdate();
            System.out.println("Product added to your cart!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void confirmPurchase(int buyerId) {
        try (Connection connection = DatabaseConnection.connect()) {
          
            String sqlCart = "SELECT cart.productId, cart.quantity, products.price, products.sellerId, products.stock " +
                             "FROM cart " +
                             "JOIN products ON cart.productId = products.id " +
                             "WHERE cart.userId = ?";
            PreparedStatement cartStatement = connection.prepareStatement(sqlCart, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            cartStatement.setInt(1, buyerId);
            ResultSet cartResultSet = cartStatement.executeQuery();

            double totalCost = 0;
            boolean hasInsufficientStock = false;

            while (cartResultSet.next()) {
                int productId = cartResultSet.getInt("productId");
                int quantity = cartResultSet.getInt("quantity");
                double price = cartResultSet.getDouble("price");
                int stock = cartResultSet.getInt("stock");

                if (quantity > stock) {
                    hasInsufficientStock = true;
                    System.out.println("Insufficient stock for product ID: " + productId);
                } else {
                    totalCost += quantity * price;
                }
            }

            if (hasInsufficientStock) {
                System.out.println("Please adjust your cart and try again.");
                return;
            }

            
            String sqlBalance = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement balanceStatement = connection.prepareStatement(sqlBalance);
            balanceStatement.setInt(1, buyerId);
            ResultSet balanceResultSet = balanceStatement.executeQuery();

            if (balanceResultSet.next()) {
                double balance = balanceResultSet.getDouble("balance");

                if (balance >= totalCost) {
                    
                    cartResultSet.beforeFirst(); 
                    while (cartResultSet.next()) {
                        int productId = cartResultSet.getInt("productId");
                        int quantity = cartResultSet.getInt("quantity");
                        double price = cartResultSet.getDouble("price");
                        int sellerId = cartResultSet.getInt("sellerId");
                        int stock = cartResultSet.getInt("stock");

                        
                        String sqlUpdateStock = "UPDATE products SET stock = ? WHERE id = ?";
                        PreparedStatement stockStatement = connection.prepareStatement(sqlUpdateStock);
                        stockStatement.setInt(1, stock - quantity);
                        stockStatement.setInt(2, productId);
                        stockStatement.executeUpdate();

                        
                        String sqlUpdateSellerBalance = "UPDATE users SET balance = balance + ? WHERE id = ?";
                        PreparedStatement sellerBalanceStatement = connection.prepareStatement(sqlUpdateSellerBalance);
                        sellerBalanceStatement.setDouble(1, quantity * price);
                        sellerBalanceStatement.setInt(2, sellerId);
                        sellerBalanceStatement.executeUpdate();
                    }

                    
                    String sqlUpdateBalance = "UPDATE users SET balance = balance - ? WHERE id = ?";
                    PreparedStatement updateBalanceStatement = connection.prepareStatement(sqlUpdateBalance);
                    updateBalanceStatement.setDouble(1, totalCost);
                    updateBalanceStatement.setInt(2, buyerId);
                    updateBalanceStatement.executeUpdate();

                    
                    String sqlClearCart = "DELETE FROM cart WHERE userId = ?";
                    PreparedStatement clearCartStatement = connection.prepareStatement(sqlClearCart);
                    clearCartStatement.setInt(1, buyerId);
                    clearCartStatement.executeUpdate();

                    System.out.println("Purchase confirmed! Thank you for shopping.");
                } else {
                    System.out.println("Your balance is not enough!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void addBalance(int buyerId) {
        System.out.println("Enter the amount you want to add to your balance:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); 

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, amount);
            statement.setInt(2, buyerId);

            statement.executeUpdate();
            System.out.println("Balance added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}