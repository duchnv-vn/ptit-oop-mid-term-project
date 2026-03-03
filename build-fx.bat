@echo off
REM Build script for JavaFX Application
REM Usage: build-fx.bat

set JAVA_FX_SDK=C:\JavaFX\javafx-sdk-23
set SRC_DIR=src
set BUILD_DIR=build
set MODULES=javafx.controls

echo Building JavaFX Application...

REM Create build directory
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

REM Compile model, storage, service classes
javac -d "%BUILD_DIR%" -encoding UTF-8 "%SRC_DIR%\model\*.java"
javac -d "%BUILD_DIR%" -encoding UTF-8 -cp "%BUILD_DIR%" "%SRC_DIR%\storage\*.java"
javac -d "%BUILD_DIR%" -encoding UTF-8 -cp "%BUILD_DIR%" "%SRC_DIR%\service\*.java"

REM Compile UI classes with JavaFX
javac -d "%BUILD_DIR%" -encoding UTF-8 --module-path "%JAVA_FX_SDK%\lib" --add-modules %MODULES% -cp "%BUILD_DIR%" "%SRC_DIR%\ui\*.java"

echo Build completed!
echo.
echo To run the application:
echo   run-fx.bat
