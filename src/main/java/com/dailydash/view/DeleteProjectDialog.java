/**
 * @file DeleteProjectDialog.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary One-phrase summary.
 *
 * @description
 * Detailed explanation of the file's purpose and functionality.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.view;

// ---------- IMPORTS
import com.dailydash.model.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

// ---------- CLASS: DeleteProjectDialog
public class DeleteProjectDialog {

    public static boolean show(Project project, boolean isLightTheme) {
        AtomicBoolean confirmed = new AtomicBoolean(false);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Delete Project");
        stage.setResizable(false);
        try {
            stage.getIcons().add(new javafx.scene.image.Image(DeleteProjectDialog.class.getResourceAsStream("/images/logo.png")));
        } catch (Exception ignored) {}

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("dialog-root");
        root.setStyle("-fx-background-color: #191c1e;");

        if (isLightTheme) {
            root.getStyleClass().add("light-theme");
            root.setStyle("-fx-background-color: #f7f9fb;");
        }

        // Title and Warning Header
        VBox headerBox = new VBox(6);
        Label titleLabel = new Label("Delete Project?");
        titleLabel.getStyleClass().add("dialog-title");
        titleLabel.setStyle("-fx-text-fill: -color-error; -fx-font-size: 18px; -fx-font-weight: 800;");

        Label warningLabel = new Label("This action cannot be undone.");
        warningLabel.setStyle("-fx-text-fill: -color-on-surface-variant; -fx-font-size: 13px; -fx-font-weight: 700;");
        headerBox.getChildren().addAll(titleLabel, warningLabel);

        // Body message
        Label contentLabel = new Label("Are you sure you want to delete '" + project.getName() + "'? All tasks and automations associated with this project will be permanently removed.");
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: -color-on-surface; -fx-font-size: 13.5px; -fx-line-spacing: 1.4;");

        // Action Buttons Row
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-btn");
        cancelBtn.setOnAction(e -> stage.close());

        Button deleteBtn = new Button("Delete Project");
        deleteBtn.getStyleClass().add("btn-danger");
        deleteBtn.setOnAction(e -> {
            confirmed.set(true);
            stage.close();
        });

        buttonBox.getChildren().addAll(cancelBtn, deleteBtn);

        root.getChildren().addAll(headerBox, contentLabel, buttonBox);

        Scene scene = new Scene(root, 440, 220);
        scene.getStylesheets().add(DeleteProjectDialog.class.getResource("/css/styles.css").toExternalForm());
        stage.setScene(scene);

        stage.showAndWait();
        return confirmed.get();
    }
}
