/**
 * @file schema.sql
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Database schema definition for DailyDash.
 *
 * @description
 * Defines the SQLite schema for tasks, projects, and settings.
 *
 * @since 07/07/2026
 * @updated 08/07/2026
 */
-- Projects Table
CREATE TABLE IF NOT EXISTS projects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    is_starred INTEGER DEFAULT 0
);

-- Tasks Table
CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT,
    priority TEXT NOT NULL,
    due_date TEXT,
    status TEXT NOT NULL,
    position INTEGER DEFAULT 0,
    project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE
);

-- Settings Table
CREATE TABLE IF NOT EXISTS settings (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

-- Automations Table (Event-Driven Triggers -> Actions)
CREATE TABLE IF NOT EXISTS automations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    trigger_type TEXT NOT NULL,
    trigger_value TEXT,
    action_type TEXT NOT NULL,
    action_value TEXT,
    enabled INTEGER DEFAULT 1,
    rule_json TEXT
);
