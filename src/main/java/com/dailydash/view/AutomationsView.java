/**
 * @file AutomationsView.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Event-Driven Automations Page.
 *
 * @description
 * Provides a No-Code Rule Builder and JSON rule syntax editor for creating and managing event-driven automations.
 *
 * @since 08/07/2026
 *
 */
// ---------- PACKAGE
package com.dailydash.view;

// ---------- IMPORTS
import com.dailydash.model.AutomationRule;
import com.dailydash.model.Project;
import com.dailydash.service.TaskDataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

// ---------- CLASS: AutomationsView
public class AutomationsView {

    public static ScrollPane build(Project activeProject, TaskDataService dataService, Runnable onRefresh) {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.getStyleClass().add("page-container");

        // Header
        VBox headerBox = new VBox(4);
        Label title = new Label("Event-Driven Automations (" + activeProject.getName() + ")");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Build Event-Driven automation rules to eliminate repetitive tasks.");
        subtitle.getStyleClass().add("page-subtitle");
        headerBox.getChildren().addAll(title, subtitle);

        // Visual No-Code Rule Builder Box
        VBox builderBox = new VBox(14);
        builderBox.setPadding(new Insets(20));
        builderBox.getStyleClass().add("automation-builder-card");

        Label builderTitle = new Label("Visual Rule Builder");
        builderTitle.getStyleClass().add("section-title");

        HBox ruleRow = new HBox(12);
        ruleRow.setAlignment(Pos.CENTER_LEFT);

        Label whenLabel = new Label("WHEN");
        whenLabel.getStyleClass().add("rule-keyword");

        ComboBox<String> triggerTypeBox = new ComboBox<>();
        triggerTypeBox.getItems().addAll("STATUS_CHANGED", "TASK_CREATED", "PRIORITY_IS", "CATEGORY_IS");
        triggerTypeBox.setValue("STATUS_CHANGED");

        TextField triggerValueField = new TextField("DONE");
        triggerValueField.setPromptText("Trigger value (e.g. DONE, HIGH)");
        triggerValueField.setPrefWidth(130);

        Label thenLabel = new Label("THEN");
        thenLabel.getStyleClass().add("rule-keyword");

        ComboBox<String> actionTypeBox = new ComboBox<>();
        actionTypeBox.getItems().addAll("SET_PRIORITY", "SET_STATUS", "APPEND_TITLE", "SET_CATEGORY");
        actionTypeBox.setValue("SET_PRIORITY");

        TextField actionValueField = new TextField("LOW");
        actionValueField.setPromptText("Action value (e.g. LOW)");
        actionValueField.setPrefWidth(130);

        ruleRow.getChildren().addAll(whenLabel, triggerTypeBox, triggerValueField, thenLabel, actionTypeBox, actionValueField);

        HBox nameAndAddRow = new HBox(12);
        nameAndAddRow.setAlignment(Pos.CENTER_LEFT);

        TextField ruleNameField = new TextField();
        ruleNameField.setPromptText("Rule Name (e.g., Auto-deprioritize completed tasks)");
        HBox.setHgrow(ruleNameField, Priority.ALWAYS);

        Button addRuleBtn = new Button("Create Rule");
        addRuleBtn.getStyleClass().add("primary-btn");
        addRuleBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("PLUS", 14));
        addRuleBtn.setOnAction(e -> {
            String name = ruleNameField.getText().trim();
            if (name.isEmpty()) {
                name = "Rule: When " + triggerTypeBox.getValue() + " -> " + actionTypeBox.getValue();
            }
            String json = "{\"trigger\":\"" + triggerTypeBox.getValue() + "\",\"triggerValue\":\"" + triggerValueField.getText() +
                    "\",\"action\":\"" + actionTypeBox.getValue() + "\",\"actionValue\":\"" + actionValueField.getText() + "\"}";

            AutomationRule rule = new AutomationRule(
                    0, activeProject.getId(), name,
                    triggerTypeBox.getValue(), triggerValueField.getText().trim(),
                    actionTypeBox.getValue(), actionValueField.getText().trim(),
                    true, json
            );

            dataService.insertAutomation(rule);
            onRefresh.run();
        });

