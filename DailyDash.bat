@echo off
REM DailyDash - Simple Launcher Script
REM This script allows users to run DailyDash without opening a terminal

setlocal enabledelayedexpansion

REM Get the directory where this batch file is located
set SCRIPT_DIR=%~dp0

REM Check if the JAR file exists
if not exist "%SCRIPT_DIR%target\*.jar" (
    echo.
    echo ============================================================
    echo ERROR: DailyDash JAR not found
    echo ============================================================
    echo.
    echo Please run the build command first:
    echo   mvn clean package
    echo.
    pause
    exit /b 1
)

REM Find the JAR file (should be dailydash-1.0.0-shaded.jar from maven-shade-plugin)
for /f "delims=" %%f in ('dir /b "%SCRIPT_DIR%target\dailydash-*.jar" 2^>nul ^| findstr /v "original"') do (
    set JAR_FILE=%%f
    goto :found_jar
)

echo ERROR: Could not find DailyDash JAR file
pause
exit /b 1

:found_jar
REM Run the JAR file
java -jar "%SCRIPT_DIR%target\%JAR_FILE%"
exit /b %ERRORLEVEL%
