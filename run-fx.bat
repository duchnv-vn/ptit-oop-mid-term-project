@echo off
REM Run script for Swing Application
REM Usage: run-fx.bat

set MAIN_CLASS=Main

echo Running Swing Application...
java -cp build %MAIN_CLASS%
