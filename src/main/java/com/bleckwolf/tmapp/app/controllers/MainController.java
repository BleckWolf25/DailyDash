package com.bleckwolf.tmapp.app.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bleckwolf.tmapp.model.Task;
import com.bleckwolf.tmapp.model.TaskList;
import com.bleckwolf.tmapp.service.TaskDataService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * Main controller class for managing the primary application view.
 * Handles user interactions, data presentation, and business logic coordination.
 * 
 * <p>Key responsibilities include:
 * <ul>
 *   <li>Managing the task table view and its data</li>
 *   <li>Handling CRUD operations for tasks</li>
 *   <li>Implementing filtering and search functionality</li>
 *   <li>Coordinating with data service layer</li>
 *   <li>Maintaining UI state and validation</li>
 * </ul>
 */

public class MainController {
    
    // Constants ----------------------------------------------------------------------------------
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final String DEFAULT_CATEGORY = "General";
    private static final String DATE_PICKER_DEFAULT_OFFSET = "1";
    private static final String STATUS_OVERDUE = "Overdue";
    private static final String STATUS_TODAY = "Today";
    private static final String STATUS_COMPLETED = "Completed";
    private static final String STATUS_ACTIVE = "Active";
    
    // UI Components ------------------------------------------------------------------------------
    // Table View
    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, Boolean> completedColumn;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> categoryColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    
    // Form Elements
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private RadioButton lowPriorityRadio;
    @FXML private RadioButton mediumPriorityRadio;
    @FXML private RadioButton highPriorityRadio;
    @FXML private CheckBox completedCheckBox;
    
    // Action Controls
    @FXML private Button addTaskButton;
    @FXML private Button deleteTaskButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
        
    // Filter/Search Components
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TextField searchField;
    
    // Status Bar Components
    @FXML private Label statusLabel;
    @FXML private Label taskCountLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label completionRateLabel;
    
    // Model and Data -----------------------------------------------------------------------------
    private TaskDataService dataService;
    private ObservableList<Task> tasksObservable;
    private FilteredList<Task> tasksFiltered;
    private Task currentTask;

    /**
     * Initializes the controller after FXML loading.
     * Sets up data bindings, UI configurations, and initial state.
     * 
     * <p>Execution flow:
     * <ol>
     *   <li>Initialize data service connection</li>
     *   <li>Load existing tasks</li>
     *   <li>Configure table columns and data bindings</li>
     *   <li>Set up filters and search functionality</li>
     *   <li>Initialize form components</li>
     *   <li>Register event listeners</li>
     * </ol>
     */
    @FXML
    public void initialize() {
        try {
            initializeDataLayer();
            configureTableView();
            initializeFilterSystem();
            initializeSearchSystem();
            initializeFormComponents();
            registerEventHandlers();
            updateStatusDisplay();
            resetFormState();
        } catch (RuntimeException e) {
            handleInitializationError(e);
        }
    }

    // Initialization Methods ----------------------------------------------------------------------
    
    /**
     * Initializes data service connection and loads tasks.
     * 
     * @throws RuntimeException if database connection fails
     */
    private void initializeDataLayer() {
        dataService = new TaskDataService();
        TaskList taskList = dataService.loadTasks();
        tasksObservable = FXCollections.observableArrayList(taskList.getAllTasks());
        tasksFiltered = new FilteredList<>(tasksObservable);
    }

    /**
     * Configures table view columns and data bindings.
     */
    private void configureTableView() {
        configureCompletedColumn();
        configureTitleColumn();
        configurePriorityColumn();
        configureCategoryColumn();
        configureDueDateColumn();
        configureStatusColumn();
        taskTableView.setItems(tasksFiltered);
    }

