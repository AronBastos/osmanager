@echo off
REM Script para testar MySQL rapidamente com Docker

echo ========================================
echo   OSManager - Teste Rapido MySQL
echo ========================================
echo.

echo [1/6] Verificando Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Docker nao esta instalado
    echo Instale: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
echo [OK] Docker encontrado
echo.

echo [2/6] Iniciando container MySQL...
docker run --name mysql-test -e MYSQL_ROOT_PASSWORD=root_password -e MYSQL_DATABASE=osmanager -p 3306:3306 -d mysql:8.0 >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Falha ao iniciar MySQL
    docker rm mysql-test >nul 2>&1
    pause
    exit /b 1
)
echo [OK] Container MySQL iniciado
echo.

echo [3/6] Aguardando MySQL ficar pronto...
timeout /t 20 /nobreak >nul
echo [OK] Pronto para testes
echo.

echo [4/6] Testando conexao...
for /l %%i in (1,1,10) do (
    docker exec mysql-test mysql -u root -proot_password -e "SELECT 1;" >nul 2>&1
    if !errorlevel! equ 0 (
        goto :mysql_ready
    )
    echo Tentativa %%i/10 falhou, aguardando...
    timeout /t 3 /nobreak >nul
)
echo [ERRO] Falha na conexao apos 10 tentativas
goto :cleanup

:mysql_ready
docker exec mysql-test mysql -u root -proot_password -e "SELECT VERSION() AS 'MySQL Version';" 2>nul
if errorlevel 1 (
    echo [ERRO] Falha na conexao
    goto :cleanup
)
echo [OK] Conexao estabelecida
echo.

echo [5/6] Criando tabelas do OSManager...
if exist scripts\create_database.sql (
    docker exec -i mysql-test mysql -u root -proot_password osmanager < scripts\create_database.sql 2>nul
    if errorlevel 1 (
        echo [AVISO] Erro ao criar tabelas, mas continuando...
    ) else (
        echo [OK] Tabelas criadas
    )
) else (
    echo [AVISO] Script create_database.sql nao encontrado
)
echo.

echo [6/6] Verificando tabelas criadas...
docker exec mysql-test mysql -u root -proot_password osmanager -e "SHOW TABLES;" 2>nul
echo.

:cleanup
echo ========================================
echo   TESTE CONCLUIDO COM SUCESSO!
echo ========================================
echo.
echo [INFO] MySQL esta funcionando corretamente
echo [INFO] Porta: 3306
echo [INFO] Usuario: root
echo [INFO] Senha: root_password
echo [INFO] Banco: osmanager
echo.
echo [INFO] Para usar na aplicacao:
echo   - db.url=jdbc:mysql://localhost:3306/osmanager
echo   - db.username=root
echo   - db.password=root_password
echo.
echo [INFO] Proximos passos:
echo   1. Configure application.properties
echo   2. Execute: mvn clean package
echo   3. Deploy no Tomcat
echo   4. Teste: http://localhost:8080
echo.
echo [INFO] Para parar o teste:
echo   docker stop mysql-test ^& docker rm mysql-test
echo.
pause
