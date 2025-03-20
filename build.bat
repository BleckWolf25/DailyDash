@echo off
REM Build with Maven
call mvn clean install

REM Run the application
call mvn javafx:run