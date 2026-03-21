@echo off
REM Script para configurar aplicacao para usar MySQL Docker
echo ========================================
echo   OSManager - Configurar MySQL Docker
echo ========================================
echo.
echo [1/3] Fazendo backup do application.properties atual...
if exist src\main\resources\application.properties (
    copy src\main\resources\application.properties src\main\resources\application.properties.backup
    echo [OK] Backup criado: application.properties.backup
) else (
    echo [AVISO] Arquivo application.properties nao encontrado
)
echo.
echo [2/3] Configurando para MySQL Docker...
copy src\main\resources\application.properties.docker src\main\resources\application.properties
if errorlevel 1 (
    echo [ERRO] Falha ao copiar configuracao
    pause
    exit /b 1
)
echo [OK] Configuracao aplicada
echo.
echo [3/3] Verificando configuracao...
findstr "db.url" src\main\resources\application.properties
findstr "db.username" src\main\resources\application.properties
findstr "db.password" src\main\resources\application.properties
echo.
echo ========================================
echo   CONFIGURACAO CONCLUIDA!
echo ========================================
echo.
echo [INFO] Aplicacao configurada para MySQL Docker
echo [INFO] Proximos passos:
echo   1. Iniciar MySQL: .\scripts\test-mysql.bat
echo   2. Compilar: mvn clean package
echo   3. Deploy no Tomcat
echo   4. Testar: http://localhost:8080
echo.
echo [INFO] Para restaurar configuracao original:
echo   copy src\main\resources\application.properties.backup src\main\resources\application.properties
echo.
pause
