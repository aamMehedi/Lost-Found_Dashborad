package com.lostandfound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:lost_found.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "item_name TEXT NOT NULL, "
                + "category TEXT, "
                + "description TEXT, "
                + "status TEXT DEFAULT 'Lost', "
                + "date_reported TEXT, "
                + "image_path TEXT, "
                + "owner_email TEXT" // NEW COLUMN
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            
            // Auto-migration: Adds the column if the DB file already exists without it
            try {
                stmt.execute("ALTER TABLE items ADD COLUMN owner_email TEXT;");
            } catch (SQLException ignored) {
                // Column already exists, safe to ignore
            }

            System.out.println("SQLite Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    // Save a new record to the database (UPDATED WITH ownerEmail)
    public static boolean insertItem(String name, String category, String description, String status, String dateReported, String imagePath, String ownerEmail) {
        String insertSQL = "INSERT INTO items(item_name, category, description, status, date_reported, image_path, owner_email) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setString(3, description);
            pstmt.setString(4, status);
            pstmt.setString(5, dateReported);
            pstmt.setString(6, imagePath);
            pstmt.setString(7, ownerEmail);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    // Read all records from the database (UPDATED WITH owner_email)
    public static List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        String querySQL = "SELECT * FROM items ORDER BY id DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(querySQL)) {
            while (rs.next()) {
                list.add(new Item(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("date_reported"),
                        rs.getString("image_path"),
                        rs.getString("owner_email") // NEW FIELD
                ));
            }
        } catch (SQLException e) {
            System.err.println("Fetch failed: " + e.getMessage());
        }
        return list;
    }

    // Dynamic counter helper to count rows by their status values
    public static int getCountByStatus(String status) {
        String querySQL = "SELECT COUNT(*) FROM items WHERE status = ?";
        if ("TOTAL".equalsIgnoreCase(status)) {
            querySQL = "SELECT COUNT(*) FROM items";
        }
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
            if (!"TOTAL".equalsIgnoreCase(status)) {
                pstmt.setString(1, status);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Count failed: " + e.getMessage());
        }
        return 0;
    }

    // Counts how many items in storage are 30+ days old and unclaimed
    public static int getDustyItemCount() {
        int count = 0;
        List<Item> items = getAllItems();
        for (Item item : items) {
            if (item.isDusty()) {
                count++;
            }
        }
        return count;
    }

    // Update an existing item's status in the database
    public static boolean updateItemStatus(int id, String newStatus) {
        String updateSQL = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Update status failed: " + e.getMessage());
            return false;
        }
    }

    // Permanently delete an item from the database
    public static boolean deleteItem(int id) {
        String deleteSQL = "DELETE FROM items WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Delete item failed: " + e.getMessage());
            return false;
        }
    }

}