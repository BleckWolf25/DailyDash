/**
 * @file TaskCell.java
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
import com.dailydash.model.TaskList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// ---------- CLASS: TaskCell
public class TaskCell extends ListCell<Task> {

    private static final DateTimeFormatter DUE_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d");

    private VBox cardRoot;
    private Label categoryTag;
    private Label idLabel;
    private Label titleLabel;
    private Label priorityLabel;
    private Label dueDateLabel;

    public TaskCell(TaskList taskList) {
        this(taskList, null);
    }

    public TaskCell(TaskList taskList, Status columnStatus) {
        try {
            // Load the FXML layout for the card
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/task_card.fxml"));
            cardRoot = loader.load();

            // Look up the nodes by their fx:id
            categoryTag = (Label) cardRoot.lookup("#categoryTag");
            idLabel = (Label) cardRoot.lookup("#idLabel");
            titleLabel = (Label) cardRoot.lookup("#titleLabel");
            priorityLabel = (Label) cardRoot.lookup("#priorityLabel");
            dueDateLabel = (Label) cardRoot.lookup("#dueDateLabel");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Keep the Drag-and-Drop functionality
        setOnDragDetected(event -> {
            if (getItem() == null) return;
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getItem().getId()));
            dragboard.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                try {
                    int draggedId = Integer.parseInt(event.getDragboard().getString());
                    Task draggedTask = taskList.getTaskById(draggedId);
                    if (draggedTask != null) {
                        if (getItem() == null) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        } else if (getItem().getId() != draggedId) {
                            event.acceptTransferModes(TransferMode.MOVE);
                            if (cardRoot != null) {
                                boolean placeAfter = event.getY() >= (getHeight() / 2.0);
                                cardRoot.getStyleClass().removeAll("drop-above", "drop-below");
                                cardRoot.getStyleClass().add(placeAfter ? "drop-below" : "drop-above");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getDragboard().hasString()) {
                try {
                    int draggedId = Integer.parseInt(event.getDragboard().getString());
                    if (getItem() != null && getItem().getId() != draggedId && cardRoot != null) {
                        cardRoot.getStyleClass().add("task-card-drag-over");
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            event.consume();
        });

        setOnDragExited(event -> {
            if (cardRoot != null) {
                cardRoot.getStyleClass().removeAll("task-card-drag-over", "drop-above", "drop-below");
            }
            event.consume();
        });

        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int draggedId = Integer.parseInt(db.getString());
                    Task draggedTask = taskList.getTaskById(draggedId);
                    Task targetTask = getItem();
                    if (draggedTask != null) {
                        if (targetTask == null) {
                            Status targetStatus = columnStatus != null ? columnStatus : draggedTask.getStatus();
                            taskList.reorderTask(draggedTask, targetStatus, null, false);
                            success = true;
                        } else if (draggedTask.getId() != targetTask.getId()) {
                            boolean placeAfter = event.getY() >= (getHeight() / 2.0);
                            taskList.reorderTask(draggedTask, targetTask.getStatus(), targetTask, placeAfter);
                            success = true;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            if (cardRoot != null) {
                cardRoot.getStyleClass().removeAll("task-card-drag-over", "drop-above", "drop-below");
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setGraphic(null);
            setStyle("-fx-background-color: transparent;");
        } else {
            idLabel.setText(String.format("TASK-%03d", task.getId()));
            titleLabel.setText(task.getTitle());

            applyCategoryStyle(task.getCategory());
            applyPriorityStyle(task.getPriority());
            applyDueDateStyle(task.getDueDate(), task.getStatus());

            setGraphic(cardRoot);
            setStyle("-fx-background-color: transparent;");
        }
    }

    // Colors the category chip based on common keywords, falling back to a neutral tag.
    private void applyCategoryStyle(String category) {
        categoryTag.getStyleClass().removeAll("tag-feature", "tag-bug", "tag-uiux", "tag-devops", "tag-default");

        String key = category == null ? "" : category.trim().toLowerCase();
        String styleClass;
        if (key.isEmpty()) {
            styleClass = "tag-default";
        } else if (key.contains("bug") || key.contains("fix")) {
            styleClass = "tag-bug";
        } else if (key.contains("ui") || key.contains("ux") || key.contains("design")) {
            styleClass = "tag-uiux";
        } else if (key.contains("dev") || key.contains("ops") || key.contains("infra")) {
            styleClass = "tag-devops";
        } else if (key.contains("feat")) {
            styleClass = "tag-feature";
        } else {
            styleClass = "tag-default";
        }

        categoryTag.getStyleClass().add(styleClass);
        categoryTag.setText(key.isEmpty() ? "GENERAL" : category.trim().toUpperCase());
    }

    // Color-codes the priority chip to match the reference design (red/teal/gray).
    private void applyPriorityStyle(Priority priority) {
        priorityLabel.getStyleClass().removeAll("priority-high", "priority-medium", "priority-low", "priority-urgent");
        if (priority == null) {
            priorityLabel.setText("");
            return;
        }
        switch (priority) {
            case URGENT -> {
                priorityLabel.getStyleClass().add("priority-urgent");
                priorityLabel.setText("● Urgent");
            }
            case HIGH -> {
                priorityLabel.getStyleClass().add("priority-high");
                priorityLabel.setText("● High");
            }
            case MEDIUM -> {
                priorityLabel.getStyleClass().add("priority-medium");
                priorityLabel.setText("● Medium");
            }
            case LOW -> {
                priorityLabel.getStyleClass().add("priority-low");
                priorityLabel.setText("● Low");
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + priority);
        }
    }

    /** Shows the due date and flags it red when the task is overdue and not yet done. */
    private void applyDueDateStyle(LocalDate dueDate, Status status) {
        dueDateLabel.getStyleClass().remove("due-date-overdue");
        if (dueDate == null) {
            dueDateLabel.setText("");
            return;
        }
        dueDateLabel.setText("Due " + dueDate.format(DUE_DATE_FORMAT));
        boolean overdue = dueDate.isBefore(LocalDate.now()) && status != Status.DONE;
        if (overdue) {
            dueDateLabel.getStyleClass().add("due-date-overdue");
        }
    }
}
