@echo off
REM DailyDash Build Script
REM Builds DailyDash and creates a distributable JAR

echo.
echo ============================================================
echo DailyDash Build Script
echo ============================================================
echo.

REM Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your system PATH
    echo.
    pause
    exit /b 1
)

echo [1/3] Cleaning previous build...
mvn clean

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)

echo.
echo [2/3] Compiling and packaging...
mvn package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo [3/3] Build complete!
echo.
echo ============================================================
echo SUCCESS: DailyDash has been built
echo ============================================================
echo.
echo To run the application, double-click: DailyDash.bat
echo.
pause
