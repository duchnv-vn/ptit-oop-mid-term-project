param(
    [string]$AppName = "PersonalExpenseManager",
    [string]$MainClass = "Main"
)

$ErrorActionPreference = "Stop"

$projectRoot = $PSScriptRoot
$srcDir = Join-Path $projectRoot "src"
$buildDir = Join-Path $projectRoot "build"
$classesDir = Join-Path $buildDir "classes"
$jarDir = Join-Path $buildDir "jar"
$distDir = Join-Path $buildDir "dist"
$jarName = "personal-expense-manager.jar"

if (-not (Test-Path $srcDir)) {
    throw "Cannot find src directory at: $srcDir"
}

if (Test-Path $buildDir) {
    Remove-Item -Path $buildDir -Recurse -Force
}

New-Item -ItemType Directory -Path $classesDir -Force | Out-Null
New-Item -ItemType Directory -Path $jarDir -Force | Out-Null
New-Item -ItemType Directory -Path $distDir -Force | Out-Null

$sources = Get-ChildItem -Path $srcDir -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($sources.Count -eq 0) {
    throw "No Java source files found under: $srcDir"
}

Write-Host "Compiling Java sources..."
javac -encoding UTF-8 -d $classesDir $sources
if ($LASTEXITCODE -ne 0) {
    throw "javac failed."
}

Write-Host "Creating runnable JAR..."
$jarPath = Join-Path $jarDir $jarName
jar --create --file $jarPath --main-class $MainClass -C $classesDir .
if ($LASTEXITCODE -ne 0) {
    throw "jar command failed."
}

Write-Host "Packaging app image (.exe launcher) ..."
jpackage `
  --type app-image `
  --name $AppName `
  --input $jarDir `
  --main-jar $jarName `
  --main-class $MainClass `
  --dest $distDir `
  --java-options "-Dfile.encoding=UTF-8"
if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed."
}

$exePath = Join-Path $distDir "$AppName\$AppName.exe"
if (-not (Test-Path $exePath)) {
    throw "Packaging completed but launcher was not found at: $exePath"
}

Write-Host ""
Write-Host "Build successful."
Write-Host "Open the app by double-clicking:"
Write-Host "  $exePath"
