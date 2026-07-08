/**
 * @file TaskList.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Collection manager for tasks.
 *
 * @description
 * Manages an ObservableList of tasks and coordinates reordering/project sorting.
 *
 * @since 07/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.model;

// ---------- IMPORTS
import com.dailydash.service.TaskDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

// ---------- CLASS: TaskList
public class TaskList {
    private final ObservableList<Task> tasks;
    private final TaskDataService dataService;
    private int currentProjectId = 1;

    public TaskList() {
        this.dataService = new TaskDataService();
        this.tasks = FXCollections.observableArrayList();
        loadTasksFromDatabase();
    }

    public void loadTasksFromDatabase() {
        tasks.clear();
        tasks.addAll(dataService.getAllTasks(currentProjectId));
    }

    public int getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProjectId(int projectId) {
        this.currentProjectId = projectId;
        loadTasksFromDatabase();
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public Task getTaskById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void addTask(Task task) {
        task.setProjectId(currentProjectId);
        // Find the maximum position in the target column for the current project
        int maxPos = tasks.stream()
                .filter(t -> t.getStatus() == task.getStatus())
                .mapToInt(Task::getPosition)
                .max()
                .orElse(-1);
        task.setPosition(maxPos + 1);

        dataService.insertTask(task);
        tasks.add(task);
    }

    public void updateTask(Task task) {
        dataService.updateTask(task);
        // Trigger UI update by replacing the item in the list
        int index = tasks.indexOf(task);
        if (index >= 0) {
            tasks.set(index, task);
        }
    }

    public void deleteTask(Task task) {
        dataService.deleteTask(task.getId());
        tasks.remove(task);
    }

    /**
     * Reorders a dragged task within a status column or moves it to a target status column.
     * If targetTask is null, the task is appended to the end of the column.
     */
    public void reorderTask(Task draggedTask, Status targetStatus, Task targetTask) {
        reorderTask(draggedTask, targetStatus, targetTask, false);
    }

    /**
     * Reorders a dragged task within a status column or moves it to a target status column.
     * If targetTask is null, the task is appended to the end of the column.
     * Otherwise, it is placed before or after targetTask based on placeAfter.
     */
    public void reorderTask(Task draggedTask, Status targetStatus, Task targetTask, boolean placeAfter) {
        Status sourceStatus = draggedTask.getStatus();

        // 1. Get all tasks for the source column in this project (excluding the dragged task)
        List<Task> sourceList = tasks.stream()
                .filter(t -> t.getStatus() == sourceStatus && t.getId() != draggedTask.getId())
                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                .collect(Collectors.toList());

        // 2. Get all tasks for the target column in this project (excluding the dragged task)
        List<Task> targetList = tasks.stream()
                .filter(t -> t.getStatus() == targetStatus && t.getId() != draggedTask.getId())
                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                .collect(Collectors.toList());

        // 3. Set the new status of the dragged task
        draggedTask.setStatus(targetStatus);

        // 4. Insert the dragged task into the target list
        if (targetTask == null) {
            targetList.add(draggedTask);
        } else {
            int insertIndex = -1;
            for (int i = 0; i < targetList.size(); i++) {
                if (targetList.get(i).getId() == targetTask.getId()) {
                    insertIndex = i;
                    break;
                }
            }
            if (insertIndex != -1) {
                int targetIndex = placeAfter ? insertIndex + 1 : insertIndex;
                if (targetIndex >= targetList.size()) {
                    targetList.add(draggedTask);
                } else {
                    targetList.add(targetIndex, draggedTask);
                }
            } else {
                targetList.add(draggedTask);
            }
        }

        // 5. Update positions in the target list
        for (int i = 0; i < targetList.size(); i++) {
            Task t = targetList.get(i);
            t.setPosition(i);
            dataService.updateTask(t);
        }

        // 6. Update positions in the source list (if different from target list)
        if (sourceStatus != targetStatus) {
            for (int i = 0; i < sourceList.size(); i++) {
                Task t = sourceList.get(i);
                t.setPosition(i);
                dataService.updateTask(t);
            }
            if (automationEngine != null) {
                automationEngine.evaluateOnStatusChanged(draggedTask, sourceStatus, targetStatus, this);
            }
        }

        // 7. Reload tasks from database to refresh UI in correct sorted order
        loadTasksFromDatabase();
    }

    private com.dailydash.service.AutomationEngine automationEngine;

    public void setAutomationEngine(com.dailydash.service.AutomationEngine automationEngine) {
        this.automationEngine = automationEngine;
    }
}