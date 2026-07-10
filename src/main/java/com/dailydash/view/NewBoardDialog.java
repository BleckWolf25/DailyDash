/**
 * @file NewBoardDialog.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Custom styled dialog for creating a new board.
 *
 * @description
 * Modal dialog for creating a new board with name input validation and theme support.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.view;

// ---------- IMPORTS
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

// ---------- CLASS: NewBoardDialog
public class NewBoardDialog {

    public static Optional<String> show(boolean isLightTheme) {
        final String[] result = new String[1];

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create New Board");
        stage.setResizable(false);
        try {
            stage.getIcons().add(new javafx.scene.image.Image(NewBoardDialog.class.getResourceAsStream("/images/logo.png")));
        } catch (Exception ignored) {}

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("dialog-root");
        root.setStyle("-fx-background-color: #191c1e;");

        if (isLightTheme) {
            root.getStyleClass().add("light-theme");
            root.setStyle("-fx-background-color: #f7f9fb;");
        }

        // Title & Subtitle Header
        VBox headerBox = new VBox(6);
        Label titleLabel = new Label("Create New Board");
        titleLabel.getStyleClass().add("dialog-title");
        titleLabel.setStyle("-fx-text-fill: -color-on-surface; -fx-font-size: 18px; -fx-font-weight: 800;");

        Label subtitleLabel = new Label("Enter a name for your new board workspace.");
        subtitleLabel.setStyle("-fx-text-fill: -color-on-surface-variant; -fx-font-size: 13px;");
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Input field
        VBox fieldBox = new VBox(8);
        Label nameLabel = new Label("Board Name");
        nameLabel.setStyle("-fx-text-fill: -color-on-surface; -fx-font-size: 12.5px; -fx-font-weight: 700;");

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Work, Fitness, Side Project");
        nameField.getStyleClass().add("form-control");
        nameField.setStyle("-fx-padding: 10px 14px; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        fieldBox.getChildren().addAll(nameLabel, nameField);

        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-btn");
        cancelBtn.setOnAction(e -> stage.close());

        Button createBtn = new Button("Create Board");
        createBtn.getStyleClass().add("primary-btn");

        Runnable submitAction = () -> {
            String text = nameField.getText() != null ? nameField.getText().trim() : "";
            if (!text.isEmpty()) {
                result[0] = text;
                stage.close();
            } else {
                nameField.setStyle("-fx-padding: 10px 14px; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-border-color: -color-error;");
            }
        };

        createBtn.setOnAction(e -> submitAction.run());
        nameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submitAction.run();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, createBtn);

        root.getChildren().addAll(headerBox, fieldBox, buttonBox);

        Scene scene = new Scene(root, 440, 230);
        try {
            scene.getStylesheets().add(NewBoardDialog.class.getResource("/css/styles.css").toExternalForm());
        } catch (Exception ignored) {}
        stage.setScene(scene);

        Platform.runLater(nameField::requestFocus);

        stage.showAndWait();
        return Optional.ofNullable(result[0]);
    }
}
