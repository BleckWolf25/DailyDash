package com.bleckwolf.tmapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a collection of tasks with operations for adding, removing,
 * and filtering tasks.
*/

public class TaskList {
    private final List<Task> tasks;
    private final String name;

    /**
     * Creates a new task list with the specified name.
     *
     * @param name The name of this task list
     */
    public TaskList(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add
     * @return true if the task was added successfully
     */
    public boolean addTask(Task task) {
        if (task == null) {
            return false;
        }
        return tasks.add(task);
    }

    /**
     * Removes a task from the list.
     *
     * @param task The task to remove
     * @return true if the task was removed successfully
     */
    public boolean removeTask(Task task) {
        return tasks.remove(task);
    }

    /**
     * Removes a task by its ID.
     *
     * @param taskId The ID of the task to remove
     * @return true if the task was removed successfully
     */
    public boolean removeTaskById(String taskId) {
        return tasks.removeIf(task -> task.getId().equals(taskId));
    }

    /**
     * Gets all tasks in this list.
     *
     * @return A list of all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // Return a copy to prevent external modification
    }

    /**
     * Gets tasks that match the completed status.
     *
     * @param completed The completed status to match
     * @return A list of tasks with the specified completed status
     */
    public List<Task> getTasksByCompletionStatus(boolean completed) {
        return tasks.stream()
                .filter(task -> task.isCompleted() == completed)
                .collect(Collectors.toList());
    }

    /**
     * Gets tasks with the specified priority.
     *
     * @param priority The priority to match
     * @return A list of tasks with the specified priority
     */
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Gets tasks in the specified category.
     *
     * @param category The category to match
     * @return A list of tasks in the specified category
     */
    public List<Task> getTasksByCategory(String category) {
        return tasks.stream()
                .filter(task -> task.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Gets overdue tasks (due date is in the past and not completed).
     *
     * @return A list of overdue tasks
     */
    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Gets the name of this task list.
     *
     * @return The name of this task list
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of tasks in this list.
     *
     * @return The number of tasks
     */
    public int getTaskCount() {
        return tasks.size();
    }
}