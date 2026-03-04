@echo off
REM Run script for JavaFX Application
REM Usage: run-fx.bat

set JAVA_FX_SDK=C:\JavaFX\javafx-sdk-23
set MAIN_CLASS=ui.PersonalExpenseManagement
set MODULES=javafx.controls

echo Running JavaFX Application...
java --module-path "%JAVA_FX_SDK%\lib" --add-modules %MODULES% -cp build "%MAIN_CLASS%"
