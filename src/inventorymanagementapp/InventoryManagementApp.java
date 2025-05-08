package inventorymanagementapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// Data class for an Inventory Item
class InventoryItem {
    private int itemId;
    private String itemName;
    private String description;
    private int quantity;
    private double price;
    private int supplierId;

    public InventoryItem(int itemId, String itemName, String description, int quantity, double price, int supplierId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.supplierId = supplierId;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public int getSupplierId() { return supplierId; }
}

// Database connection utility
class DBConnection {
    public static Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/inventory_db";
            String user = "root";
            String password = "password"; // your password
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Data Access Object for inventory
class InventoryItemDAO {
    public boolean addInventoryItem(InventoryItem item) {
        String query = "INSERT INTO inventory_items (item_name, description, quantity, price, supplier_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            ps.setInt(5, item.getSupplierId());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getAllInventoryItems() {
        try {
            Connection conn = DBConnection.connect();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery("SELECT * FROM inventory_items");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Main GUI Application
public class InventoryManagementApp {
    private JFrame frame;
    private JPanel panel;
    private JButton addItemButton, viewItemsButton;
    private JTextArea inventoryTextArea;
    private JTextField itemNameField, descriptionField, quantityField, priceField, supplierIdField;

    public InventoryManagementApp() {
        frame = new JFrame("Inventory Management");
        panel = new JPanel(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Buttons
        addItemButton = new JButton("Add Item");
        viewItemsButton = new JButton("View Items");

        // Input form
        JPanel addItemPanel = new JPanel(new GridLayout(6, 2));
        addItemPanel.add(new JLabel("Item Name:")); itemNameField = new JTextField(); addItemPanel.add(itemNameField);
        addItemPanel.add(new JLabel("Description:")); descriptionField = new JTextField(); addItemPanel.add(descriptionField);
        addItemPanel.add(new JLabel("Quantity:")); quantityField = new JTextField(); addItemPanel.add(quantityField);
        addItemPanel.add(new JLabel("Price:")); priceField = new JTextField(); addItemPanel.add(priceField);
        addItemPanel.add(new JLabel("Supplier ID:")); supplierIdField = new JTextField(); addItemPanel.add(supplierIdField);

        // Text area
        inventoryTextArea = new JTextArea(15, 60);
        inventoryTextArea.setLineWrap(true);
        inventoryTextArea.setWrapStyleWord(true);
        inventoryTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(inventoryTextArea);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addItemButton);
        buttonPanel.add(viewItemsButton);

        // Layout
        panel.add(addItemPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);

        // Add Item Action
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String itemName = itemNameField.getText();
                    String description = descriptionField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    int supplierId = Integer.parseInt(supplierIdField.getText());

                    InventoryItem item = new InventoryItem(0, itemName, description, quantity, price, supplierId);
                    InventoryItemDAO dao = new InventoryItemDAO();
                    boolean result = dao.addInventoryItem(item);

                    if (result) {
                        JOptionPane.showMessageDialog(frame, "Item Added Successfully");
                        itemNameField.setText("");
                        descriptionField.setText("");
                        quantityField.setText("");
                        priceField.setText("");
                        supplierIdField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error Adding Item");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers for Quantity, Price, and Supplier ID");
                }
            }
        });

        // View Items Action
        viewItemsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryTextArea.setText("");
                InventoryItemDAO dao = new InventoryItemDAO();
                ResultSet rs = dao.getAllInventoryItems();

                try {
                    if (rs != null) {
                        while (rs.next()) {
                            String item = "ID: " + rs.getInt("item_id") +
                                          ", Name: " + rs.getString("item_name") +
                                          ", Desc: " + rs.getString("description") +
                                          ", Qty: " + rs.getInt("quantity") +
                                          ", Price: " + rs.getDouble("price") +
                                          ", Supplier: " + rs.getInt("supplier_id") + "\n";
                            inventoryTextArea.append(item);
                        }
                        rs.getStatement().getConnection().close(); // close connection after use
                    } else {
                        inventoryTextArea.setText("No data found or error retrieving data.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error retrieving data: " + ex.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementApp());
    }
}
