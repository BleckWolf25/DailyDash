/**
 * @file MainController.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Main board controller for DailyDash.
 *
 * @description
 * Handles UI events, filters, drag-and-drop, theme configuration, and multiple-project boards.
 *
 * @since 07/07/2026
 * @updated 10/07/2026
 */
// ---------- PACKAGE
package com.dailydash.controller;

import com.dailydash.model.Project;
import com.dailydash.model.Priority;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import com.dailydash.model.TaskList;
import com.dailydash.service.AutomationEngine;
import com.dailydash.service.TaskDataService;
import com.dailydash.view.AnalysisView;
import com.dailydash.view.AutomationsView;
import com.dailydash.view.HomeView;
import com.dailydash.view.ProjectsView;
import com.dailydash.view.SettingsView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

// ---------- CLASS: MainController
public class MainController {

    @FXML private StackPane contentStack;
    @FXML private VBox boardContainer;

    @FXML private Button navHomeBtn;
    @FXML private Button navProjectsBtn;
    @FXML private Button navBoardBtn;
    @FXML private Button navAutomationsBtn;
    @FXML private Button navAnalysisBtn;
    @FXML private Button navSettingsBtn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    @FXML private ListView<Task> todoListView;
    @FXML private ListView<Task> inProgressListView;
    @FXML private ListView<Task> doneListView;

    @FXML private Label todoCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label doneCountLabel;

    @FXML private ComboBox<Project> projectSelector;
    @FXML private Button themeToggleButton;

    private TaskList taskList;
    private FilteredList<Task> masterFilteredList;
    private TaskDataService dataService;
    private AutomationEngine automationEngine;
    private boolean isLightTheme = false;

    @FXML
    public void initialize() {
        dataService = new TaskDataService();
        automationEngine = new AutomationEngine(dataService);
        taskList = new TaskList();
        taskList.setAutomationEngine(automationEngine);

        // 1. Setup Theme from configurations
        initializeTheme();

        // 2. Setup Projects
        initializeProjects();

        // 3. Setup Master Filter
        masterFilteredList = new FilteredList<>(taskList.getTasks(), p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilters());
        priorityFilter.getItems().addAll("ALL", "URGENT", "HIGH", "MEDIUM", "LOW");
        priorityFilter.setValue("ALL");
        priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());

        // 4. Setup Columns & Drag-and-Drop
        setupColumn(todoListView, Status.TODO, todoCountLabel);
        setupColumn(inProgressListView, Status.IN_PROGRESS, inProgressCountLabel);
        setupColumn(doneListView, Status.DONE, doneCountLabel);

        // 5. Setup Progress Indicator
        taskList.getTasks().addListener((ListChangeListener<Task>) c -> updateProgress());
        updateProgress(); // Initial calculation

