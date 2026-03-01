@echo off
setlocal

powershell -ExecutionPolicy Bypass -File "%~dp0build-exe.ps1"
if errorlevel 1 (
  echo.
  echo Build failed.
  pause
  exit /b 1
)

echo.
echo Build finished successfully.
pause
