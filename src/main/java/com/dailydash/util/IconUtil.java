/**
 * @file IconUtil.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Lightweight JavaFX Icon Utility.
 *
 * @description
 * Generates theme-friendly vector icons using JavaFX Region with -fx-shape.
 *
 * @since 08/07/2026
 * @updated 10/07/2026
 */
// ---------- PACKAGE
package com.dailydash.util;

// ---------- IMPORTS
import javafx.scene.layout.Region;
import java.util.HashMap;
import java.util.Map;

// ---------- CLASS: IconUtil
public class IconUtil {
    private static final Map<String, String> ICON_PATHS = new HashMap<>();

    static {
        ICON_PATHS.put("HOME", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z");
        ICON_PATHS.put("PROJECTS", "M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z");
        ICON_PATHS.put("BOARD", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");
        ICON_PATHS.put("AUTOMATIONS", "M7 2v11h3v9l7-12h-4l4-8z");
        ICON_PATHS.put("ANALYTICS", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-5h2v5zm4 0h-2v-3h2v3zm4 0h-2V7h2v10z");
        ICON_PATHS.put("SETTINGS", "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z");
        ICON_PATHS.put("EDIT", "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
        ICON_PATHS.put("PLUS", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        ICON_PATHS.put("STAR", "M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
        ICON_PATHS.put("SUN", "M12 7c-2.76 0-5 2.24-5 5s2.24 5 5 5 5-2.24 5-5-2.24-5-5-5zM2 13h2c.55 0 1-.45 1-1s-.45-1-1-1H2c-.55 0-1 .45-1 1s.45 1 1 1zm18 0h2c.55 0 1-.45 1-1s-.45-1-1-1h-2c-.55 0-1 .45-1 1s.45 1 1 1zM11 2v2c0 .55.45 1 1 1s1-.45 1-1V2c0-.55-.45-1-1-1s-1 .45-1 1zm0 18v2c0 .55.45 1 1 1s1-.45 1-1v-2c0-.55-.45-1-1-1s-1 .45-1 1zM5.99 4.58c-.39-.39-1.03-.39-1.41 0s-.39 1.03 0 1.41l1.06 1.06c.39.39 1.03.39 1.41 0s.39-1.03 0-1.41L5.99 4.58zm12.37 12.37c-.39-.39-1.03-.39-1.41 0-.39.39-.39 1.03 0 1.41l1.06 1.06c.39.39 1.03.39 1.41 0 .39-.39.39-1.03 0-1.41l-1.06-1.06zm1.06-10.96c.39-.39.39-1.03 0-1.41-.39-.39-1.03-.39-1.41 0l-1.06 1.06c-.39.39-.39 1.03 0 1.41s1.03.39 1.41 0l1.06-1.06zM7.05 18.36l-1.06-1.06c-.39-.39-1.03-.39-1.41 0-.39.39-.39 1.03 0 1.41l1.06 1.06c.39.39 1.03.39 1.41 0 .39-.39.39-1.03 0-1.41z");
        ICON_PATHS.put("MOON", "M12.3 2C6.57 2 1.9 6.67 1.9 12.4c0 5.73 4.67 10.4 10.4 10.4 4.39 0 8.16-2.73 9.61-6.68-.3.06-.61.1-.94.1-4.8 0-8.7-3.9-8.7-8.7 0-2.26.87-4.32 2.29-5.86C13.84 2.05 13.08 2 12.3 2z");
        ICON_PATHS.put("ARCHIVE", "M20.54 5.23l-1.39-1.68C18.88 3.21 18.47 3 18 3H6c-.47 0-.88.21-1.16.55L3.46 5.23C3.17 5.57 3 6.02 3 6.5V19c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V6.5c0-.48-.17-.93-.46-1.27zM6.24 5h11.52l.81.97H5.43l.81-.97zM5 19V8h14v11H5zm6-6.5l3.15 3.15c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41l-1.44-1.44c-.39-.39-.39-1.02 0-1.41.39-.39 1.02-.39 1.41 0L17 13v-3.5c0-.55-.45-1-1-1H8c-.55 0-1 .45-1 1v2.58l-1.15-1.15c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L7.59 15.5c.39.39 1.02.39 1.41 0L11 12.5z");
        ICON_PATHS.put("IMPORT", "M9 16h6v-6h4l-7-7-7 7h4v6zm-4 2h14v2H5v-2z");
    }

    public static Region getIcon(String name, int size, String styleClass) {
        Region region = new Region();
        String path = ICON_PATHS.getOrDefault(name.toUpperCase(), "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z");
        region.setStyle("-fx-shape: \"" + path + "\";");
        region.setPrefSize(size, size);
        region.setMinSize(size, size);
        region.setMaxSize(size, size);

        region.getStyleClass().add("vector-icon");
        if (styleClass != null && !styleClass.isEmpty()) {
            region.getStyleClass().add(styleClass);
        }
        return region;
    }

    public static Region getIcon(String name, int size) {
        return getIcon(name, size, null);
    }
}
