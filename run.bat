@echo off
echo ========================================
echo    AEROLINEAS PC21 - Compilando...
echo ========================================
cd src\main\java
javac com/aerolineas/*.java

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo        ERROR DE COMPILACION!
    echo ========================================
    echo Corrige los errores y vuelve a ejecutar.
    echo.
    pause
    exit /b 1
)

cls
echo ========================================
echo      COMPILACION EXITOSA - Ejecutando...
echo ========================================
java com.aerolineas.Main
pause
