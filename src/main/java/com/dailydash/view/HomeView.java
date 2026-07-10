/**
 * @file HomeView.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Custom Home Page view for DailyDash.
 *
 * @description
 * Displays dynamic greeting, quick-access Starred Projects grid with progress bars, and workspace KPI summary.
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

// ---------- CLASS: HomeView
public class HomeView {

    public static ScrollPane build(TaskDataService dataService, Consumer<Project> onOpenProject) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.getStyleClass().add("page-container");

        // Dynamic Greeting
        VBox headerBox = new VBox(4);
        int hour = LocalTime.now().getHour();
        String greeting = hour < 12 ? "Good morning!" : (hour < 18 ? "Good afternoon!" : "Good evening!");

        Label greetingLabel = new Label(greeting);
        greetingLabel.getStyleClass().add("page-title");

        Label subtitleLabel = new Label("Here is your workspace overview and starred projects.");
        subtitleLabel.getStyleClass().add("page-subtitle");
        headerBox.getChildren().addAll(greetingLabel, subtitleLabel);

        // KPI Summary Cards
        HBox kpiBox = new HBox(16);
        kpiBox.setAlignment(Pos.CENTER_LEFT);

        List<Project> projects = dataService.getProjects();
        int totalTasks = 0;
        int completedTasks = 0;
        int highPriorityTasks = 0;

        for (Project p : projects) {
            List<Task> tasks = dataService.getAllTasks(p.getId());
            totalTasks += tasks.size();
            for (Task t : tasks) {
                if (t.getStatus() == Status.DONE) {
                    completedTasks++;
                }
                if ((t.getPriority() == com.dailydash.model.Priority.HIGH || t.getPriority() == com.dailydash.model.Priority.URGENT) && t.getStatus() != Status.DONE) {
                    highPriorityTasks++;
                }
            }
        }

        int completionRate = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;

        kpiBox.getChildren().addAll(
                buildKpiCard("Active Projects", String.valueOf(projects.size()), "Total boards in workspace"),
                buildKpiCard("Total Tasks", String.valueOf(totalTasks), "Tasks across all projects"),
                buildKpiCard("Completion Rate", completionRate + "%", completedTasks + " finished tasks"),
                buildKpiCard("High Priority Pending", String.valueOf(highPriorityTasks), "Requires attention")
        );

        // Starred Projects Section
        VBox starredSection = new VBox(14);
        Label starredHeader = new Label("⭐ Starred Projects Quick Access");
        starredHeader.getStyleClass().add("section-title");

        FlowPane cardsGrid = new FlowPane();
        cardsGrid.setHgap(16);
        cardsGrid.setVgap(16);

        List<Project> starredProjects = projects.stream().filter(Project::isStarred).toList();
        if (starredProjects.isEmpty()) {
            Label emptyStarred = new Label("No starred projects yet. Star projects from the Projects page for instant access!");
            emptyStarred.getStyleClass().add("empty-message");
            starredSection.getChildren().addAll(starredHeader, emptyStarred);
        } else {
            for (Project p : starredProjects) {
                cardsGrid.getChildren().add(buildProjectMiniCard(p, dataService, onOpenProject));
            }
            starredSection.getChildren().addAll(starredHeader, cardsGrid);
        }

        root.getChildren().addAll(headerBox, kpiBox, starredSection);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }

    private static VBox buildKpiCard(String title, String val, String desc) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setPrefWidth(210);
        card.getStyleClass().add("kpi-card");

        Label titleL = new Label(title);
        titleL.getStyleClass().add("kpi-title");

        Label valL = new Label(val);
        valL.getStyleClass().add("kpi-value");

        Label descL = new Label(desc);
        descL.getStyleClass().add("kpi-desc");

        card.getChildren().addAll(titleL, valL, descL);
        return card;
    }

    private static VBox buildProjectMiniCard(Project p, TaskDataService dataService, Consumer<Project> onOpenProject) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setPrefWidth(260);
        card.getStyleClass().add("project-mini-card");

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label("★ " + p.getName());
        name.getStyleClass().add("project-card-title");
        top.getChildren().add(name);

        List<Task> tasks = dataService.getAllTasks(p.getId());
        long doneCount = tasks.stream().filter(t -> t.getStatus() == Status.DONE).count();
        double ratio = tasks.isEmpty() ? 0 : (double) doneCount / tasks.size();

        ProgressBar pb = new ProgressBar(ratio);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.getStyleClass().add("project-progress-bar");

        Label progressText = new Label(doneCount + " of " + tasks.size() + " tasks completed (" + (int)(ratio * 100) + "%)");
        progressText.getStyleClass().add("project-progress-text");

        Button openBtn = new Button("Open Board");
        openBtn.getStyleClass().add("primary-btn");
        openBtn.setMaxWidth(Double.MAX_VALUE);
        openBtn.setOnAction(e -> onOpenProject.accept(p));

        card.getChildren().addAll(top, pb, progressText, openBtn);
        return card;
    }
}
