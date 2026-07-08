/**
 * @file DatabaseUtil.java
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
package com.dailydash.util;

// ---------- IMPORTS
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

// ---------- CLASS: DatabaseUtil
public class DatabaseUtil {
    // The database will be saved in the "data" folder at the root of your project
    private static final String DB_DIR = "data";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + "/dailydash.db";

    public static Connection getConnection() throws SQLException {
        // Ensure the data directory exists before connecting
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             InputStream is = DatabaseUtil.class.getResourceAsStream("/db/schema.sql")) {

            // Read the SQL file if not null
            //  and execute it
            if (is != null) {
                try (
                Scanner scanner = new Scanner(is).useDelimiter(";")) {
                    while (scanner.hasNext()) {
                        String sql = scanner.next().trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                        }
                    }
                }
                System.out.println("Database initialized successfully.");
            } else {
                System.err.println("Error: Could not find /db/schema.sql");
            }

            // Perform migration for existing databases
            migrateDatabase(conn);
        } catch (Exception e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }

    private static void migrateDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check columns in tasks table
            boolean hasPosition = false;
            boolean hasProjectId = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(tasks)")) {
                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("position".equals(columnName)) {
                        hasPosition = true;
                    } else if ("project_id".equals(columnName)) {
                        hasProjectId = true;
                    }
                }
            }

            if (!hasPosition) {
                stmt.execute("ALTER TABLE tasks ADD COLUMN position INTEGER DEFAULT 0");
                System.out.println("Migration: Added 'position' column to tasks.");
            }

            if (!hasProjectId) {
                stmt.execute("ALTER TABLE tasks ADD COLUMN project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE");
                System.out.println("Migration: Added 'project_id' column to tasks.");
            }

            // Check columns in projects table
            boolean hasIsStarred = false;
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(projects)")) {
                while (rs.next()) {
                    if ("is_starred".equals(rs.getString("name"))) {
                        hasIsStarred = true;
                        break;
                    }
                }
            }
            if (!hasIsStarred) {
                stmt.execute("ALTER TABLE projects ADD COLUMN is_starred INTEGER DEFAULT 0");
                System.out.println("Migration: Added 'is_starred' column to projects.");
            }

            // Ensure the settings table exists
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT NOT NULL)");

            // Ensure the automations table exists
            stmt.execute("CREATE TABLE IF NOT EXISTS automations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE, " +
                    "name TEXT NOT NULL, " +
                    "trigger_type TEXT NOT NULL, " +
                    "trigger_value TEXT, " +
                    "action_type TEXT NOT NULL, " +
                    "action_value TEXT, " +
                    "enabled INTEGER DEFAULT 1, " +
                    "rule_json TEXT)");

            // Ensure at least one default project exists if table is empty
            try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM projects")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO projects (id, name, is_starred) VALUES (1, 'Default Board', 1)");
                }
            }

            // Associate any task having a null/invalid project_id with the default project
            stmt.execute("UPDATE tasks SET project_id = 1 WHERE project_id IS NULL");
        } catch (SQLException e) {
            System.err.println("Database migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
