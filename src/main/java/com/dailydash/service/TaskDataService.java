/**
 * @file TaskDataService.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary SQLite CRUD operations and service layer for DailyDash.
 *
 * @description
 * Handles inserting, reading, updating, and deleting tasks, projects, and settings.
 *
 * @since 07/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.service;

// ---------- IMPORTS
import com.dailydash.model.Priority;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import com.dailydash.model.Project;
import com.dailydash.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ---------- CLASS: TaskDataService
public class TaskDataService {

    // CREATE TASK
    public void insertTask(Task task) {
        String sql = "INSERT INTO tasks(title, description, category, priority, due_date, status, position, project_id) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getCategory());
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getDueDate() != null ? task.getDueDate().toString() : null);
            pstmt.setString(6, task.getStatus().name());
            pstmt.setInt(7, task.getPosition());
            pstmt.setInt(8, task.getProjectId());
            
            pstmt.executeUpdate();

            // Retrieve the auto-generated ID and update the Task object
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting task: " + e.getMessage());
        }
    }

    // READ TASKS BY PROJECT
    public List<Task> getAllTasks(int projectId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY position ASC, id ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setCategory(rs.getString("category"));
                    try {
                        task.setPriority(Priority.valueOf(rs.getString("priority")));
                    } catch (Exception ex) {
                        task.setPriority(Priority.MEDIUM);
                    }
                    
                    String dueDateStr = rs.getString("due_date");
                    if (dueDateStr != null && !dueDateStr.isEmpty()) {
                        task.setDueDate(LocalDate.parse(dueDateStr));
                    }
                    
                    task.setStatus(Status.valueOf(rs.getString("status")));
                    task.setPosition(rs.getInt("position"));
                    task.setProjectId(rs.getInt("project_id"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tasks for project " + projectId + ": " + e.getMessage());
        }
        return tasks;
    }

    // Deprecated read method to preserve signatures if called without parameters
    public List<Task> getAllTasks() {
        return getAllTasks(1); // Default project ID
    }

    // UPDATE TASK
    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, category = ?, priority = ?, due_date = ?, status = ?, position = ?, project_id = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getCategory());
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getDueDate() != null ? task.getDueDate().toString() : null);
            pstmt.setString(6, task.getStatus().name());
            pstmt.setInt(7, task.getPosition());
            pstmt.setInt(8, task.getProjectId());
            pstmt.setInt(9, task.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    // DELETE TASK
    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
        }
    }

    // PROJECTS CRUD
    public List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects ORDER BY is_starred DESC, id ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                boolean starred = false;
                try {
                    starred = rs.getInt("is_starred") == 1;
                } catch (SQLException ignored) {}
                projects.add(new Project(rs.getInt("id"), rs.getString("name"), starred));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving projects: " + e.getMessage());
        }
        return projects;
    }

    public void seedShowcaseProjectIfEmpty() {
        List<Project> projects = getProjects();
        if (!projects.isEmpty()) {
            // If there's only one project named "Default Board" with zero tasks, turn it into Showcase Board
            if (projects.size() == 1 && "Default Board".equals(projects.get(0).getName())) {
                List<Task> existingTasks = getAllTasks(projects.get(0).getId());
                if (existingTasks.isEmpty()) {
                    deleteProject(projects.get(0).getId());
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        int projId = insertProject("Showcase Board (Welcome!)", true);
        if (projId <= 0) return;

        // Task 1 (TODO)
        Task t1 = new Task(
                "👋 Welcome to DailyDash! Click me",
                "# Welcome to DailyDash!\nDailyDash is your high-performance, locally encrypted engineering productivity dashboard built with **JavaFX** and **SQLite**.\n\n### Quick Navigation\n- **Single-click** a card to view formatted details\n- **Double-click** a card to edit instantly\n- **Drag & Drop** cards across columns or reorder inside columns!",
                "Onboarding", Priority.HIGH, LocalDate.now().plusDays(2), Status.TODO
        );
        t1.setProjectId(projId);
        t1.setPosition(0);
        insertTask(t1);

        // Task 2 (TODO)
        Task t2 = new Task(
                "⚡ Explore Event-Driven Automations",
                "### Automate Repetitive Workflows\nHead over to the **Automations** tab on the left sidebar to code custom rules!\n\nExamples:\n- **When** status changes to `DONE` -> **Append title** `[ARCHIVED]`\n- **When** priority is `URGENT` -> **Set category** `Critical`\n- Import or Export advanced JSON rules with 1 click!",
                "Automation", Priority.URGENT, LocalDate.now().plusDays(5), Status.TODO
        );
        t2.setProjectId(projId);
        t2.setPosition(1);
        insertTask(t2);

        // Task 5 (TODO - Markdown Showcase)
        Task t5 = new Task(
                "📝 Rich Markdown Notes Supported",
                "# Markdown Features\nDailyDash parses standard Markdown:\n\n## Headers & Subheaders\nYou can organize content into sections.\n\n### Text Formatting\n- **Bold** text for emphasis\n- *Italic* text for styling\n- `Inline code` for keywords\n\n### Structured Checklists\n- [ ] Task checkbox item 1\n- [x] Completed checkbox item 2",
                "Productivity", Priority.MEDIUM, LocalDate.now().plusDays(4), Status.TODO
        );
        t5.setProjectId(projId);
        t5.setPosition(2);
        insertTask(t5);

        // Task 3 (IN_PROGRESS)
        Task t3 = new Task(
                "🎨 Try Dark Mode & Custom Themes",
                "### Sleek Syntactic Theme\nDailyDash ships with a curated **Syntactic Management** color palette designed for contrast and readability.\n\n- Toggle between **Dark Mode** and **Light Mode** anytime via the bottom-left sidebar toggle or **Settings** page.\n- Check out the **Analytics** page for beautiful SVG charts!",
                "UI/UX", Priority.MEDIUM, LocalDate.now().plusDays(3), Status.IN_PROGRESS
        );
        t3.setProjectId(projId);
        t3.setPosition(0);
        insertTask(t3);

        // Task 4 (DONE)
        Task t4 = new Task(
                "✨ Drag & Drop Reordering Supported",
                "You can drag any card to reorder your tasks or move them between **TODO**, **IN_PROGRESS**, and **DONE**.\n\n*Completed tasks can be archived anytime from the Settings page!*",
                "Productivity", Priority.LOW, LocalDate.now(), Status.DONE
        );
        t4.setProjectId(projId);
        t4.setPosition(0);
        insertTask(t4);

        // Add a Showcase Automation Rule
        com.dailydash.model.AutomationRule rule = new com.dailydash.model.AutomationRule(
                0, projId, "Auto-tag completed tasks",
                "STATUS_CHANGED", "DONE",
                "APPEND_TITLE", "[COMPLETED]",
                true, "{\"trigger\":\"STATUS_CHANGED\",\"triggerValue\":\"DONE\",\"action\":\"APPEND_TITLE\",\"actionValue\":\"[COMPLETED]\"}"
        );
        insertAutomation(rule);
    }

    public int insertProject(String name) {
        return insertProject(name, false);
    }

    public int insertProject(String name, boolean starred) {
        String sql = "INSERT INTO projects(name, is_starred) VALUES(?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, starred ? 1 : 0);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting project: " + e.getMessage());
        }
        return -1;
    }

    public void toggleProjectStar(int id, boolean starred) {
        String sql = "UPDATE projects SET is_starred = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, starred ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error starring project: " + e.getMessage());
        }
    }

    public void deleteProject(int id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting project: " + e.getMessage());
        }
    }

    // AUTOMATION RULES CRUD
    public List<com.dailydash.model.AutomationRule> getAutomations(int projectId) {
        List<com.dailydash.model.AutomationRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM automations WHERE project_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rules.add(new com.dailydash.model.AutomationRule(
                            rs.getInt("id"),
                            rs.getInt("project_id"),
                            rs.getString("name"),
                            rs.getString("trigger_type"),
                            rs.getString("trigger_value"),
                            rs.getString("action_type"),
                            rs.getString("action_value"),
                            rs.getInt("enabled") == 1,
                            rs.getString("rule_json")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving automations: " + e.getMessage());
        }
        return rules;
    }

    public int insertAutomation(com.dailydash.model.AutomationRule rule) {
        String sql = "INSERT INTO automations(project_id, name, trigger_type, trigger_value, action_type, action_value, enabled, rule_json) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rule.getProjectId());
            pstmt.setString(2, rule.getName());
            pstmt.setString(3, rule.getTriggerType());
            pstmt.setString(4, rule.getTriggerValue());
            pstmt.setString(5, rule.getActionType());
            pstmt.setString(6, rule.getActionValue());
            pstmt.setInt(7, rule.isEnabled() ? 1 : 0);
            pstmt.setString(8, rule.getRuleJson());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    rule.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting automation rule: " + e.getMessage());
        }
        return -1;
    }

    public void updateAutomation(com.dailydash.model.AutomationRule rule) {
        String sql = "UPDATE automations SET name=?, trigger_type=?, trigger_value=?, action_type=?, action_value=?, enabled=?, rule_json=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rule.getName());
            pstmt.setString(2, rule.getTriggerType());
            pstmt.setString(3, rule.getTriggerValue());
            pstmt.setString(4, rule.getActionType());
            pstmt.setString(5, rule.getActionValue());
            pstmt.setInt(6, rule.isEnabled() ? 1 : 0);
            pstmt.setString(7, rule.getRuleJson());
            pstmt.setInt(8, rule.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating automation rule: " + e.getMessage());
        }
    }

    public void deleteAutomation(int id) {
        String sql = "DELETE FROM automations WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting automation rule: " + e.getMessage());
        }
    }

    // SETTINGS METHODS
    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting setting: " + e.getMessage());
        }
        return defaultValue;
    }

    public void setSetting(String key, String value) {
        String sql = "INSERT INTO settings(key, value) VALUES(?,?) ON CONFLICT(key) DO UPDATE SET value = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setString(3, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error setting setting: " + e.getMessage());
        }
    }
}