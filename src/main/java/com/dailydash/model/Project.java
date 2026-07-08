/**
 * @file Project.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Project model representing a board/project.
 *
 * @description
 * Simple model for switching task boards, supporting ComboBox display via toString().
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.model;

// ---------- CLASS: Project
public class Project {
    private final int id;
    private final String name;
    private boolean starred;

    public Project(int id, String name) {
        this(id, name, false);
    }

    public Project(int id, String name, boolean starred) {
        this.id = id;
        this.name = name;
        this.starred = starred;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    @Override
    public String toString() {
        return (starred ? "★ " : "") + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
