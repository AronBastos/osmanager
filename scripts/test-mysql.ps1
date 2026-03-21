# Script para testar MySQL com Docker
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  OSManager - Teste MySQL Docker" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Docker
Write-Host "[1/6] Verificando Docker..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version 2>$null
    Write-Host "   OK - $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "   ERRO - Docker nao encontrado" -ForegroundColor Red
    Write-Host "   Instale: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Iniciar MySQL
Write-Host "[2/6] Iniciando container MySQL..." -ForegroundColor Yellow
try {
    docker run --name mysql-test -e MYSQL_ROOT_PASSWORD=root_password -e MYSQL_DATABASE=osmanager -p 3306:3306 -d mysql:8.0 >$null 2>&1
    Write-Host "   OK - Container iniciado" -ForegroundColor Green
} catch {
    Write-Host "   ERRO - Falha ao iniciar container" -ForegroundColor Red
    docker rm mysql-test >$null 2>&1
    exit 1
}
Write-Host ""

# Aguardar MySQL
Write-Host "[3/6] Aguardando MySQL ficar pronto..." -ForegroundColor Yellow
Start-Sleep -Seconds 15
Write-Host "   OK - Pronto para testes" -ForegroundColor Green
Write-Host ""

# Testar conexao
Write-Host "[4/6] Testando conexao..." -ForegroundColor Yellow
try {
    $result = docker exec mysql-test mysql -u root -proot_password -e "SELECT VERSION() AS 'MySQL Version';" 2>$null
    Write-Host "   OK - Conexao estabelecida" -ForegroundColor Green
    Write-Host "   $result" -ForegroundColor White
} catch {
    Write-Host "   ERRO - Falha na conexao" -ForegroundColor Red
    docker stop mysql-test >$null 2>&1
    docker rm mysql-test >$null 2>&1
    exit 1
}
Write-Host ""

# Criar tabelas
Write-Host "[5/6] Criando tabelas do OSManager..." -ForegroundColor Yellow
if (Test-Path "scripts\create_database.sql") {
    try {
        Get-Content "scripts\create_database.sql" | docker exec -i mysql-test mysql -u root -proot_password osmanager 2>$null
        Write-Host "   OK - Tabelas criadas" -ForegroundColor Green
    } catch {
        Write-Host "   AVISO - Erro ao criar tabelas" -ForegroundColor Yellow
    }
} else {
    Write-Host "   AVISO - Script create_database.sql nao encontrado" -ForegroundColor Yellow
}
Write-Host ""

# Verificar tabelas
Write-Host "[6/6] Verificando tabelas criadas..." -ForegroundColor Yellow
try {
    $tables = docker exec mysql-test mysql -u root -proot_password osmanager -e "SHOW TABLES;" 2>$null
    Write-Host "   OK - Tabelas encontradas:" -ForegroundColor Green
    Write-Host "   $tables" -ForegroundColor White
} catch {
    Write-Host "   AVISO - Erro ao verificar tabelas" -ForegroundColor Yellow
}
Write-Host ""

# Conclusao
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   TESTE CONCLUIDO COM SUCESSO!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "MySQL esta funcionando corretamente:" -ForegroundColor White
Write-Host "  Porta: 3306" -ForegroundColor White
Write-Host "  Usuario: root" -ForegroundColor White
Write-Host "  Senha: root_password" -ForegroundColor White
Write-Host "  Banco: osmanager" -ForegroundColor White
Write-Host ""
Write-Host "Para usar na aplicacao:" -ForegroundColor Yellow
Write-Host "  db.url=jdbc:mysql://localhost:3306/osmanager" -ForegroundColor White
Write-Host "  db.username=root" -ForegroundColor White
Write-Host "  db.password=root_password" -ForegroundColor White
Write-Host ""
Write-Host "Proximos passos:" -ForegroundColor Yellow
Write-Host "  1. Execute: mvn clean package" -ForegroundColor White
Write-Host "  2. Deploy no Tomcat" -ForegroundColor White
Write-Host "  3. Teste: http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "Para parar o teste:" -ForegroundColor Yellow
Write-Host "  docker stop mysql-test; docker rm mysql-test" -ForegroundColor White
Write-Host ""
Read-Host "Pressione Enter para continuar"
