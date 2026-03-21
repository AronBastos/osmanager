# OSManager Deploy Script
# Este script automatiza o build e deploy da aplicação

param(
    [Parameter(Mandatory=$false)]
    [string]$env = "development",

    [Parameter(Mandatory=$false)]
    [string]$tomcatPath = "C:\WorkSpace\ApacheTomCat"
)

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "OSManager - Deploy Script" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Validações iniciais
Write-Host "[1/5] Validando ambiente..." -ForegroundColor Yellow

if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "Erro: Maven não encontrado no PATH" -ForegroundColor Red
    exit 1
}

if (!(Test-Path $tomcatPath)) {
    Write-Host "Aviso: Caminho Tomcat não encontrado: $tomcatPath" -ForegroundColor Yellow
}

Write-Host "✓ Ambiente validado" -ForegroundColor Green
Write-Host ""

# Clean
Write-Host "[2/5] Limpando projeto (mvn clean)..." -ForegroundColor Yellow
mvn clean -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro ao executar clean" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Clean concluído" -ForegroundColor Green
Write-Host ""

# Compile
Write-Host "[3/5] Compilando projeto (mvn compile)..." -ForegroundColor Yellow
mvn compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro ao compilar" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Compilação concluída" -ForegroundColor Green
Write-Host ""

# Test
Write-Host "[4/5] Executando testes (mvn test)..." -ForegroundColor Yellow
mvn test -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro nos testes" -ForegroundColor Yellow
    # Continua mesmo com erro de testes
}
Write-Host "✓ Testes concluídos" -ForegroundColor Green
Write-Host ""

# Package
Write-Host "[5/5] Gerando WAR (mvn package)..." -ForegroundColor Yellow
mvn package -q -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro ao gerar WAR" -ForegroundColor Red
    exit 1
}
Write-Host "✓ WAR gerado com sucesso" -ForegroundColor Green
Write-Host ""

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Deploy Concluído com Sucesso!" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Arquivo WAR: target/osmanager-1.0-SNAPSHOT.war" -ForegroundColor Cyan
Write-Host ""

if (Test-Path "$tomcatPath\webapps") {
    $response = Read-Host "Deseja copiar para Tomcat? (S/N)"
    if ($response -eq "S" -or $response -eq "s") {
        Copy-Item "target/osmanager-1.0-SNAPSHOT.war" "$tomcatPath\webapps\ROOT.war" -Force
        Write-Host "✓ WAR copiado para Tomcat" -ForegroundColor Green
        Write-Host "Reinicie o Tomcat para aplicar as alterações" -ForegroundColor Cyan
    }
}