    /**
     * Initializes filter system with default options and categories.
     */
    private void initializeFilterSystem() {
        ObservableList<String> filters = FXCollections.observableArrayList(
            "All Tasks", "Active Tasks", "Completed Tasks",
            "High Priority", "Medium Priority", "Low Priority",
            "Due Today", "Overdue"
        );
        filters.addAll(extractUniqueCategories());
        
        filterComboBox.setItems(filters);
        filterComboBox.getSelectionModel().selectFirst();
        filterComboBox.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> applyFilter(newVal));
    }

    /**
     * Initializes search functionality with text listener.
     */
    private void initializeSearchSystem() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> 
            applySearchFilter(newVal.toLowerCase())
        );
    }

    /**
     * Initializes form components with default values.
     */
    private void initializeFormComponents() {
        categoryComboBox.setItems(FXCollections.observableArrayList(extractUniqueCategories()));
        dueDatePicker.setValue(LocalDate.now().plusDays(Integer.parseInt(DATE_PICKER_DEFAULT_OFFSET)));
    }

    /**
     * Registers event listeners for UI interactions.
     */
    private void registerEventHandlers() {
        taskTableView.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> handleTableSelection(newVal));
    }

    // Table Column Configurations ----------------------------------------------------------------
    
    private void configureCompletedColumn() {
        completedColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            return task.completedProperty().asObject();
        });
        completedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(completedColumn));
    }

    private void configureTitleColumn() {
        titleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTitle())
        );
    }

    private void configurePriorityColumn() {
        priorityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPriority().toString())
        );
        priorityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(priority);
                    setStyle(getPriorityStyle(priority));
                }
            }

            private String getPriorityStyle(String priority) {
                return switch (priority) {
                    case "HIGH" -> "-fx-text-fill: #d32f2f;";
                    case "MEDIUM" -> "-fx-text-fill: #ff9800;";
                    case "LOW" -> "-fx-text-fill: #4caf50;";
                    default -> "";
                };
            }
        });
    }

    private void configureCategoryColumn() {
        categoryColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategory())
        );
    }

    private void configureDueDateColumn() {
        dueDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(DATE_FORMATTER.format(cellData.getValue().getDueDate()))
        );
    }

    private void configureStatusColumn() {
        statusColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            return new SimpleStringProperty(determineTaskStatus(task));
        });
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle(getStatusStyle(status));
                }
            }
            
            private String getStatusStyle(String status) {
                return switch (status) {
                    case STATUS_OVERDUE -> "-fx-text-fill: #d32f2f; -fx-font-weight: bold;";
                    case STATUS_TODAY -> "-fx-text-fill: #ff9800; -fx-font-weight: bold;";
                    case STATUS_COMPLETED -> "-fx-text-fill: #4caf50;";
                    default -> "";
                };
            }
        });
    }

    // Filter/Search Implementation ---------------------------------------------------------------
    
    /**
     * Applies selected filter to task list.
     * 
     * @param filter Criteria to apply (null-safe)
     */
    private void applyFilter(String filter) {
        if (filter == null) {
            tasksFiltered.setPredicate(task -> true);
            updateStatusDisplay();
            return;
        }
        
        Predicate<Task> filterPredicate = createFilterPredicate(filter);
        tasksFiltered.setPredicate(filterPredicate);
        updateStatusDisplay();
    }

    /**
     * Applies search filter to currently visible tasks.
     * 
     * @param searchTerm Text to search for (lowercase)
     */
    private void applySearchFilter(String searchTerm) {
        String filter = filterComboBox.getValue();
        Predicate<Task> filterPredicate = createFilterPredicate(filter);
        Predicate<Task> searchPredicate = createSearchPredicate(searchTerm);
        
        tasksFiltered.setPredicate(filterPredicate.and(searchPredicate));
        updateStatusDisplay();
    }
    

    // Form Handling ------------------------------------------------------------------------------
    
    /**
     * Handles table selection changes.
     * 
     * @param selectedTask Newly selected task (null indicates deselection)
     */
    private void handleTableSelection(Task selectedTask) {
        if (selectedTask != null) {
            currentTask = selectedTask;
            populateForm(currentTask);
            enableFormEditing(true);
        } else {
            resetFormState();
        }
    }

    /**
     * Populates form fields with task data.
     * 
     * @param task Task to display in form
     */
    private void populateForm(Task task) {
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        categoryComboBox.setValue(task.getCategory());
        dueDatePicker.setValue(task.getDueDate());
        setPrioritySelection(task.getPriority());
        completedCheckBox.setSelected(task.isCompleted());
    }

    /**
     * Resets form to default state.
     */
    private void resetFormState() {
        clearFormFields();
        enableFormEditing(false);
        currentTask = null;
    }

    /**
     * Clears all form input fields.
     */
    private void clearFormFields() {
        titleField.clear();
        descriptionArea.clear();
        categoryComboBox.setValue(DEFAULT_CATEGORY);
        dueDatePicker.setValue(LocalDate.now().plusDays(Integer.parseInt(DATE_PICKER_DEFAULT_OFFSET)));
        mediumPriorityRadio.setSelected(true);
        completedCheckBox.setSelected(false);
    }

    /**
     * Enables/disables form editing.
     * 
     * @param enabled True to enable form controls
     */
    private void enableFormEditing(boolean enabled) {
        Control[] formControls = {
            titleField, descriptionArea, categoryComboBox, dueDatePicker,
            lowPriorityRadio, mediumPriorityRadio, highPriorityRadio,
            completedCheckBox, saveButton, cancelButton
        };
        
        for (Control control : formControls) {
            control.setDisable(!enabled);
        }
    }

    // Event Handlers -----------------------------------------------------------------------------
    
    /**
     * Handles add task action.
     */
    @FXML
    private void handleAddTask() {
        resetFormState();
        enableFormEditing(true);
        titleField.requestFocus();
    }

    /**
     * Handles delete task action.
     */
    @FXML
    private void handleDeleteTask() {
        Task selected = taskTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSelectionAlert();
            return;
        }
        
        ConfirmationDialog dialog = new ConfirmationDialog(
            "Delete Task",
            "Confirm Deletion",
            "Delete task '" + selected.getTitle() + "'?"
        );
        
        if (dialog.showAndWait()) {
            performTaskDeletion(selected);
        }
    }

    /**
     * Handles task save action.
     */
    @FXML
    private void handleSave() {
        if (!validateForm()) return;
        
        Task task = currentTask != null ? currentTask : new Task();
        updateTaskFromForm(task);
        
        dataService.saveTask(task);
        updateTaskList(task);
        updateCategoryOptions();
        updateStatusDisplay();
        resetFormState();
    }

    /**
     * Handles cancel action.
     */
    @FXML
    private void handleCancel() {
        resetFormState();
    }

    // Helper Methods -----------------------------------------------------------------------------
    
    private Set<String> extractUniqueCategories() {
        Set<String> categories = new HashSet<>();
        categories.add(DEFAULT_CATEGORY);
        
        if (tasksObservable != null) {
            tasksObservable.stream()
                .map(Task::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .forEach(categories::add);
        }
        
        return categories;
    }

    private String determineTaskStatus(Task task) {
        if (task.isCompleted()) return STATUS_COMPLETED;  // Was "Completed"
        if (task.isOverdue()) return STATUS_OVERDUE;      // Was "Overdue"
        if (task.getDueDate().isEqual(LocalDate.now())) return STATUS_TODAY;
        return STATUS_ACTIVE;
    }

    private Predicate<Task> createFilterPredicate(String filter) {
        if (filter == null) return task -> true;
        
        return switch (filter) {
            case "Active Tasks" -> task -> !task.isCompleted();
            case "Completed Tasks" -> Task::isCompleted;
            case "High Priority" -> task -> task.getPriority() == Task.Priority.HIGH;
            case "Medium Priority" -> task -> task.getPriority() == Task.Priority.MEDIUM;
            case "Low Priority" -> task -> task.getPriority() == Task.Priority.LOW;
            case "Due Today" -> task -> task.getDueDate().isEqual(LocalDate.now());
            case "Overdue" -> Task::isOverdue;
            default -> {
                if (filter.startsWith("Category: ")) {
                    String category = filter.substring("Category: ".length());
                    yield task -> task.getCategory().equalsIgnoreCase(category);
                } else if (extractUniqueCategories().contains(filter)) {
                    yield task -> task.getCategory().equals(filter);
                }
                yield task -> true;
            }
        };
    }

    private Predicate<Task> createSearchPredicate(String term) {
        return term.isEmpty() 
            ? task -> true 
            : task -> {
                String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                String desc = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                String category = task.getCategory() != null ? task.getCategory().toLowerCase() : "";
                
                return title.contains(term) || desc.contains(term) || category.contains(term);
            };
    }

    private void setPrioritySelection(Task.Priority priority) {
        switch (priority) {
            case LOW -> lowPriorityRadio.setSelected(true);
            case MEDIUM -> mediumPriorityRadio.setSelected(true);
            case HIGH -> highPriorityRadio.setSelected(true);
        }
    }

    private void updateTaskFromForm(Task task) {
        task.setTitle(titleField.getText());
        task.setDescription(descriptionArea.getText());
        task.setCategory(categoryComboBox.getValue());
        task.setDueDate(dueDatePicker.getValue());
        task.setPriority(getSelectedPriority());
        task.setCompleted(completedCheckBox.isSelected());
    }

    private Task.Priority getSelectedPriority() {
        if (highPriorityRadio.isSelected()) return Task.Priority.HIGH;
        if (mediumPriorityRadio.isSelected()) return Task.Priority.MEDIUM;
        return Task.Priority.LOW;
    }

    // Validation & Status Updates ----------------------------------------------------------------
    
    /**
     * Validates form inputs.
     * 
     * @return true if all required fields are valid
     */
    private boolean validateForm() {
        ValidationResult result = new ValidationResult()
            .requireNonEmpty(titleField, "Title")
            .requireSelected(categoryComboBox, "Category")
            .requireNonNull(dueDatePicker.getValue(), "Due Date");
        
        if (!result.isValid()) {
            showValidationAlert(result.getErrors());
            return false;
        }
        return true;
    }

    /**
     * Updates status bar with current statistics.
     */
    private void updateStatusDisplay() {
        int total = tasksObservable.size();
        long completed = tasksObservable.stream().filter(Task::isCompleted).count();
        int visible = tasksFiltered.size();
        
        double progress = total > 0 ? (double) completed / total : 0;
        String statusText = visible < total 
            ? String.format("Showing %d of %d tasks", visible, total)
            : "All tasks visible";
        
        progressBar.setProgress(progress);
        completionRateLabel.setText(String.format("%.0f%%", progress * 100));
        taskCountLabel.setText(visible + " tasks");
        statusLabel.setText(statusText);
    }

    // Error Handling -----------------------------------------------------------------------------
    
    private void handleInitializationError(Exception e) {
        logger.error("Application initialization failed", e);
        showErrorAlert(
            "Initialization Error",
            "Failed to initialize application",
            e.getMessage()
        );
    }

    private void performTaskDeletion(Task task) {
        try {
            dataService.deleteTask(task.getId());
            tasksObservable.remove(task);
            updateStatusDisplay();
            statusLabel.setText("Task deleted: " + task.getTitle());
        } catch (RuntimeException e) {
            logger.error("Task deletion failed", e);
            showErrorAlert(
                "Deletion Error",
                "Failed to delete task",
                e.getMessage()
            );
        }
    }

    private void updateTaskList(Task task) {
        if (!tasksObservable.contains(task)) {
            tasksObservable.add(task);
        } else {
            // Update the existing task in the list
            int index = tasksObservable.indexOf(task);
            if (index >= 0) {
                tasksObservable.set(index, task);
            }
            taskTableView.refresh();
        }
    }

    private void updateCategoryOptions() {
        Set<String> categories = extractUniqueCategories();
        
        // Update filter combobox
        ObservableList<String> currentFilters = filterComboBox.getItems();
        Set<String> filteredCategories = new HashSet<>(currentFilters);
        
        // Add new categories
        for (String category : categories) {
            if (!filteredCategories.contains(category)) {
                currentFilters.add(category);
            }
        }
        
        // Update category combobox
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }

    // Alert Dialogs ------------------------------------------------------------------------------
    
    private void showSelectionAlert() {
        showInformationAlert(
            "No Selection",
            "No Task Selected",
            "Please select a task to perform this action"
        );
    }

    private void showValidationAlert(String errors) {
        showErrorAlert(
            "Invalid Input",
            "Please correct the following issues:",
            errors
        );
    }

    private void showErrorAlert(String title, String header, String content) {
        new AlertDialog(Alert.AlertType.ERROR, title, header, content).show();
    }

    private void showInformationAlert(String title, String header, String content) {
        new AlertDialog(Alert.AlertType.INFORMATION, title, header, content).show();
    }

    // Helper Classes -----------------------------------------------------------------------------
    
    /**
     * Validation result container.
     */
    private static class ValidationResult {
        private final StringBuilder errors = new StringBuilder();
        
        ValidationResult requireNonEmpty(TextInputControl field, String fieldName) {
            if (field.getText() == null || field.getText().trim().isEmpty()) {
                errors.append(String.format("- %s is required%n", fieldName));
            }
            return this;
        }
        
        ValidationResult requireSelected(ComboBox<?> comboBox, String fieldName) {
            if (comboBox.getValue() == null || comboBox.getValue().toString().trim().isEmpty()) {
                errors.append(String.format("- %s must be selected%n", fieldName));
            }
            return this;
        }
        
        ValidationResult requireNonNull(Object value, String fieldName) {
            if (value == null) {
                errors.append(String.format("- %s must be selected%n", fieldName));
            }
            return this;
        }
        
        boolean isValid() {
            return errors.isEmpty();
        }
        
        String getErrors() {
            return errors.toString();
        }
    }

    /**
     * Custom confirmation dialog wrapper.
     */
    private static class ConfirmationDialog {
        private final Alert dialog;
        
        ConfirmationDialog(String title, String header, String content) {
            dialog = new Alert(Alert.AlertType.CONFIRMATION);
            dialog.setTitle(title);
            dialog.setHeaderText(header);
            dialog.setContentText(content);
        }
        
        boolean showAndWait() {
            return dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
        }
    }

    /**
     * Custom alert dialog wrapper.
     */
    private static class AlertDialog {
        private final Alert dialog;
        
        AlertDialog(Alert.AlertType type, String title, String header, String content) {
            dialog = new Alert(type);
            dialog.setTitle(title);
            dialog.setHeaderText(header);
            dialog.setContentText(content);
        }
        
        void show() {
            dialog.showAndWait();
        }
    }
}