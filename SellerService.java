package MainCode;

import java.sql.*;
import java.util.Scanner;

public class SellerService {
    static Scanner scanner = new Scanner(System.in);

    
    public static void addProduct(int sellerId) {
        System.out.println("Enter product name:");
        String productName = scanner.nextLine();

        System.out.println("Enter product price:");
        double price = scanner.nextDouble();

        System.out.println("Enter product stock:");
        int stock = scanner.nextInt();
        scanner.nextLine(); 

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "INSERT INTO products (name, price, stock, sellerId) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, productName);
            statement.setDouble(2, price);
            statement.setInt(3, stock);
            statement.setInt(4, sellerId);

            statement.executeUpdate();
            System.out.println("Product added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void viewProducts(int sellerId) {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM products WHERE sellerId = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, sellerId);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Your Products:");
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
}