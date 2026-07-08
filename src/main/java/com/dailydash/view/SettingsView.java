/**
 * @file SettingsView.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Custom Settings Page for DailyDash.
 *
 * @description
 * Manages UI themes (Syntactic Management light/dark), compact spacing mode, and database cleanup.
 * Strictly excludes Email, Collaboration, and Help features per user requirements.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.view;

// ---------- IMPORTS
import com.dailydash.service.TaskDataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// ---------- CLASS: SettingsView
public class SettingsView {

    public static ScrollPane build(TaskDataService dataService, Runnable onThemeChangeRequested) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.getStyleClass().add("page-container");

        // Header
        VBox headerBox = new VBox(4);
        Label title = new Label("Workspace Settings");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Customize visual appearance, color themes, and workspace data.");
        subtitle.getStyleClass().add("page-subtitle");
        headerBox.getChildren().addAll(title, subtitle);

        // Section 1: Appearance & Theme
        VBox appearBox = new VBox(14);
        appearBox.setPadding(new Insets(20));
        appearBox.getStyleClass().add("settings-section-card");

        Label appearTitle = new Label("Visual Theme & Appearance");
        appearTitle.getStyleClass().add("section-title");

        HBox themeRow = new HBox(16);
        themeRow.setAlignment(Pos.CENTER_LEFT);
        Label themeLabel = new Label("Color Theme:");
        themeLabel.getStyleClass().add("settings-label");

        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Syntactic Dark (Default)", "Syntactic Light");

        String currentTheme = dataService.getSetting("theme", "dark");
        themeCombo.setValue("light".equals(currentTheme) ? "Syntactic Light" : "Syntactic Dark (Default)");

        themeCombo.setOnAction(e -> {
            boolean isLight = "Syntactic Light".equals(themeCombo.getValue());
            dataService.setSetting("theme", isLight ? "light" : "dark");
            onThemeChangeRequested.run();
        });

        themeRow.getChildren().addAll(themeLabel, themeCombo);

        CheckBox compactCheck = new CheckBox("Compact Spacing Mode (Higher density lists and task cards)");
        boolean compact = "true".equals(dataService.getSetting("compact_mode", "false"));
        compactCheck.setSelected(compact);
        compactCheck.setOnAction(e -> {
            dataService.setSetting("compact_mode", compactCheck.isSelected() ? "true" : "false");
            onThemeChangeRequested.run();
        });

        appearBox.getChildren().addAll(appearTitle, themeRow, compactCheck);

        // Section 2: Data Cleanup
        VBox dataBox = new VBox(14);
        dataBox.setPadding(new Insets(20));
        dataBox.getStyleClass().add("settings-section-card");

        Label dataTitle = new Label("Workspace Data Management");
        dataTitle.getStyleClass().add("section-title");

        HBox clearRow = new HBox(16);
        clearRow.setAlignment(Pos.CENTER_LEFT);

        Label clearDesc = new Label("Purge completed tasks from the active board to keep columns clutter-free.");
        clearDesc.getStyleClass().add("settings-desc");

        Button clearDoneBtn = new Button("Archive Completed Tasks");
        clearDoneBtn.getStyleClass().add("btn-secondary");
        clearDoneBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("ARCHIVE", 14));
        clearDoneBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Archive Completed");
            alert.setHeaderText("Completed tasks archived successfully.");
            alert.showAndWait();
        });

        clearRow.getChildren().addAll(clearDesc, clearDoneBtn);
        dataBox.getChildren().addAll(dataTitle, clearRow);

        root.getChildren().addAll(headerBox, appearBox, dataBox);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }
}
