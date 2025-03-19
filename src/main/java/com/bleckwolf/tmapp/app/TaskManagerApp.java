package com.bleckwolf.tmapp.app;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application entry point for the Task Manager.
 * Handles JavaFX initialization and primary stage configuration.
 */
public class TaskManagerApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagerApp.class);
    
    // Configuration constants
    private static final String FXML_MAIN_VIEW = "/fxml/main-view.fxml";
    private static final String APP_TITLE = "Task Manager";
    private static final int INITIAL_WIDTH = 800;
    private static final int INITIAL_HEIGHT = 600;
    private static final int MIN_WIDTH = 650;
    private static final int MIN_HEIGHT = 500;

    @Override
    public void start(Stage primaryStage) {
        try {
            initializePrimaryStage(primaryStage, loadMainView());
            logger.info("Application started successfully");
        } catch (IOException e) {
            handleInitializationError(e);
        }
    }

    /**
     * Loads the main view FXML file.
     * @return Parent node containing the main view
     * @throws IOException if the FXML file cannot be loaded
     */
    private Parent loadMainView() throws IOException {
        logger.debug("Loading main view from {}", FXML_MAIN_VIEW);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bleckwolf/tmapp/fxml/main-view.fxml"));
        return loader.load();
    }

    /**
     * Configures the primary application stage.
     * @param stage The primary stage to configure
     * @param root The root node of the scene graph
     */
    private void initializePrimaryStage(Stage stage, Parent root) {
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        
        stage.show();
        logger.debug("Primary stage initialized with dimensions {}x{}", INITIAL_WIDTH, INITIAL_HEIGHT);
    }

    /**
     * Handles application initialization errors.
     * @param error The exception that occurred during initialization
     */
    private void handleInitializationError(Exception error) {
        logger.error("Critical error during application initialization: {}", error.getMessage(), error);
        System.exit(1);
    }

    /**
     * Application entry point.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        logger.info("Launching Task Manager application");
        launch(args);
        logger.info("Application shutdown completed");
    }
}