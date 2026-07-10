/**
 * @file Launcher.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Runnable helper main class bypassing JavaFX startup issues.
 *
 * @description
 * Executable entry point for fat JAR execution without JavaFX runtime check errors.
 *
 * @since 08/07/2026
 * @updated 08/07/2026
 */
// ---------- PACKAGE
package com.dailydash;

// ---------- IMPORTS
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

// ---------- CLASS: Launcher
public class Launcher {
    public static void main(String[] args) {
        try {
            Main.main(args);
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                File logFile = new File(System.getProperty("user.home"), "dailydash-error.log");
                try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
                    pw.println("=== DailyDash Startup Error ===");
                    t.printStackTrace(pw);
                }
                JOptionPane.showMessageDialog(null,
                        "DailyDash failed to start:\n" + t.toString() +
                        "\n\nError details written to:\n" + logFile.getAbsolutePath(),
                        "DailyDash Startup Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(1);
        }
    }
}
