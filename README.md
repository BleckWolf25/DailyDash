# DailyDash

![CI/CD Build & Release](https://github.com/BleckWolf25/DailyDash/actions/workflows/ci.yml/badge.svg)

> High-performance, locally encrypted personal Kanban task manager built with JavaFX and SQLite.

DailyDash is a premium offline-first desktop productivity application. It provides an interactive task board, custom markdown notes, workspace analytics, and custom automation rules.

- **Kanban Board** - Drag-and-drop tasks across status columns (To Do, In Progress, Done) and reorder within lists.
- **Showcase Board** - Pre-populated interactive welcome guide highlighting all key application capabilities.
- **Multiple Projects** - Switch, star, add, or delete independent project task boards.
- **Markdown Notes** - View and edit formatted descriptions supporting headings, lists, bold, and italic syntax.
- **Automations** - Code/visual event-driven automations or import/export advanced JSON rules.
- **Workspace Analytics** - Built-in SVG chart breakdown of completion rates, priorities, and project tasks.
- **Modern Dark Mode** - Persistent themes matching a sleek, custom Syntactic Management color palette.

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)** 21 or higher
- **Maven** 3.9.0 or higher

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/BleckWolf25/DailyDash.git
   cd DailyDash
   ```

2. Compile and run the application:

   ```bash
   mvn clean javafx:run
   ```

3. Packaging a standalone shaded fat JAR:

   ```bash
   mvn clean package -DskipTests
   ```

4. Run the compiled standalone executable:

   ```bash
   java -jar target/dailydash-1.0.0.jar
   ```

## 📝 Available Commands

- `mvn javafx:run` - Start the application locally in development mode
- `mvn clean test` - Run JUnit 5 unit and integration tests
- `mvn clean package` - Build standalone runnable shaded fat JAR
- `mvn gluonfx:build -Pdesktop` - Build native macOS/Windows desktop application executable via GraalVM
- `mvn gluonfx:build -Pandroid` - Package native Android mobile application package (.apk/.aab)
- `mvn gluonfx:build -Pios` - Package native iOS mobile application package
- `mvn jpro:run` - Host JavaFX app on local server for web browser execution

## 🏗️ Project Structure

```zsh
DailyDash/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dailydash/
│   │   │       ├── Main.java          # Application startup entry point
│   │   │       ├── controller/        # Event handling controllers & dialog frames
│   │   │       ├── model/             # Entities, Enums (Priority, Status) & Observable lists
│   │   │       ├── service/           # SQLite CRUD operations & Automation Engine
│   │   │       └── view/              # Home, Projects, Automations, Settings views
│   │   └── resources/
│   │       ├── fxml/                  # JavaFX layout templates
│   │       ├── css/                   # Stylesheets containing custom dark/light rules
│   │       ├── assets/icons/          # Android, iOS, macOS, and Web icon mipmaps
│   │       └── db/                    # SQLite database creation scripts
│   └── test/
│       └── java/
│           └── com/dailydash/
│               └── service/           # Database, task list, and automation engine unit tests
├── .editorconfig                      # editorconfig configuration
├── .gitignore                         # gitignore configuration
├── CHANGELOG.md                       # Project changelog
├── pom.xml                            # Maven build configuration and profiles
└── LICENSE                            # Project License
```

## 🧪 Testing

The project uses **JUnit 5** for database, MVC architecture, and business logic tests.

### Run Unit Tests

```bash
mvn clean test
```

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guidelines](CONTRIBUTING.md) before submitting a pull request.

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔒 Security

For security concerns, please review our [Security Policy](SECURITY.md).

## 📧 Contact

For questions or support, please open an issue on GitHub or contact [joao.coutinho08@icloud.com](mailto:joao.coutinho08@icloud.com).

---

Built with ❤️ using JavaFX, Maven, and SQLite
