/**
 * @file TaskDialogController.java
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
 * @since 07/07/2026
 * @updated 07/07/2026
 */
// ---------- PACKAGE
package com.dailydash.controller;

// ---------- IMPORTS
import com.dailydash.model.Priority;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

// ---------- CLASS: TaskDialogController
public class TaskDialogController {

    @FXML private Label dialogTitleLabel;
    @FXML private Label dialogSubtitleLabel;
    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private ComboBox<Priority> priorityComboBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextArea descriptionArea;

    private Task resultTask = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        priorityComboBox.getItems().addAll(Priority.values());
        priorityComboBox.setValue(Priority.MEDIUM);
        if (dialogTitleLabel != null) dialogTitleLabel.setText("Create New Task");
        if (dialogSubtitleLabel != null) dialogSubtitleLabel.setText("Fill in details, markdown description, category tag, and priority.");
    }

    // NEW METHOD: Call this when opening the dialog to edit an existing task
    public void setTaskToEdit(Task task) {
        this.resultTask = task;
        this.isEditMode = true;
        if (dialogTitleLabel != null) dialogTitleLabel.setText("Edit Task");
        if (dialogSubtitleLabel != null) dialogSubtitleLabel.setText("Update markdown description, category tag, priority, or deadline.");
        
        titleField.setText(task.getTitle());
        categoryField.setText(task.getCategory());
        priorityComboBox.setValue(task.getPriority());
        dueDatePicker.setValue(task.getDueDate());
        descriptionArea.setText(task.getDescription());
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Task title cannot be empty!");
            alert.showAndWait();
            return;
        }

        if (isEditMode) {
            // Update the existing task object
            resultTask.setTitle(titleField.getText().trim());
            resultTask.setDescription(descriptionArea.getText() != null ? descriptionArea.getText().trim() : "");
            resultTask.setCategory(categoryField.getText() != null ? categoryField.getText().trim() : "");
            resultTask.setPriority(priorityComboBox.getValue());
            resultTask.setDueDate(dueDatePicker.getValue());
        } else {
            // Create a brand new task
            resultTask = new Task(
                    titleField.getText().trim(),
                    descriptionArea.getText() != null ? descriptionArea.getText().trim() : "",
                    categoryField.getText() != null ? categoryField.getText().trim() : "",
                    priorityComboBox.getValue(),
                    dueDatePicker.getValue(),
                    Status.TODO
            );
        }

        closeWindow();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        resultTask = null; // Return null so the MainController knows we cancelled
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    public Task getTask() {
        return resultTask;
    }
}