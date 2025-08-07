@echo off
title API Tickets - Servidor Local

echo ========================================
echo API TICKETS - SISTEMA DE GESTION LOCAL
echo ========================================
echo.
echo Configurando variables de entorno...

REM Cargar variables del archivo .env
for /f "usebackq tokens=1,2 delims==" %%a in (".env") do (
    if not "%%a"=="" if not "%%a:~0,1"=="#" (
        set "%%a=%%b"
    )
)

echo Variables cargadas desde .env
echo.
echo DB_URL: %DB_URL%
echo DB_USER: %DB_USER%
echo SERVER_PORT: %SERVER_PORT%
echo.
echo Iniciando aplicacion Spring Boot...
echo.

REM Ejecutar la aplicación con las variables de entorno
call mvnw spring-boot:run

echo.
echo Aplicacion finalizada.
pause
