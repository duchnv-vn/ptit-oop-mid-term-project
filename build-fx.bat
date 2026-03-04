@echo off
REM Build script for the application (Swing)
REM Usage: build-fx.bat

set SRC_DIR=src
set BUILD_DIR=build

echo Building application (Swing)...

REM Create build directory
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

REM Create a sources list and compile all .java files (works in cmd)
if exist sources.txt del sources.txt
for /R %SRC_DIR% %%f in (*.java) do @echo %%f >> sources.txt

javac -d "%BUILD_DIR%" -encoding UTF-8 @sources.txt

if exist sources.txt del sources.txt

echo Build completed!
echo.
echo To run the application:
echo   run-fx.bat