        nameAndAddRow.getChildren().addAll(ruleNameField, addRuleBtn);
        builderBox.getChildren().addAll(builderTitle, ruleRow, nameAndAddRow);

        // JSON / Syntax Editor Section
        VBox jsonSection = new VBox(10);
        jsonSection.setPadding(new Insets(16));
        jsonSection.getStyleClass().add("automation-json-card");

        Label jsonTitle = new Label("Advanced JSON Rule Syntax Editor");
        jsonTitle.getStyleClass().add("section-subtitle");

        TextArea jsonEditor = new TextArea("{\n  \"name\": \"Custom Advanced Rule\",\n  \"trigger_type\": \"STATUS_CHANGED\",\n  \"trigger_value\": \"DONE\",\n  \"action_type\": \"APPEND_TITLE\",\n  \"action_value\": \"[ARCHIVED]\"\n}");
        jsonEditor.setPrefRowCount(16);
        jsonEditor.setPrefHeight(340);
        jsonEditor.getStyleClass().add("json-editor-area");

        Button importJsonBtn = new Button("Import Rule from JSON");
        importJsonBtn.getStyleClass().add("btn-secondary");
        importJsonBtn.setGraphic(com.dailydash.util.IconUtil.getIcon("IMPORT", 14));
        importJsonBtn.setOnAction(e -> {
            // Simple parse for user rule
            String text = jsonEditor.getText();
            AutomationRule rule = new AutomationRule(
                    0, activeProject.getId(), "Custom JSON Rule",
                    "STATUS_CHANGED", "DONE",
                    "APPEND_TITLE", "[ARCHIVED]",
                    true, text
            );
            dataService.insertAutomation(rule);
            onRefresh.run();
        });

        jsonSection.getChildren().addAll(jsonTitle, jsonEditor, importJsonBtn);

        // Active Rules List
        VBox listSection = new VBox(12);
        Label listTitle = new Label("Active Automation Rules");
        listTitle.getStyleClass().add("section-title");

        List<AutomationRule> rules = dataService.getAutomations(activeProject.getId());
        if (rules.isEmpty()) {
            Label emptyList = new Label("No automation rules configured for this project yet.");
            emptyList.getStyleClass().add("empty-message");
            listSection.getChildren().addAll(listTitle, emptyList);
        } else {
            listSection.getChildren().add(listTitle);
            for (AutomationRule r : rules) {
                listSection.getChildren().add(buildRuleCard(r, dataService, onRefresh));
            }
        }

        root.getChildren().addAll(headerBox, builderBox, jsonSection, listSection);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }

    private static HBox buildRuleCard(AutomationRule r, TaskDataService dataService, Runnable onRefresh) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.getStyleClass().add("automation-rule-row");

        VBox info = new VBox(4);
        Label name = new Label(r.getName());
        name.getStyleClass().add("rule-card-name");

        Label desc = new Label("When " + r.getTriggerType() + " (" + r.getTriggerValue() + ") → Then " + r.getActionType() + " (" + r.getActionValue() + ")");
        desc.getStyleClass().add("rule-card-desc");

        info.getChildren().addAll(name, desc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        CheckBox enableCheck = new CheckBox("Enabled");
        enableCheck.setSelected(r.isEnabled());
        enableCheck.setOnAction(e -> {
            r.setEnabled(enableCheck.isSelected());
            dataService.updateAutomation(r);
        });

        Button delBtn = new Button("Delete");
        delBtn.getStyleClass().add("icon-delete-button");
        delBtn.setOnAction(e -> {
            dataService.deleteAutomation(r.getId());
            onRefresh.run();
        });

        card.getChildren().addAll(info, spacer, enableCheck, delBtn);
        return card;
    }
}
