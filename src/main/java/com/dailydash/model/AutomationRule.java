/**
 * @file AutomationRule.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Event-driven automation rule model.
 *
 * @description
 * Represents an event rule with a Trigger condition and an Action sequence.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash.model;

// ---------- CLASS: AutomationRule
public class AutomationRule {
    private int id;
    private int projectId;
    private String name;
    private String triggerType;
    private String triggerValue;
    private String actionType;
    private String actionValue;
    private boolean enabled;
    private String ruleJson;

    public AutomationRule() {
        this.enabled = true;
    }

    public AutomationRule(int id, int projectId, String name, String triggerType, String triggerValue,
                          String actionType, String actionValue, boolean enabled, String ruleJson) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.triggerType = triggerType;
        this.triggerValue = triggerValue;
        this.actionType = actionType;
        this.actionValue = actionValue;
        this.enabled = enabled;
        this.ruleJson = ruleJson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionValue() {
        return actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRuleJson() {
        return ruleJson;
    }

    public void setRuleJson(String ruleJson) {
        this.ruleJson = ruleJson;
    }
}
