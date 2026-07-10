/**
 * @file UpdateService.java
 *
 * @version 1.0.0
 * @author BleckWolf25
 * @license MIT
 *
 * @summary Checks for and installs application updates from GitHub Releases.
 *
 * @description
 * This service checks GitHub for the latest release of DailyDash, compares it with the current version,
 * and if a newer version is available, it downloads the release asset and launches the installer.
 *
 * @since 09/07/2026
 * @updated 10/07/2026
 */
// ---------- PACKAGE
package com.dailydash.service;

// ---------- IMPORTS
import javafx.application.Platform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;

// ---------- CLASS: UpdateService
public class UpdateService {

    public static final String CURRENT_VERSION = "1.0.0";
    public static final String GITHUB_REPO = "BleckWolf25/DailyDash";
    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest";

    public static class ReleaseInfo {
        public final String version;
        public final String tagName;
        public final String releaseNotes;
        public final String downloadUrl;
        public final String assetName;

        public ReleaseInfo(String version, String tagName, String releaseNotes, String downloadUrl, String assetName) {
            this.version = version;
            this.tagName = tagName;
            this.releaseNotes = releaseNotes;
            this.downloadUrl = downloadUrl;
            this.assetName = assetName;
        }
    }

    /**
     * Checks GitHub for the latest release asynchronously.
     */
    public static void checkForUpdatesAsync(Consumer<ReleaseInfo> onUpdateAvailable, Consumer<String> onNoUpdateOrError) {
        Thread thread = new Thread(() -> {
            try {
                ReleaseInfo info = fetchLatestRelease();
                if (info != null && isNewerVersion(info.version, CURRENT_VERSION)) {
                    Platform.runLater(() -> onUpdateAvailable.accept(info));
                } else {
                    Platform.runLater(() -> onNoUpdateOrError.accept("DailyDash is up to date (v" + CURRENT_VERSION + ")."));
                }
            } catch (Exception e) {
                Platform.runLater(() -> onNoUpdateOrError.accept("Could not check for updates: " + e.getMessage()));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static ReleaseInfo fetchLatestRelease() throws Exception {
        URL url = URI.create(LATEST_RELEASE_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "DailyDash-App");
        conn.setRequestProperty("Accept", "application/vnd.github+json");
        conn.setConnectTimeout(6000);
        conn.setReadTimeout(6000);

        if (conn.getResponseCode() != 200) {
            return null;
        }

        String json;
        try (InputStream in = conn.getInputStream(); Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            json = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }

        String tagName = extractJsonField(json, "tag_name");
        if (tagName == null) {
            return null;
        }

        String cleanVersion = tagName.startsWith("v") || tagName.startsWith("V") ? tagName.substring(1) : tagName;
        String body = extractJsonField(json, "body");
        if (body == null) {
            body = "New version available on GitHub.";
        }

        // Match OS asset extension
        String os = System.getProperty("os.name", "").toLowerCase();
        String ext1 = ".exe";
        String ext2 = ".msi";
        if (os.contains("mac")) {
            ext1 = ".dmg";
            ext2 = ".pkg";
        } else if (os.contains("nix") || os.contains("nux")) {
            ext1 = ".deb";
            ext2 = ".rpm";
        }

        String downloadUrl = null;
        String assetName = null;

        // Simple parse for browser_download_url and name inside assets
        String[] parts = json.split("\"browser_download_url\"\\s*:\\s*\"");
        for (int i = 1; i < parts.length; i++) {
            int end = parts[i].indexOf("\"");
            if (end > 0) {
                String candidateUrl = parts[i].substring(0, end);
                String lower = candidateUrl.toLowerCase();
                if (lower.endsWith(ext1) || lower.endsWith(ext2)) {
                    downloadUrl = candidateUrl;
                    int slash = candidateUrl.lastIndexOf('/');
                    assetName = slash >= 0 ? candidateUrl.substring(slash + 1) : "DailyDash-Update" + ext1;
                    break;
                }
            }
        }

        // If no OS specific asset found, fallback to GitHub releases page URL
        if (downloadUrl == null) {
            downloadUrl = "https://github.com/" + GITHUB_REPO + "/releases/latest";
            assetName = "GitHub-Release";
        }

        return new ReleaseInfo(cleanVersion, tagName, body, downloadUrl, assetName);
    }

    private static String extractJsonField(String json, String field) {
        String key = "\"" + field + "\"\\s*:\\s*\"";
        String[] split = json.split(key);
        if (split.length < 2) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String reminder = split[1];
        boolean escape = false;
        for (int i = 0; i < reminder.length(); i++) {
            char c = reminder.charAt(i);
            if (escape) {
                if (c == 'n') {
                    sb.append('\n');
                } else if (c == 'r') {
                    sb.append('\r');
                } else {
                    sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isNewerVersion(String latestVersion, String currentVersion) {
        try {
            String[] v1 = latestVersion.split("\\.");
            String[] v2 = currentVersion.split("\\.");
            int length = Math.max(v1.length, v2.length);
            for (int i = 0; i < length; i++) {
                int n1 = i < v1.length ? Integer.parseInt(v1[i].replaceAll("\\D", "")) : 0;
                int n2 = i < v2.length ? Integer.parseInt(v2[i].replaceAll("\\D", "")) : 0;
                if (n1 > n2) {
                    return true;
                }
                if (n1 < n2) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return !latestVersion.equalsIgnoreCase(currentVersion);
        }
        return false;
    }

    /**
     * Downloads the release asset and launches the installer.
     */
    public static void downloadAndInstallAsync(ReleaseInfo release, Consumer<Double> progressCallback, Runnable onSuccess, Consumer<Exception> onError) {
        Thread thread = new Thread(() -> {
            try {
                URL url = URI.create(release.downloadUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "DailyDash-App");
                conn.setInstanceFollowRedirects(true);
                conn.connect();

                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    String newUrl = conn.getHeaderField("Location");
                    url = URI.create(newUrl).toURL();
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "DailyDash-App");
                    conn.connect();
                }

                int contentLength = conn.getContentLength();
                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                File targetFile = new File(tempDir, release.assetName);

                try (InputStream in = new BufferedInputStream(conn.getInputStream());
                     FileOutputStream out = new FileOutputStream(targetFile)) {

                    byte[] buffer = new byte[8192];
                    long totalRead = 0;
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        totalRead += read;
                        if (contentLength > 0) {
                            double progress = (double) totalRead / contentLength;
                            Platform.runLater(() -> progressCallback.accept(progress));
                        }
                    }
                }

                Platform.runLater(() -> progressCallback.accept(1.0));

                // Launch installer
                launchInstaller(targetFile);

                Platform.runLater(onSuccess);
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private static void launchInstaller(File installerFile) throws Exception {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            new ProcessBuilder("cmd", "/c", "start", "", installerFile.getAbsolutePath()).start();
        } else if (os.contains("mac")) {
            new ProcessBuilder("open", installerFile.getAbsolutePath()).start();
        } else {
            new ProcessBuilder("xdg-open", installerFile.getAbsolutePath()).start();
        }
        // Exit current app cleanly so installer can update files
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
