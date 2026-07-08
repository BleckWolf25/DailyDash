# DailyDash - Quick Reference Card

## For End Users 👥

```
JUST DOUBLE-CLICK:
DailyDash.bat
```

✅ That's all!

---

## For Developers 👨‍💻

### Build the App

```bash
# Option 1: Double-click (Windows)
build.bat

# Option 2: Terminal
mvn clean package
```

### Run the App

```bash
# Option 1: Double-click (Windows)
DailyDash.bat

# Option 2: Terminal
java -jar target/dailydash-1.0.0.jar
```

### Create Installer (Advanced)

```bash
mvn clean package
mvn jpackage:jpackage
# Output: target/dist/DailyDash.exe
```

---

## Common Commands

| Task | Command |
|------|---------|
| Compile Only | `mvn compile` |
| Run Tests | `mvn test` |
| Build JAR | `mvn package` |
| Full Build | `mvn clean package` |
| Run Tests + Build | `mvn clean package` |
| Skip Tests | `mvn package -DskipTests` |
| View Dependencies | `mvn dependency:tree` |
| Clean Build Folder | `mvn clean` |

---

## File Structure

```
DailyDash/
├── src/                     ← Source code
│   ├── main/
│   │   ├── java/           ← Java source files
│   │   └── resources/      ← FXML, CSS, images
│   └── test/               ← Test files
├── target/                 ← Build output
│   └── dailydash-*.jar     ← Your runnable JAR
├── pom.xml                 ← Maven configuration
├── DailyDash.bat          ← Run the app (Windows)
├── DailyDash.ps1          ← Run the app (PowerShell)
├── build.bat              ← Build the app (Windows)
├── USER_SETUP.md          ← User installation guide
├── DEPLOYMENT_GUIDE.md    ← Distribution options
└── DEPLOYMENT_SETUP.md    ← Full setup documentation
```

---

## Troubleshooting

### "Java not found"

→ Install Java 21+ from [adoptium.net](https://adoptium.net/)

### "Maven not found"

→ Install Maven from [maven.apache.org](https://maven.apache.org/download.cgi)

### "JAR not found"

→ Run `build.bat` or `mvn clean package` first

### Application won't start

→ Make sure both `DailyDash.bat` and `target/dailydash-*.jar` exist in same folder

---

## System Requirements

- **Java 21+**
- **Maven 3.8.8+** (for developers only)
- **Windows 10+** or Linux or macOS
- **200 MB** free disk space

---

## File Locations (Important!)

For the batch file to work, keep these together:

```
📁 YourFolder/
  ├── 📜 DailyDash.bat
  └── 📁 target/
      └── 📦 dailydash-1.0.0.jar
```

If you move the JAR, update the batch file paths!

---

## Keyboard Shortcuts (In App)

See your application's documentation for shortcuts.

---

## Getting Help

1. Check `USER_SETUP.md` for detailed instructions
2. Check `DEPLOYMENT_GUIDE.md` for distribution options
3. Check project `README.md` for development info

---

## Key Files You Need

For **Running the App:**

- ✅ `DailyDash.bat`
- ✅ `target/dailydash-1.0.0.jar`

For **Building the App:**

- ✅ `build.bat` (Windows)
- ✅ `pom.xml`

For **Documentation:**

- ✅ `USER_SETUP.md`
- ✅ `DEPLOYMENT_GUIDE.md`
- ✅ `README.md`

---

## Version Info

| Component | Version |
|-----------|---------|
| DailyDash | 1.0.0 |
| Java | 21+ |
| Maven | 3.8.8+ |
| JavaFX | 23.0.1 |
| SQLite | 3.46.1.0 |

---

**Last Updated:** 2026-07-08
**Ready to Deploy:** ✅ YES
