package com.lostandfound;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Welcome to the Lost & Found Portal!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 640, 480);

        stage.setTitle("Lost & Found Admin App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}