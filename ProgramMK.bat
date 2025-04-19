@echo off
:: Check for administrator rights
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Requesting administrative privileges...
    goto UACPrompt
) else (
    goto GotAdmin
)

:UACPrompt
:: Create a temporary VBScript to elevate privileges
set "vbsFile=%temp%\getadmin.vbs"
echo Set UAC = CreateObject^("Shell.Application"^) > "%vbsFile%"
echo UAC.ShellExecute "cmd.exe", "/c ""%~f0""", "", "runas", 1 >> "%vbsFile%"
cscript //nologo "%vbsFile%"
del "%vbsFile%"
exit /b

:GotAdmin
pushd "%CD%"
cd /d "%~dp0"

:: Your actual code starts here
set "JAVA_PATH=C:\Program Files\Azul\zulu-21\bin\javaw.exe"
set "PROGRAM_PATH=C:\Program Files\MK Facturacion\ProgramaMK.jar"
set "JAVAFX_HOME=C:\Program Files\open-javaxf\javafx-sdk-21.0.7\lib"
start "" "%JAVA_PATH%" --module-path "%JAVAFX_HOME%" --add-modules javafx.controls,javafx.fxml -jar "%PROGRAM_PATH%"
exit
