/**
 * @file Task.java
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
 * @updated 10/07/2026 @updated 07/07/2026
 */
// ---------- PACKAGE
package com.dailydash.model;

// ---------- IMPORTS
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

// ---------- CLASS: Task
public class Task {
    // Properties for JavaFX
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty title = new SimpleStringProperty(this, "title");
    private final StringProperty description = new SimpleStringProperty(this, "description");
    private final StringProperty category = new SimpleStringProperty(this, "category");
    private final ObjectProperty<Priority> priority = new SimpleObjectProperty<>(this, "priority");
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>(this, "dueDate");
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(this, "status");
    private final IntegerProperty position = new SimpleIntegerProperty(this, "position", 0);
    private final IntegerProperty projectId = new SimpleIntegerProperty(this, "projectId", 1);

    public Task() {}

    // Constructor w/ parameters
    public Task(String title, String description, String category, Priority priority, LocalDate dueDate, Status status) {
        setTitle(title);
        setDescription(description);
        setCategory(category);
        setPriority(priority);
        setDueDate(dueDate);
        setStatus(status);
        setPosition(0);
        setProjectId(1);
    }

    public Task(String title, String description, String category, Priority priority, LocalDate dueDate, Status status, int position, int projectId) {
        setTitle(title);
        setDescription(description);
        setCategory(category);
        setPriority(priority);
        setDueDate(dueDate);
        setStatus(status);
        setPosition(position);
        setProjectId(projectId);
    }

    // Getters and Setters for JavaFX properties
    public int getId() {
        return id.get();
    }
    public void setId(int value) {
        id.set(value);
    }
    public IntegerProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }
    public void setTitle(String value) {
        title.set(value);
    }
    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }
    public void setDescription(String value) {
        description.set(value);
    }
    public StringProperty descriptionProperty() {
        return description;
    }

    public String getCategory() {
        return category.get();
    }
    public void setCategory(String value) {
        category.set(value);
    }
    public StringProperty categoryProperty() {
        return category;
    }

    public Priority getPriority() {
        return priority.get();
    }
    public void setPriority(Priority value) {
        priority.set(value);
    }
    public ObjectProperty<Priority> priorityProperty() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }
    public void setDueDate(LocalDate value) {
        dueDate.set(value);
    }
    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    public Status getStatus() {
        return status.get();
    }
    public void setStatus(Status value) {
        status.set(value);
    }
    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public int getPosition() {
        return position.get();
    }
    public void setPosition(int value) {
        position.set(value);
    }
    public IntegerProperty positionProperty() {
        return position;
    }

    public int getProjectId() {
        return projectId.get();
    }
    public void setProjectId(int value) {
        projectId.set(value);
    }
    public IntegerProperty projectIdProperty() {
        return projectId;
    }
}
