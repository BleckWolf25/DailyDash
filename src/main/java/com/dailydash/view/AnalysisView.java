/**
 * @file AnalysisView.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Custom Analysis Page with visual JavaFX charts.
 *
 * @description
 * Displays status distribution PieChart, priority BarChart, and project progress comparison.
 *
 * @since 08/07/2026
 *
 */
// ---------- PACKAGE
package com.dailydash.view;

// ---------- IMPORTS
import com.dailydash.model.Priority;
import com.dailydash.model.Project;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import com.dailydash.service.TaskDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

// ---------- CLASS: AnalysisView
public class AnalysisView {

    public static ScrollPane build(TaskDataService dataService) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.getStyleClass().add("page-container");

        // Header
        VBox headerBox = new VBox(4);
        Label title = new Label("Workspace Analytics & Insights");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Visual breakdown of productivity trends across your projects.");
        subtitle.getStyleClass().add("page-subtitle");
        headerBox.getChildren().addAll(title, subtitle);

        List<Project> projects = dataService.getProjects();

        // Calculate workspace stats
        int totalTodo = 0;
        int totalInProgress = 0;
        int totalDone = 0;
        int urgentP = 0;
        int highP = 0;
        int medP = 0;
        int lowP = 0;

        for (Project p : projects) {
            List<Task> tasks = dataService.getAllTasks(p.getId());
            for (Task t : tasks) {
                if (t.getStatus() == Status.TODO) {
                    totalTodo++;
                } else if (t.getStatus() == Status.IN_PROGRESS) {
                    totalInProgress++;
                } else if (t.getStatus() == Status.DONE) {
                    totalDone++;
                }

                if (t.getPriority() == Priority.URGENT) {
                    urgentP++;
                } else if (t.getPriority() == Priority.HIGH) {
                    highP++;
                } else if (t.getPriority() == Priority.MEDIUM) {
                    medP++;
                } else if (t.getPriority() == Priority.LOW) {
                    lowP++;
                }
            }
        }

        int totalTasks = totalTodo + totalInProgress + totalDone;
        int completionRate = totalTasks > 0 ? (totalDone * 100) / totalTasks : 0;

        // KPI row
        HBox kpiBox = new HBox(16);
        kpiBox.getChildren().addAll(
                buildKpi("Total Tasks", String.valueOf(totalTasks)),
                buildKpi("To Do", String.valueOf(totalTodo)),
                buildKpi("In Progress", String.valueOf(totalInProgress)),
                buildKpi("Done", String.valueOf(totalDone)),
                buildKpi("Overall Rate", completionRate + "%")
        );

        // Charts Row 1: Status PieChart & Priority BarChart
        HBox chartsRow1 = new HBox(20);
        chartsRow1.setAlignment(Pos.CENTER_LEFT);

        // PieChart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("To Do (" + totalTodo + ")", totalTodo),
                new PieChart.Data("In Progress (" + totalInProgress + ")", totalInProgress),
                new PieChart.Data("Done (" + totalDone + ")", totalDone)
        );
        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Task Status Distribution");
        pieChart.setPrefSize(420, 300);
        pieChart.getStyleClass().add("analytics-chart");

        // Priority BarChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Task Priority Breakdown");
        barChart.setLegendVisible(false);
        barChart.setPrefSize(440, 300);
        barChart.getStyleClass().add("analytics-chart");

        XYChart.Series<String, Number> prioritySeries = new XYChart.Series<>();
        prioritySeries.getData().add(new XYChart.Data<>("Urgent", urgentP));
        prioritySeries.getData().add(new XYChart.Data<>("High", highP));
        prioritySeries.getData().add(new XYChart.Data<>("Medium", medP));
        prioritySeries.getData().add(new XYChart.Data<>("Low", lowP));
        barChart.getData().add(prioritySeries);

        chartsRow1.getChildren().addAll(pieChart, barChart);

        // Charts Row 2: Project Comparison BarChart
        CategoryAxis projX = new CategoryAxis();
        NumberAxis projY = new NumberAxis();
        BarChart<String, Number> projChart = new BarChart<>(projX, projY);
        projChart.setTitle("Tasks Completed by Project");
        projChart.setLegendVisible(false);
        projChart.setPrefHeight(260);
        projChart.getStyleClass().add("analytics-chart");

        XYChart.Series<String, Number> projSeries = new XYChart.Series<>();
        for (Project p : projects) {
            List<Task> tasks = dataService.getAllTasks(p.getId());
            long done = tasks.stream().filter(t -> t.getStatus() == Status.DONE).count();
            projSeries.getData().add(new XYChart.Data<>(p.getName(), done));
        }
        projChart.getData().add(projSeries);

        root.getChildren().addAll(headerBox, kpiBox, chartsRow1, projChart);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }

    private static VBox buildKpi(String label, String val) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14));
        card.setPrefWidth(160);
        card.getStyleClass().add("kpi-card");

        Label titleL = new Label(label);
        titleL.getStyleClass().add("kpi-title");

        Label valL = new Label(val);
        valL.getStyleClass().add("kpi-value");

        card.getChildren().addAll(titleL, valL);
        return card;
    }
}
