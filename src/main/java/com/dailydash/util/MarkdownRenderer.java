/**
 * @file MarkdownRenderer.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Lightweight JavaFX Markdown Renderer.
 *
 * @description
 * Converts standard Markdown syntax (headers, bold, italic, code, bullets)
 * into beautifully styled JavaFX nodes.
 *
 * @since 08/07/2026
 *
 */
// ---------- PACKAGE
package com.dailydash.util;

// ---------- IMPORTS
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

// ---------- CLASS: MarkdownRenderer
public class MarkdownRenderer {

    public static VBox render(String markdown) {
        VBox container = new VBox(8);
        container.setPadding(new Insets(4, 0, 4, 0));

        if (markdown == null || markdown.trim().isEmpty()) {
            Label emptyLabel = new Label("No description provided.");
            emptyLabel.getStyleClass().add("md-empty-text");
            container.getChildren().add(emptyLabel);
            return container;
        }

        String[] lines = markdown.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.startsWith("### ")) {
                Label h3 = new Label(trimmed.substring(4));
                h3.getStyleClass().add("md-h3");
                container.getChildren().add(h3);
            } else if (trimmed.startsWith("## ")) {
                Label h2 = new Label(trimmed.substring(3));
                h2.getStyleClass().add("md-h2");
                container.getChildren().add(h2);
            } else if (trimmed.startsWith("# ")) {
                Label h1 = new Label(trimmed.substring(2));
                h1.getStyleClass().add("md-h1");
                container.getChildren().add(h1);
            } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                HBox bulletRow = new HBox(6);
                Label bullet = new Label("•");
                bullet.getStyleClass().add("md-bullet");
                TextFlow textFlow = renderInlineMarkdown(trimmed.substring(2));
                bulletRow.getChildren().addAll(bullet, textFlow);
                container.getChildren().add(bulletRow);
            } else {
                TextFlow p = renderInlineMarkdown(line);
                container.getChildren().add(p);
            }
        }

        return container;
    }

    private static TextFlow renderInlineMarkdown(String text) {
        TextFlow flow = new TextFlow();
        // Simple inline parser for **bold**, *italic*, and `code`
        int idx = 0;
        int len = text.length();

        while (idx < len) {
            if (idx + 1 < len && text.charAt(idx) == '*' && text.charAt(idx + 1) == '*') {
                int endIdx = text.indexOf("**", idx + 2);
                if (endIdx != -1) {
                    Text t = new Text(text.substring(idx + 2, endIdx));
                    t.setFont(Font.font(t.getFont().getFamily(), FontWeight.BOLD, 13));
                    t.getStyleClass().add("md-bold");
                    flow.getChildren().add(t);
                    idx = endIdx + 2;
                    continue;
                }
            }
            if (text.charAt(idx) == '*') {
                int endIdx = text.indexOf('*', idx + 1);
                if (endIdx != -1) {
                    Text t = new Text(text.substring(idx + 1, endIdx));
                    t.setFont(Font.font(t.getFont().getFamily(), FontPosture.ITALIC, 13));
                    t.getStyleClass().add("md-italic");
                    flow.getChildren().add(t);
                    idx = endIdx + 1;
                    continue;
                }
            }
            if (text.charAt(idx) == '`') {
                int endIdx = text.indexOf('`', idx + 1);
                if (endIdx != -1) {
                    Label codeLabel = new Label(text.substring(idx + 1, endIdx));
                    codeLabel.getStyleClass().add("md-inline-code");
                    flow.getChildren().add(codeLabel);
                    idx = endIdx + 1;
                    continue;
                }
            }

            // Find next special token
            int nextDoubleStar = text.indexOf("**", idx);
            int nextStar = text.indexOf('*', idx);
            int nextBacktick = text.indexOf('`', idx);

            int nextToken = len;
            if (nextDoubleStar != -1) {
                nextToken = Math.min(nextToken, nextDoubleStar);
            }
            if (nextStar != -1 && nextStar != nextDoubleStar) {
                nextToken = Math.min(nextToken, nextStar);
            }
            if (nextBacktick != -1) {
                nextToken = Math.min(nextToken, nextBacktick);
            }

            String normal = text.substring(idx, nextToken);
            Text t = new Text(normal);
            t.getStyleClass().add("md-text");
            flow.getChildren().add(t);
            idx = nextToken;
        }

        return flow;
    }
}
