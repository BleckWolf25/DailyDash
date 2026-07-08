/**
 * @file TaskDetailDialog.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Beautiful read-only Task Detail Dialog with Markdown rendering.
 *
 * @description
 * Displays a task's full Markdown description, metadata badges, and allows transitioning to edit mode.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.controller;

// ---------- IMPORTS
import com.dailydash.model.Task;
import com.dailydash.util.MarkdownRenderer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// ---------- CLASS: TaskDetailDialog
public class TaskDetailDialog {

    public static void show(Task task, Runnable onEditRequested) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Task Details: " + task.getTitle());
        try {
            stage.getIcons().add(new javafx.scene.image.Image(TaskDetailDialog.class.getResourceAsStream("/images/logo.png")));
        } catch (Exception ignored) {}

        BorderPane root = new BorderPane();
        root.getStyleClass().add("detail-dialog-root");

        // Top Header bar
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 20, 16, 20));
        topBar.getStyleClass().add("detail-dialog-top");

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("detail-title");

        Label priorityBadge = new Label(task.getPriority().name());
        priorityBadge.getStyleClass().addAll("tag-badge", "priority-" + task.getPriority().name().toLowerCase());

        Label statusBadge = new Label(task.getStatus().name().replace("_", " "));
        statusBadge.getStyleClass().addAll("tag-badge", "status-" + task.getStatus().name().toLowerCase());

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        topBar.getChildren().addAll(titleLabel, priorityBadge, statusBadge, spacer);

        // Center Content (Markdown & Metadata)
        VBox centerBox = new VBox(16);
        centerBox.setPadding(new Insets(20));

        HBox metadataRow = new HBox(24);
        metadataRow.setAlignment(Pos.CENTER_LEFT);

        Label catLabel = new Label("Category: " + (task.getCategory() != null && !task.getCategory().isEmpty() ? task.getCategory() : "General"));
        catLabel.getStyleClass().add("detail-metadata");

        Label dueLabel = new Label("Due Date: " + (task.getDueDate() != null ? task.getDueDate().toString() : "No Due Date"));
        dueLabel.getStyleClass().add("detail-metadata");

        metadataRow.getChildren().addAll(catLabel, dueLabel);

        Label descHeader = new Label("Description");
        descHeader.getStyleClass().add("detail-section-header");

        VBox mdView = MarkdownRenderer.render(task.getDescription());
        mdView.getStyleClass().add("markdown-container");

        centerBox.getChildren().addAll(metadataRow, descHeader, mdView);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("detail-scroll");

        root.setTop(topBar);
        root.setCenter(scrollPane);

        // Bottom Actions bar
        HBox bottomBar = new HBox(12);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(16, 20, 16, 20));
        bottomBar.getStyleClass().add("detail-dialog-bottom");

        Button editButton = new Button("Edit Task");
        editButton.setGraphic(com.dailydash.util.IconUtil.getIcon("EDIT", 14));
        editButton.getStyleClass().add("primary-btn");
        editButton.setOnAction(e -> {
            stage.close();
            if (onEditRequested != null) {
                onEditRequested.run();
            }
        });

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("btn-secondary");
        closeButton.setOnAction(e -> stage.close());

        bottomBar.getChildren().addAll(editButton, closeButton);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(TaskDetailDialog.class.getResource("/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }
}
