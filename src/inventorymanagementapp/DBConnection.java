package inventorymanagementapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection connect() {
        try {
            // Update these connection details
            String url = "jdbc:mysql://localhost:3306/inventory_db";  // Your DB URL
            String user = "root";  // Your MySQL username
            String password = "St3v3n@Kg";  // Your MySQL password
            
            // Register MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            Connection conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("Connection successful!");
            }
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Show user-friendly message
            System.out.println("Error connecting to the database: " + e.getMessage());
            return null;
        }
    }
}
