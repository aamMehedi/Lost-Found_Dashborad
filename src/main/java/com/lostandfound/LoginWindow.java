package com.lostandfound;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginWindow {

    public void showLoginScreen(Stage primaryStage) {
        primaryStage.setTitle("Lost & Found Portal - Secure Admin Login");

        // Main Container with a modern dark/slate minimalist aesthetic
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1e293b;"); // Sleek dark slate background

        // Title Header
        Label titleLabel = new Label("ADMIN PORTAL");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #f8fafc; -fx-letter-spacing: 2px;");

        Label subtitleLabel = new Label("Please sign in to continue");
        subtitleLabel.setFont(Font.font("System", 12));
        subtitleLabel.setStyle("-fx-text-fill: #94a3b8;");
        
        VBox headerBox = new VBox(5, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);

        // Form Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Username Elements
        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-weight: bold;");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-padding: 8; -fx-background-radius: 4;");
        userField.setPrefWidth(220);

        // Password Elements
        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-weight: bold;");

        // The Trick: Hidden text field and Password field stacked together
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-padding: 8; -fx-background-radius: 4;");
        passField.setPrefWidth(220);

        TextField plainPassField = new TextField();
        plainPassField.setPromptText("Enter password");
        plainPassField.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-padding: 8; -fx-background-radius: 4;");
        plainPassField.setPrefWidth(220);
        plainPassField.setVisible(false); // Hidden by default

        // Stack the two password inputs together
        StackPane passwordStack = new StackPane(plainPassField, passField);

        // Eye Toggle Button
        Button eyeButton = new Button("👁");
        eyeButton.setStyle("-fx-background-color: #475569; -fx-text-fill: #e2e8f0; -fx-cursor: hand; -fx-padding: 6 10; -fx-background-radius: 4;");
        
        // Dynamic Eye Toggle Logic
        eyeButton.setOnAction(e -> {
            if (passField.isVisible()) {
                // Switch to plain text visibility
                plainPassField.setText(passField.getText());
                passField.setVisible(false);
                plainPassField.setVisible(true);
                eyeButton.setText("🙈"); // Closed eye / monkey face
                eyeButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 6 10; -fx-background-radius: 4;");
            } else {
                // Switch back to masked text visibility
                passField.setText(plainPassField.getText());
                plainPassField.setVisible(false);
                passField.setVisible(true);
                eyeButton.setText("👁"); // Open eye
                eyeButton.setStyle("-fx-background-color: #475569; -fx-text-fill: #e2e8f0; -fx-cursor: hand; -fx-padding: 6 10; -fx-background-radius: 4;");
            }
        });

        HBox passwordRow = new HBox(8, passwordStack, eyeButton);
        passwordRow.setAlignment(Pos.CENTER_LEFT);

        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passwordRow, 1, 1);

        // Feedback / Error Message Label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        // Login Button
        Button loginButton = new Button("SIGN IN");
        loginButton.setMaxWidth(Double.MAX_VALUE); // Fill width
        loginButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;");
        
        // Hover effects for the login button to make it feel premium
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;"));

        // Handle login event (syncing both password fields just in case)
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.isVisible() ? passField.getText() : plainPassField.getText();

           if ("admin".equals(username) && "password123".equals(password)) {
                messageLabel.setStyle("-fx-text-fill: #10b981;");
                messageLabel.setText("Success! Loading dashboard...");
                
                // --- ADD THESE LINES TO SWITCH STAGES ---
                DashboardWindow dashboard = new DashboardWindow();
                dashboard.showDashboard(primaryStage);
                // ----------------------------------------
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        root.getChildren().addAll(headerBox, grid, loginButton, messageLabel);

        Scene scene = new Scene(root, 450, 380);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}