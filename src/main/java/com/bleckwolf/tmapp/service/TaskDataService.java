package com.bleckwolf.tmapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bleckwolf.tmapp.model.Task;
import com.bleckwolf.tmapp.model.TaskList;

/**
 * Service class for persisting task data to a SQLite database.
 * Implements proper resource management and error handling.
 */
public class TaskDataService {
    private static final Logger logger = LoggerFactory.getLogger(TaskDataService.class);
    private static final String CREATE_TABLE_SQL = 
        "CREATE TABLE IF NOT EXISTS tasks (" +
        "id TEXT PRIMARY KEY," +
        "title TEXT NOT NULL," +
        "description TEXT," +
        "priority TEXT NOT NULL," +
        "due_date TEXT NOT NULL," +
        "completed INTEGER NOT NULL," +
        "category TEXT NOT NULL)";
    
    private static final String SAVE_TASK_SQL =
        "INSERT OR REPLACE INTO tasks (id, title, description, priority, due_date, completed, category) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String LOAD_TASKS_SQL = 
        "SELECT id, title, description, priority, due_date, completed, category FROM tasks";

    private static final String DELETE_TASK_SQL = 
        "DELETE FROM tasks WHERE id = ?";

    private final String dbUrl;

    /**
     * Creates a new TaskDataService with default database location.
     */
    public TaskDataService() {
        this("jdbc:sqlite:taskmanager.db");
    }

    /**
     * Creates a new TaskDataService with a custom database location.
     * @param dbUrl The JDBC URL for the database
     */
    public TaskDataService(String dbUrl) {
        this.dbUrl = dbUrl;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(CREATE_TABLE_SQL);
            logger.info("Database initialized successfully");
            
        } catch (SQLException e) {
            logger.error("Database initialization failed", e);
            throw new DataServiceException("Failed to initialize database", e);
        }
    }

    /**
     * Saves a task to the database (insert or update).
     * @param task The task to persist
     * @return true if operation succeeded
     * @throws DataServiceException if database operation fails
     */
    public boolean saveTask(Task task) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(SAVE_TASK_SQL)) {
            
            setTaskParameters(pstmt, task);
            int rowsAffected = pstmt.executeUpdate();
            
            logger.debug("Saved task {} ({} rows affected)", task.getId(), rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Failed to save task {}", task.getId(), e);
            throw new DataServiceException("Failed to save task", e);
        }
    }

    private void setTaskParameters(PreparedStatement pstmt, Task task) throws SQLException {
        pstmt.setString(1, task.getId());
        pstmt.setString(2, task.getTitle());
        pstmt.setString(3, task.getDescription());
        pstmt.setString(4, task.getPriority().name());
        pstmt.setString(5, task.getDueDate().toString());
        pstmt.setInt(6, task.isCompleted() ? 1 : 0);
        pstmt.setString(7, task.getCategory());
    }

    /**
     * Loads all tasks from the database.
     * @return TaskList containing all persisted tasks
     * @throws DataServiceException if database operation fails
     */
    public TaskList loadTasks() {
        final TaskList taskList = new TaskList("All Tasks");
        
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(LOAD_TASKS_SQL)) {
            
            while (rs.next()) {
                taskList.addTask(createTaskFromResultSet(rs));
            }
            
            logger.info("Loaded {} tasks from database", taskList.getTaskCount());
            return taskList;
            
        } catch (SQLException e) {
            logger.error("Failed to load tasks", e);
            throw new DataServiceException("Failed to load tasks", e);
        }
    }

    private Task createTaskFromResultSet(ResultSet rs) throws SQLException {
        Task task = new Task(
            rs.getString("title"),
            rs.getString("description"),
            Task.Priority.valueOf(rs.getString("priority")),
            LocalDate.parse(rs.getString("due_date"))
        );
        
        // Use package-private setter instead of reflection
        task.setId(rs.getString("id"));
        task.setCompleted(rs.getInt("completed") == 1);
        task.setCategory(rs.getString("category"));
        
        return task;
    }

    /**
     * Deletes a task from the database.
     * @param taskId The ID of the task to remove
     * @return true if operation succeeded
     * @throws DataServiceException if database operation fails
     */
    public boolean deleteTask(String taskId) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(DELETE_TASK_SQL)) {
            
            pstmt.setString(1, taskId);
            int rowsAffected = pstmt.executeUpdate();
            
            logger.debug("Deleted task {} ({} rows affected)", taskId, rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Failed to delete task {}", taskId, e);
            throw new DataServiceException("Failed to delete task", e);
        }
    }

    /**
     * Custom exception for data service operations.
     */
    public static class DataServiceException extends RuntimeException {
        public DataServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}