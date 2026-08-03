package com.lostandfound;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:lost_found.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        // Upgraded SQL query including the image_path column
        String createTableSQL = "CREATE TABLE IF NOT EXISTS items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "item_name TEXT NOT NULL, "
                + "category TEXT, "
                + "description TEXT, "
                + "status TEXT DEFAULT 'Lost', "
                + "date_reported TEXT, "
                + "reporter_name TEXT, "
                + "reporter_contact TEXT, "
                + "image_path TEXT" // Stores the path to the uploaded photo
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("SQLite Database initialized successfully with Image support!");
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}