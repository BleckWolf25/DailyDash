/**
 * @file ProjectsView.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Custom Projects Page view for DailyDash.
 *
 * @description
 * Rich card grid sorted by Starred Projects first with interactive star toggles, progress bars, and board creation/deletion.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.view;

// ---------- IMPORTS
import com.dailydash.model.Project;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import com.dailydash.service.TaskDataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

// ---------- CLASS: ProjectsView
public class ProjectsView {

    public static ScrollPane build(TaskDataService dataService, Consumer<Project> onOpenProject, Runnable onProjectsChanged) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.getStyleClass().add("page-container");

        // Header Row
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label pageTitle = new Label("Projects & Boards");
        pageTitle.getStyleClass().add("page-title");
        Label pageSub = new Label("Manage all your project boards. Starred projects appear first.");
        pageSub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(pageTitle, pageSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newProjBtn = new Button("New Project");
        newProjBtn.getStyleClass().add("primary-btn");
        newProjBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("PLUS", 14));
        newProjBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Project");
            dialog.setHeaderText("Create a New Task Board");
            dialog.setContentText("Project Name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    dataService.insertProject(trimmed);
                    onProjectsChanged.run();
                }
            });
        });

        headerBox.getChildren().addAll(titleBox, spacer, newProjBtn);

        // Projects Grid
        FlowPane grid = new FlowPane();
        grid.setHgap(18);
        grid.setVgap(18);

        List<Project> projects = dataService.getProjects();
        for (Project p : projects) {
            grid.getChildren().add(buildProjectCard(p, dataService, onOpenProject, onProjectsChanged));
        }

        root.getChildren().addAll(headerBox, grid);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }

    private static VBox buildProjectCard(Project p, TaskDataService dataService, Consumer<Project> onOpenProject, Runnable onProjectsChanged) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setPrefWidth(290);
        card.getStyleClass().add(p.isStarred() ? "project-card-starred" : "project-card");

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(p.getName());
        name.getStyleClass().add("project-card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button starBtn = new Button(p.isStarred() ? "★" : "☆");
        starBtn.getStyleClass().add("star-toggle-button");
        starBtn.setMinWidth(Region.USE_PREF_SIZE);
        starBtn.setMinHeight(Region.USE_PREF_SIZE);
        starBtn.setOnAction(e -> {
            dataService.toggleProjectStar(p.getId(), !p.isStarred());
            onProjectsChanged.run();
        });

        top.getChildren().addAll(name, spacer, starBtn);

        List<Task> tasks = dataService.getAllTasks(p.getId());
        long doneCount = tasks.stream().filter(t -> t.getStatus() == Status.DONE).count();
        double ratio = tasks.isEmpty() ? 0 : (double) doneCount / tasks.size();

        ProgressBar pb = new ProgressBar(ratio);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.getStyleClass().add("project-progress-bar");

        Label progressText = new Label(doneCount + " of " + tasks.size() + " tasks completed (" + (int)(ratio * 100) + "%)");
        progressText.getStyleClass().add("project-progress-text");

        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button openBtn = new Button("Open Board");
        openBtn.getStyleClass().add("primary-btn");
        HBox.setHgrow(openBtn, Priority.ALWAYS);
        openBtn.setMaxWidth(Double.MAX_VALUE);
        openBtn.setOnAction(e -> onOpenProject.accept(p));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("icon-delete-button");
        deleteBtn.setOnAction(e -> {
            boolean isLightTheme = false;
            if (deleteBtn.getScene() != null && deleteBtn.getScene().getRoot() != null) {
                isLightTheme = deleteBtn.getScene().getRoot().getStyleClass().contains("light-theme");
            }
            if (com.dailydash.view.DeleteProjectDialog.show(p, isLightTheme)) {
                dataService.deleteProject(p.getId());
                onProjectsChanged.run();
            }
        });

        actionBox.getChildren().addAll(openBtn, deleteBtn);

        card.getChildren().addAll(top, pb, progressText, actionBox);
        return card;
    }
}
