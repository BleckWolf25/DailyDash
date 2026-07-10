/**
 * @file UpdateDialog.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Styled Update Dialog displaying GitHub release notes and download progress.
 */
package com.dailydash.view;

import com.dailydash.service.UpdateService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateDialog {

    public static void show(UpdateService.ReleaseInfo release, boolean isLightTheme) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("DailyDash Update Available");
        stage.setResizable(false);
        try {
            stage.getIcons().add(new javafx.scene.image.Image(UpdateDialog.class.getResourceAsStream("/images/logo.png")));
        } catch (Exception ignored) {}

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("dialog-root");
        root.setStyle("-fx-background-color: -color-surface;");

        if (isLightTheme) {
            root.getStyleClass().add("light-theme");
        }

        // Header
        VBox headerBox = new VBox(6);
        Label titleLabel = new Label("New Update Available: v" + release.version);
        titleLabel.getStyleClass().add("dialog-title");
        titleLabel.setStyle("-fx-text-fill: -color-on-surface; -fx-font-size: 19px; -fx-font-weight: 800;");

        Label subtitleLabel = new Label("Current Version: v" + UpdateService.CURRENT_VERSION + "  •  Repository: " + UpdateService.GITHUB_REPO);
        subtitleLabel.setStyle("-fx-text-fill: -color-primary; -fx-font-size: 13px; -fx-font-weight: 700;");
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Release notes box
        VBox notesBox = new VBox(8);
        Label notesHeader = new Label("Release Notes");
        notesHeader.setStyle("-fx-text-fill: -color-on-surface; -fx-font-size: 13.5px; -fx-font-weight: 700;");

        Label notesContent = new Label(release.releaseNotes);
        notesContent.setWrapText(true);
        notesContent.setStyle("-fx-text-fill: -color-on-surface-variant; -fx-font-size: 13px; -fx-line-spacing: 1.3;");

        ScrollPane scrollPane = new ScrollPane(notesContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(130);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        notesBox.getChildren().addAll(notesHeader, scrollPane);

        // Progress bar (initially hidden)
        VBox progressBox = new VBox(8);
        progressBox.setVisible(false);
        progressBox.setManaged(false);

        Label statusLabel = new Label("Downloading update...");
        statusLabel.setStyle("-fx-text-fill: -color-on-surface; -fx-font-size: 12.5px; -fx-font-weight: 700;");

        ProgressBar progressBar = new ProgressBar(0.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(16);
        progressBox.getChildren().addAll(statusLabel, progressBar);

        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Later");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> stage.close());

        Button updateBtn = new Button("Download & Install");
        updateBtn.getStyleClass().add("btn-primary");
        updateBtn.setOnAction(e -> {
            cancelBtn.setDisable(true);
            updateBtn.setDisable(true);
            progressBox.setVisible(true);
            progressBox.setManaged(true);
            stage.sizeToScene();

            UpdateService.downloadAndInstallAsync(release,
                progress -> {
                    progressBar.setProgress(progress);
                    int pct = (int) (progress * 100);
                    statusLabel.setText("Downloading " + release.assetName + " (" + pct + "%)...");
                },
                () -> {
                    statusLabel.setText("Launching installer...");
                },
                err -> {
                    cancelBtn.setDisable(false);
                    updateBtn.setDisable(false);
                    progressBox.setVisible(false);
                    progressBox.setManaged(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update Failed");
                    alert.setHeaderText("Failed to download update");
                    alert.setContentText(err.getMessage());
                    alert.showAndWait();
                }
            );
        });

        buttonBox.getChildren().addAll(cancelBtn, updateBtn);

        root.getChildren().addAll(headerBox, notesBox, progressBox, buttonBox);

        Scene scene = new Scene(root, 480, 340);
        try {
            scene.getStylesheets().add(UpdateDialog.class.getResource("/css/styles.css").toExternalForm());
        } catch (Exception ignored) {}
        stage.setScene(scene);

        stage.showAndWait();
    }
}
