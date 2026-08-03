package com.lostandfound;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Initialize our SQLite backend database structure
        DatabaseHandler.initializeDatabase();

        // 2. Launch the Secure Login Screen window
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.showLoginScreen(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}