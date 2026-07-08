/**
 * @file AutomationEngine.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Event-Driven Automation Engine.
 *
 * @description
 * Evaluates triggers and executes corresponding actions automatically
 * when tasks are created, updated, or moved between columns.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.service;

// ---------- IMPORTS
import com.dailydash.model.AutomationRule;
import com.dailydash.model.Priority;
import com.dailydash.model.Status;
import com.dailydash.model.Task;
import com.dailydash.model.TaskList;

import java.util.List;

// ---------- CLASS: AutomationEngine
public class AutomationEngine {
    private final TaskDataService dataService;

    public AutomationEngine(TaskDataService dataService) {
        this.dataService = dataService;
    }

    public boolean evaluateOnTaskCreated(Task task, TaskList taskList) {
        return evaluateRules(task, "TASK_CREATED", task.getStatus().name(), taskList);
    }

    public boolean evaluateOnStatusChanged(Task task, Status oldStatus, Status newStatus, TaskList taskList) {
        return evaluateRules(task, "STATUS_CHANGED", newStatus.name(), taskList);
    }

    public boolean evaluateOnTaskUpdated(Task task, TaskList taskList) {
        return evaluateRules(task, "TASK_UPDATED", task.getStatus().name(), taskList);
    }

    public boolean evaluateRules(Task task, String eventType, String eventValue, TaskList taskList) {
        if (task == null) return false;

        List<AutomationRule> rules = dataService.getAutomations(task.getProjectId());
        if (rules == null || rules.isEmpty()) return false;

        boolean modified = false;

        for (AutomationRule rule : rules) {
            if (!rule.isEnabled()) continue;

            boolean matches = matchesTrigger(task, rule, eventType, eventValue);
            if (matches) {
                boolean executed = executeAction(task, rule);
                if (executed) {
                    modified = true;
                }
            }
        }

        if (modified) {
            dataService.updateTask(task);
        }
        return modified;
    }

    private boolean matchesTrigger(Task task, AutomationRule rule, String eventType, String eventValue) {
        String triggerType = rule.getTriggerType();
        String triggerVal = rule.getTriggerValue();

        if ("STATUS_CHANGED".equalsIgnoreCase(triggerType)) {
            if (!"STATUS_CHANGED".equalsIgnoreCase(eventType) && !"TASK_CREATED".equalsIgnoreCase(eventType)) {
                return false;
            }
            return "ANY".equalsIgnoreCase(triggerVal) || (triggerVal != null && (triggerVal.equalsIgnoreCase(eventValue) || triggerVal.equalsIgnoreCase(task.getStatus().name())));
        } else if ("TASK_CREATED".equalsIgnoreCase(triggerType)) {
            return "TASK_CREATED".equalsIgnoreCase(eventType);
        } else if ("PRIORITY_IS".equalsIgnoreCase(triggerType)) {
            return task.getPriority() != null && task.getPriority().name().equalsIgnoreCase(triggerVal);
        } else if ("CATEGORY_IS".equalsIgnoreCase(triggerType)) {
            return task.getCategory() != null && task.getCategory().equalsIgnoreCase(triggerVal);
        }
        return false;
    }

    private boolean executeAction(Task task, AutomationRule rule) {
        String actionType = rule.getActionType();
        String actionVal = rule.getActionValue();

        if (actionVal == null) return false;

        try {
            if ("SET_STATUS".equalsIgnoreCase(actionType)) {
                Status targetStatus = Status.valueOf(actionVal.toUpperCase());
                if (task.getStatus() != targetStatus) {
                    task.setStatus(targetStatus);
                    return true;
                }
            } else if ("SET_PRIORITY".equalsIgnoreCase(actionType)) {
                Priority targetPriority = Priority.valueOf(actionVal.toUpperCase());
                if (task.getPriority() != targetPriority) {
                    task.setPriority(targetPriority);
                    return true;
                }
            } else if ("APPEND_TITLE".equalsIgnoreCase(actionType)) {
                if (!task.getTitle().contains(actionVal)) {
                    task.setTitle(task.getTitle() + " " + actionVal);
                    return true;
                }
            } else if ("SET_CATEGORY".equalsIgnoreCase(actionType)) {
                if (!actionVal.equals(task.getCategory())) {
                    task.setCategory(actionVal);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing automation action: " + e.getMessage());
        }
        return false;
    }
}
