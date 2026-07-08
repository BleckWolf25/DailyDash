/**
 * @file TaskDataServiceTest.java
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
package com.dailydash.service;

// ---------- IMPORTS
import com.dailydash.model.Priority;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import com.dailydash.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// ---------- CLASS:
public class TaskDataServiceTest {

    private static TaskDataService service;

    @BeforeAll
    public static void setup() {
        // Ensure the database and tables are created before testing
        DatabaseUtil.initDatabase();
        service = new TaskDataService();
    }

    @Test
    public void testCrudOperations() {
        // 1. CREATE
        Task newTask = new Task("Test Task", "Testing CRUD", "Test", Priority.HIGH, LocalDate.now(), Status.TODO);
        service.insertTask(newTask);

        // Verify ID was generated
        assertTrue(newTask.getId() > 0, "Task ID should be generated after insert");

        // 2. READ
        List<Task> tasks = service.getAllTasks();
        assertFalse(tasks.isEmpty(), "Task list should not be empty");

        // Find our specific task
        Task retrievedTask = tasks.stream()
                .filter(t -> t.getId() == newTask.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedTask, "Should be able to retrieve the inserted task");
        assertEquals("Test Task", retrievedTask.getTitle());
        assertEquals(Priority.HIGH, retrievedTask.getPriority());

        // 3. UPDATE
        retrievedTask.setStatus(Status.IN_PROGRESS);
        retrievedTask.setTitle("Updated Test Task");
        service.updateTask(retrievedTask);

        // Read again to verify update
        List<Task> updatedTasks = service.getAllTasks();
        Task updatedTask = updatedTasks.stream()
                .filter(t -> t.getId() == retrievedTask.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(updatedTask);
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
        assertEquals("Updated Test Task", updatedTask.getTitle());

        // 4. DELETE
        service.deleteTask(updatedTask.getId());

        // Read again to verify deletion
        List<Task> finalTasks = service.getAllTasks();
        boolean exists = finalTasks.stream().anyMatch(t -> t.getId() == updatedTask.getId());
        assertFalse(exists, "Task should be deleted from the database");
    }

    @Test
    public void testSettingsPersistence() {
        // Test upsert of setting
        service.setSetting("test_key", "test_value");
        assertEquals("test_value", service.getSetting("test_key", "default"));

        service.setSetting("test_key", "new_value");
        assertEquals("new_value", service.getSetting("test_key", "default"));

        // Test fallback to default
        assertEquals("fallback", service.getSetting("non_existent_key", "fallback"));
    }

    @Test
    public void testProjectsCrud() {
        // Read existing projects
        List<com.dailydash.model.Project> initialProjects = service.getProjects();
        int initialCount = initialProjects.size();

        String testProjectName = "Test Board " + System.currentTimeMillis();
        int newProjectId = service.insertProject(testProjectName);
        assertTrue(newProjectId > 0, "Generated project ID should be > 0");

        List<com.dailydash.model.Project> updatedProjects = service.getProjects();
        assertEquals(initialCount + 1, updatedProjects.size());
        assertTrue(updatedProjects.stream().anyMatch(p -> p.getId() == newProjectId && testProjectName.equals(p.getName())));

        // Test inserting tasks in new project
        Task taskInProject = new Task("Project Task", "Testing project scope", "Test", Priority.MEDIUM, null, Status.TODO);
        taskInProject.setProjectId(newProjectId);
        taskInProject.setPosition(5);
        service.insertTask(taskInProject);

        // Retrieve tasks for new project and default project (ID 1)
        List<Task> defaultTasks = service.getAllTasks(1);
        List<Task> projectTasks = service.getAllTasks(newProjectId);

        assertFalse(projectTasks.isEmpty());
        assertTrue(projectTasks.stream().anyMatch(t -> t.getId() == taskInProject.getId()));

        service.deleteProject(newProjectId);
        assertFalse(defaultTasks.stream().anyMatch(t -> t.getId() == taskInProject.getId()), "Task shouldn't show in default project list");

        // Clean up project
        service.deleteProject(newProjectId);
        List<com.dailydash.model.Project> finalProjects = service.getProjects();
        assertEquals(initialCount, finalProjects.size());
    }

    @Test
    public void testReorderTaskPlaceAfterAndSameColumn() {
        com.dailydash.model.TaskList taskList = new com.dailydash.model.TaskList();
        Task t1 = new Task("Task 1", "Desc 1", "Cat", Priority.LOW, null, Status.TODO);
        Task t2 = new Task("Task 2", "Desc 2", "Cat", Priority.LOW, null, Status.TODO);
        Task t3 = new Task("Task 3", "Desc 3", "Cat", Priority.LOW, null, Status.TODO);

        taskList.addTask(t1);
        taskList.addTask(t2);
        taskList.addTask(t3);

        // Reorder t1 below t2 (placeAfter = true)
        taskList.reorderTask(t1, Status.TODO, t2, true);

        // After reorder, positions should be t2 (0), t1 (1), t3 (2)
        List<Task> todoTasks = service.getAllTasks(taskList.getCurrentProjectId()).stream()
                .filter(t -> t.getStatus() == Status.TODO)
                .toList();

        int posT2 = todoTasks.stream().filter(t -> t.getId() == t2.getId()).findFirst().get().getPosition();
        int posT1 = todoTasks.stream().filter(t -> t.getId() == t1.getId()).findFirst().get().getPosition();
        assertTrue(posT2 < posT1, "t2 position (" + posT2 + ") should be before t1 (" + posT1 + ")");

        // Clean up
        service.deleteTask(t1.getId());
        service.deleteTask(t2.getId());
        service.deleteTask(t3.getId());
    }

    @Test
    public void testStarredProjects() {
        int projId = service.insertProject("Test Starred Project", false);
        assertTrue(projId > 0);

        service.toggleProjectStar(projId, true);
        boolean isStarred = service.getProjects().stream()
                .filter(p -> p.getId() == projId)
                .findFirst()
                .map(com.dailydash.model.Project::isStarred)
                .orElse(false);

        assertTrue(isStarred, "Project should be marked as starred");

        service.deleteProject(projId);
    }

    @Test
    public void testAutomationEngine() {
        int projId = service.insertProject("Test Automation Board");
        com.dailydash.model.AutomationRule rule = new com.dailydash.model.AutomationRule(
                0, projId, "Auto Low Priority on Done",
                "STATUS_CHANGED", "DONE",
                "SET_PRIORITY", "LOW",
                true, "{}"
        );
        service.insertAutomation(rule);

        com.dailydash.model.Task task = new com.dailydash.model.Task("Test Task", "Desc", "General", com.dailydash.model.Priority.HIGH, null, Status.TODO, 0, projId);
        service.insertTask(task);

        com.dailydash.service.AutomationEngine engine = new com.dailydash.service.AutomationEngine(service);
        boolean executed = engine.evaluateOnStatusChanged(task, Status.TODO, Status.DONE, null);

        assertTrue(executed, "Automation rule should have executed");
        assertEquals(com.dailydash.model.Priority.LOW, task.getPriority(), "Task priority should be changed to LOW by automations");

        // Clean up
        service.deleteTask(task.getId());
        service.deleteProject(projId);
    }

    @Test
    public void testFxmlLoading() throws Exception {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        final Exception[] err = new Exception[1];
        javafx.application.Platform.runLater(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                loader.load();
            } catch (Exception ex) {
                err[0] = ex;
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        if (err[0] != null) {
            err[0].printStackTrace();
        }
        assertNull(err[0], "main.fxml should load without any FXMLLoader exception");
    }

    @Test
    public void testUrgentPriorityTaskPersistence() {
        Task urgentTask = new Task("Urgent Task", "ASAP desc", "Work", Priority.URGENT, LocalDate.now(), Status.TODO);
        service.insertTask(urgentTask);
        assertTrue(urgentTask.getId() > 0);

        List<Task> tasks = service.getAllTasks();
        Task retrieved = tasks.stream().filter(t -> t.getId() == urgentTask.getId()).findFirst().orElse(null);
        assertNotNull(retrieved);
        assertEquals(Priority.URGENT, retrieved.getPriority());

        // Clean up
        service.deleteTask(urgentTask.getId());
    }

    @Test
    public void testIconUtilThemeAwareness() {
        javafx.scene.layout.Region icon = com.dailydash.util.IconUtil.getIcon("HOME", 16, "custom-style");
        assertNotNull(icon);
        assertTrue(icon.getStyleClass().contains("vector-icon"));
        assertTrue(icon.getStyleClass().contains("custom-style"));
        assertEquals(16.0, icon.getPrefWidth());
        assertEquals(16.0, icon.getPrefHeight());
    }
}
