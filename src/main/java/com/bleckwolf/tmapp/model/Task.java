package com.bleckwolf.tmapp.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Represents a task in the task management system.
 * Each task has a unique identifier, title, description, priority,
 * due date, and completion status.
 */
public class Task {
    private String id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private final BooleanProperty completed = new SimpleBooleanProperty();
    private String category;

    /**
     * Priority levels for tasks
     */
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    /**
     * Creates a new task with default values.
     */
    public Task() {
        this("", "", Priority.MEDIUM, LocalDate.now().plusDays(1));
    }

    /**
     * Creates a new task with the specified attributes.
     *
     * @param title Task title
     * @param description Task description
     * @param priority Task priority
     * @param dueDate Task due date
     */
    public Task(String title, String description, Priority priority, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed.set(false);
        this.category = "General";
    }

    // JavaFX Properties
    public BooleanProperty completedProperty() {
        return completed;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Checks if the task is overdue.
     *
     * @return true if the task is not completed and the due date is before today
     */
    public boolean isOverdue() {
        return !isCompleted() && dueDate.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", completed=" + isCompleted() +
                ", category='" + category + '\'' +
                '}';
    }
}