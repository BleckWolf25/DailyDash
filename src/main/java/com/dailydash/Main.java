/**
 * @file Main.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Main entry point for the DailyDash application.
 *
 * @description
 * Detailed explanation of the file's purpose and functionality.
 *
 * @since 07/07/2026
 * @updated 07/07/2026
 */
// ---------- PACKAGE
package com.dailydash;

// ---------- IMPORTS
import com.dailydash.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

// ---------- CLASS: Main
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseUtil.initDatabase(); // Initialize the database at application startup
        // Load the main FXML layout from the resources folder
        URL fxmlLocation = getClass().getResource("/fxml/main.fxml");
        if (fxmlLocation == null) {
            System.err.println("Error: Could not find /fxml/main.fxml");
            System.exit(1);
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Set up the scene and stage (window)
        Scene scene = new Scene(root, 900, 600);
        
        // Load CSS styling right away
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("DailyDash - Task Manager");
        try {
            primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}