@echo off
echo Creando base de datos apiticket...

REM Intentar con diferentes rutas posibles de MySQL
set MYSQL_PATHS="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe" "C:\xampp\mysql\bin\mysql.exe" "C:\wamp64\bin\mysql\mysql8.0.27\bin\mysql.exe"

for %%i in (%MYSQL_PATHS%) do (
    if exist %%i (
        echo Encontrado MySQL en: %%i
        %%i -u root -pconde2312 < create_database.sql
        if errorlevel 1 (
            echo Error al crear la base de datos
        ) else (
            echo Base de datos apiticket creada exitosamente
        )
        goto :found
    )
)

echo.
echo MySQL no encontrado en las rutas habituales.
echo Por favor ejecuta manualmente:
echo   mysql -u root -pconde2312 < create_database.sql
echo.
echo O desde el cliente MySQL:
echo   CREATE DATABASE IF NOT EXISTS apiticket;
echo.

:found
pause