        // 6. Setup Vector Icons
        navHomeBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("HOME", 16));
        navProjectsBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("PROJECTS", 16));
        navBoardBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("BOARD", 16));
        navAutomationsBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("AUTOMATIONS", 16));
        navAnalysisBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("ANALYTICS", 16));
        navSettingsBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("SETTINGS", 16));

        // Default to Home view
        Platform.runLater(() -> showHome(null));
    }

    // Home Handler
    @FXML
    public void showHome(ActionEvent event) {
        setActiveNavButton(navHomeBtn);
        contentStack.getChildren().setAll(HomeView.build(dataService, this::openProjectBoard));
    }

    // Projects Handler
    @FXML
    public void showProjects(ActionEvent event) {
        setActiveNavButton(navProjectsBtn);
        contentStack.getChildren().setAll(ProjectsView.build(dataService, this::openProjectBoard, () -> {
            initializeProjects();
            showProjects(null);
        }));
    }

    // Board Handler
    @FXML
    public void showBoard(ActionEvent event) {
        setActiveNavButton(navBoardBtn);
        contentStack.getChildren().setAll(boardContainer);
    }

    // Automations Handler
    @FXML
    public void showAutomations(ActionEvent event) {
        setActiveNavButton(navAutomationsBtn);
        Project currentProj = projectSelector.getValue();
        if (currentProj == null) {
            currentProj = new Project(taskList.getCurrentProjectId(), "Current Board");
        }
        contentStack.getChildren().setAll(AutomationsView.build(currentProj, dataService, () -> showAutomations(null)));
    }

    // Analysis Handler
    @FXML
    public void showAnalysis(ActionEvent event) {
        setActiveNavButton(navAnalysisBtn);
        contentStack.getChildren().setAll(AnalysisView.build(dataService));
    }

    // Settings Handler
    @FXML
    public void showSettings(ActionEvent event) {
        setActiveNavButton(navSettingsBtn);
        contentStack.getChildren().setAll(SettingsView.build(dataService, () -> {
            initializeTheme();
            showSettings(null);
        }));
    }

    // Open Project Board Handler
    public void openProjectBoard(Project project) {
        if (project != null) {
            Project match = projectSelector.getItems().stream()
                    .filter(p -> p.getId() == project.getId())
                    .findFirst()
                    .orElse(project);
            projectSelector.setValue(match);
            showBoard(null);
        }
    }

    // Set Active Navigation Button
    private void setActiveNavButton(Button activeBtn) {
        Button[] navs = {navHomeBtn, navProjectsBtn, navBoardBtn, navAutomationsBtn, navAnalysisBtn, navSettingsBtn};
        for (Button btn : navs) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-nav-button-active");
                if (btn == activeBtn && !btn.getStyleClass().contains("sidebar-nav-button-active")) {
                    btn.getStyleClass().add("sidebar-nav-button-active");
                }
            }
        }
    }

    // Initialize Theme based on saved settings
    private void initializeTheme() {
        String theme = dataService.getSetting("theme", "dark");
        isLightTheme = "light".equals(theme);

        // Defer theme application to after the scene is set
        Platform.runLater(this::applyThemeStyle);
    }

    // Apply the current theme to the root scene
    private void applyThemeStyle() {
        if (themeToggleButton.getScene() == null) {
            return;
        }
        Parent root = themeToggleButton.getScene().getRoot();
        if (isLightTheme) {
            themeToggleButton.setText("Dark Mode");
            themeToggleButton.setGraphic(com.dailydash.util.IconUtil.getIcon("MOON", 16));
            if (!root.getStyleClass().contains("light-theme")) {
                root.getStyleClass().add("light-theme");
            }
        } else {
            themeToggleButton.setText("Light Mode");
            themeToggleButton.setGraphic(com.dailydash.util.IconUtil.getIcon("SUN", 16));
            root.getStyleClass().remove("light-theme");
        }
    }

    // Toggle Theme Handler
    @FXML
    public void handleToggleTheme(ActionEvent event) {
        isLightTheme = !isLightTheme;
        dataService.setSetting("theme", isLightTheme ? "light" : "dark");
        applyThemeStyle();
        if (navSettingsBtn.getStyleClass().contains("sidebar-nav-button-active")) {
            showSettings(null);
        }
    }

    // Initialize Projects and setup the project selector
    private void initializeProjects() {
        dataService.seedShowcaseProjectIfEmpty();
        List<Project> projects = dataService.getProjects();
        if (projects.isEmpty()) {
            dataService.insertProject("Default Board");
            projects = dataService.getProjects();
        }

        projectSelector.getItems().setAll(projects);

        // Select project ID 1 or the first one available
        Project defaultProject = projects.stream()
                .filter(p -> p.getId() == 1)
                .findFirst()
                .orElse(projects.get(0));

        projectSelector.setValue(defaultProject);
        taskList.setCurrentProjectId(defaultProject.getId());

        // Handle project selection change
        projectSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                taskList.setCurrentProjectId(newVal.getId());
                updateFilters();
            }
        });

        // Silent automatic startup check for GitHub release updates
        com.dailydash.service.UpdateService.checkForUpdatesAsync(
                release -> com.dailydash.view.UpdateDialog.show(release, isLightTheme),
                msg -> {}
        );
    }

    @FXML
    public void handleNewProject(ActionEvent event) {
        Optional<String> result = com.dailydash.view.NewBoardDialog.show(isLightTheme);
        result.ifPresent(name -> {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                int generatedId = dataService.insertProject(trimmed);
                if (generatedId != -1) {
                    List<Project> projects = dataService.getProjects();
                    projectSelector.getItems().setAll(projects);

                    // Select the newly created project
                    Project newProj = projects.stream()
                            .filter(p -> p.getId() == generatedId)
                            .findFirst()
                            .orElse(null);
                    if (newProj != null) {
                        projectSelector.setValue(newProj);
                    }
                }
            }
        });
    }

    @FXML
    public void handleDeleteProject(ActionEvent event) {
        Project selected = projectSelector.getValue();
        if (selected == null) {
            return;
        }

        if (selected.getId() == 1) {
            com.dailydash.view.UpdateDialog.showMessageDialog("Cannot Delete Board", "The default board cannot be deleted!", isLightTheme);
            return;
        }

        if (com.dailydash.view.DeleteProjectDialog.show(selected, isLightTheme)) {
            dataService.deleteProject(selected.getId());

            // Reload projects
            List<Project> projects = dataService.getProjects();
            projectSelector.getItems().setAll(projects);

            // Select default project
            Project defaultProject = projects.stream()
                    .filter(p -> p.getId() == 1)
                    .findFirst()
                    .orElse(projects.get(0));
            projectSelector.setValue(defaultProject);
        }
    }

    private void updateFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String priority = priorityFilter.getValue();

        masterFilteredList.setPredicate(task -> {
            boolean matchesSearch = task.getTitle().toLowerCase().contains(searchText) ||
                                   (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchText)) ||
                                   (task.getCategory() != null && task.getCategory().toLowerCase().contains(searchText));
            boolean matchesPriority = "ALL".equals(priority) || task.getPriority().name().equals(priority);
            return matchesSearch && matchesPriority;
        });
    }

    private void setupColumn(ListView<Task> listView, Status status, Label countLabel) {
        FilteredList<Task> columnList = new FilteredList<>(masterFilteredList, t -> t.getStatus() == status);
        listView.setItems(columnList);

        countLabel.textProperty().bind(Bindings.size(columnList).asString());

        // Pass taskList and status to TaskCell for drag and drop reordering
        listView.setCellFactory(param -> new TaskCell(taskList, status));

        final javafx.animation.PauseTransition clickTimer = new javafx.animation.PauseTransition(javafx.util.Duration.millis(220));
        listView.setOnMouseClicked(event -> {
            Task selectedTask = listView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                if (event.getClickCount() >= 2) {
                    clickTimer.stop();
                    openTaskDialog(selectedTask);
                } else if (event.getClickCount() == 1) {
                    clickTimer.setOnFinished(e -> {
                        javafx.application.Platform.runLater(() -> {
                            TaskDetailDialog.show(selectedTask, () -> javafx.application.Platform.runLater(() -> openTaskDialog(selectedTask)));
                        });
                    });
                    clickTimer.playFromStart();
                }
            }
        });

        // Accept drops on the ListView (fallback for empty space drops)
        listView.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Handle the drop on empty space (Appends task to bottom of column)
        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int taskId = Integer.parseInt(db.getString());
                    Task task = taskList.getTaskById(taskId);
                    if (task != null) {
                        // Move/Append task to the target column
                        taskList.reorderTask(task, status, null);
                        success = true;
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updateProgress() {
        int total = taskList.getTasks().size();
        if (total == 0) {
            progressBar.setProgress(0);
            progressLabel.setText("0%");
            return;
        }
        long doneCount = taskList.getTasks().stream().filter(t -> t.getStatus() == Status.DONE).count();
        double progress = (double) doneCount / total;
        progressBar.setProgress(progress);
        progressLabel.setText(Math.round(progress * 100) + "%");
    }

    @FXML
    public void handleNewTask(ActionEvent event) {
        openTaskDialog(null); // null means create a new task
    }

    private void applyAutomations(Task task) {
        String title = task.getTitle().toLowerCase();

        // 1. Auto-Priority: Set to HIGH if the title contains urgent/blocker/asap
        if (title.contains("urgent") || title.contains("blocker") || title.contains("asap")) {
            task.setPriority(Priority.HIGH);
        }

        // 2. Auto-Categorize: Set category based on title keyword tags
        if (title.contains("bug") || title.contains("fix")) {
            task.setCategory("Bug");
        } else if (title.contains("feat") || title.contains("feature")) {
            task.setCategory("Feature");
        }
    }

    private void openTaskDialog(Task taskToEdit) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/task_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            TaskDialogController dialogController = loader.getController();

            // If we are editing, pass the task to the dialog
            if (taskToEdit != null) {
                dialogController.setTaskToEdit(taskToEdit);
            }

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(taskToEdit == null ? "Create New Task" : "Edit Task");
            try {
                dialogStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
            } catch (Exception ignored) {}
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.setScene(new javafx.scene.Scene(root, 520, 560));
            dialogStage.setMinWidth(480);
            dialogStage.setMinHeight(520);

            // Re-apply light theme styling on the dialog if active
            if (isLightTheme) {
                root.getStyleClass().add("light-theme");
            }

            dialogStage.showAndWait();

            Task resultTask = dialogController.getTask();
            if (resultTask != null) {
                applyAutomations(resultTask);
                if (taskToEdit == null) {
                    taskList.addTask(resultTask); // It's a new task
                } else {
                    taskList.updateTask(resultTask); // It's an edited task
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading task dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
