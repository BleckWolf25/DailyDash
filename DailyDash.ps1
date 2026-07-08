# DailyDash - PowerShell Launcher Script
# This script allows users to run DailyDash without opening a terminal

param(
    [switch]$Console = $false  # Add -Console flag to show console window
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$jarPath = Get-ChildItem -Path "$scriptDir\target" -Filter "dailydash-*.jar" -Exclude "*original*" | Select-Object -First 1

if (-not $jarPath) {
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host "ERROR: DailyDash JAR not found" -ForegroundColor Red
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please run the build command first:" -ForegroundColor Yellow
    Write-Host "  mvn clean package" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Press any key to continue..." -ForegroundColor Gray
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# Run the application
if ($Console) {
    # Show console window
    java -jar $jarPath.FullName
} else {
    # Hide console window
    $processInfo = New-Object System.Diagnostics.ProcessStartInfo
    $processInfo.FileName = "java"
    $processInfo.Arguments = "-jar `"$($jarPath.FullName)`""
    $processInfo.UseShellExecute = $false
    $processInfo.CreateNoWindow = $true
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    
    $process = [System.Diagnostics.Process]::Start($processInfo)
    $process.WaitForExit()
    exit $process.ExitCode
}
