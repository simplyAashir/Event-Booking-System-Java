package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        
        primaryStage.setTitle("Event Booking System - Login");
        primaryStage.setScene(new Scene(root, 700, 700));
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Event Booking System GUI...");
        launch(args);
    }
}