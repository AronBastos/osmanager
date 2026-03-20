# Script de validacao de ambiente OSManager
# Uso: .\scripts\validate-environment.ps1

$SUCCESS = "Green"
$WARNING = "Yellow"
$ERROR = "Red"

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "VALIDACAO DE AMBIENTE - OSMANGER" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Java
Write-Host "1. Verificando Java..." -ForegroundColor Yellow
$java = java -version 2>&1
if ($java -like "*17*") {
    Write-Host "   OK - Java 17 encontrado" -ForegroundColor $SUCCESS
} else {
    Write-Host "   ERRO - Versao incorreta encontrada" -ForegroundColor $ERROR
    Write-Host "   $($java[0])" -ForegroundColor $ERROR
    Write-Host "   Necessario: Java 17+" -ForegroundColor $WARNING
}
Write-Host ""

# 2. Maven
Write-Host "2. Verificando Maven..." -ForegroundColor Yellow
$maven = mvn -version 2>&1 | Select-Object -First 1
if ($maven) {
    Write-Host "   OK - $maven" -ForegroundColor $SUCCESS
} else {
    Write-Host "   ERRO - Maven nao encontrado" -ForegroundColor $ERROR
}
Write-Host ""

# 3. Tomcat
Write-Host "3. Verificando Tomcat..." -ForegroundColor Yellow
if (Test-Path "C:\apache-tomcat\bin\startup.bat") {
    Write-Host "   OK - Tomcat encontrado em C:\apache-tomcat" -ForegroundColor $SUCCESS
    if (Test-Path "C:\apache-tomcat\bin\setenv.bat") {
        Write-Host "   OK - setenv.bat configurado" -ForegroundColor $SUCCESS
    } else {
        Write-Host "   AVISO - setenv.bat nao encontrado" -ForegroundColor $WARNING
    }
} else {
    Write-Host "   ERRO - Tomcat nao encontrado" -ForegroundColor $ERROR
}
Write-Host ""

# 4. MySQL
Write-Host "4. Verificando MySQL..." -ForegroundColor Yellow
$mysql = mysql -u root -e "SELECT VERSION();" 2>&1 | Select-Object -First 1
if ($mysql -and $mysql -notmatch "error") {
    Write-Host "   OK - MySQL rodando: $mysql" -ForegroundColor $SUCCESS
} else {
    Write-Host "   ERRO - MySQL nao respondeu" -ForegroundColor $ERROR
    Write-Host "   Execute: net start MySQL80" -ForegroundColor $WARNING
}
Write-Host ""

# 5. Projeto
Write-Host "5. Verificando projeto..." -ForegroundColor Yellow
if (Test-Path ".\pom.xml") {
    Write-Host "   OK - Projeto encontrado (pom.xml)" -ForegroundColor $SUCCESS
} else {
    Write-Host "   ERRO - pom.xml nao encontrado" -ForegroundColor $ERROR
}

if (Test-Path ".\docs") {
    Write-Host "   OK - Documentacao encontrada" -ForegroundColor $SUCCESS
} else {
    Write-Host "   AVISO - docs nao encontrada" -ForegroundColor $WARNING
}
Write-Host ""

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "Verificacao concluida!" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan

