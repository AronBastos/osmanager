@echo off
REM Script para iniciar Docker Compose no Windows

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   OSManager - Docker Compose Startup
echo ========================================
echo.

REM Verificar se Docker está instalado
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker nao esta instalado
    echo.
    echo Instale Docker Desktop em:
    echo https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Verificar se Docker Compose está instalado
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker Compose nao esta instalado
    pause
    exit /b 1
)

echo [OK] Docker e Docker Compose encontrados
echo.

REM Verificar se .env existe
if not exist .env (
    if exist .env.example (
        echo [AVISO] Arquivo .env nao encontrado
        echo Criando a partir de .env.example...
        copy .env.example .env
        echo [OK] Arquivo .env criado
        echo Edite conforme necessario: .env
    )
)

echo.
echo [INFO] Configuracoes:
echo   - MySQL Database: 3306
echo   - Tomcat Application: 8080
echo   - phpMyAdmin: 8081
echo.

REM Perguntar se deseja fazer rebuild
set /p rebuild="Deseja reconstruir as imagens? (s/n): "
if /i "%rebuild%"=="s" (
    echo [INFO] Construindo imagens...
    docker-compose build
    echo [OK] Imagens construidas
)

echo.
echo [INFO] Iniciando containers...
docker-compose up -d

if errorlevel 1 (
    echo [ERRO] Falha ao iniciar containers
    pause
    exit /b 1
)

echo [OK] Containers iniciados!
echo.

echo [INFO] Aguardando servicos ficarem prontos...
timeout /t 10 /nobreak

echo.
echo [INFO] Status dos containers:
docker-compose ps

echo.
echo [OK] Ambiente Docker iniciado com sucesso!
echo.
echo [INFO] URLs:
echo   - Aplicacao: http://localhost:8080
echo   - phpMyAdmin: http://localhost:8081
echo.
echo [INFO] Proximos passos:
echo   - Ver logs: docker-compose logs -f
echo   - Parar: docker-compose down
echo   - Limpar: docker-compose down -v
echo.
pause

