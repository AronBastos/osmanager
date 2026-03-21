# Guia Completo: Configurar Tomcat 10 + Java 17 + MySQL 8

## 🎯 Objetivo
Configurar ambiente profissional com Java 17, Tomcat 10 e MySQL 8 para deploy do OSManager.

---

## 1️⃣ INSTALAR JAVA 17

### Download
```
https://www.oracle.com/java/technologies/downloads/#java17
```

### Windows - Passo a Passo

1. **Baixe o arquivo**
   - Windows x64 Installer (jdk-17_windows-x64_bin.exe)

2. **Execute o instalador**
   - Clique duas vezes no .exe
   - Clique "Next" nas telas

3. **Selecione destino** (importante!)
   - Caminho: `C:\WorkSpace\Java 17`
   - Ajuste conforme sua instalação

4. **Conclua a instalação**
   - Clique "Finish"

### Verificar Instalação

```powershell
java -version
```

**Deve mostrar:**
```
java version "17.x.x"
Java(TM) SE Runtime Environment (build 17.x.x+x)
```

### Adicionar ao PATH (se necessário)

1. Abra: **Variáveis de Ambiente** (Win+X)
2. Clique: **Variáveis de Ambiente**
3. Em **PATH** do usuário, adicione:
   ```
   C:\WorkSpace\Java 17\bin
   ```
4. Clique OK e reinicie o terminal

---

## 2️⃣ INSTALAR TOMCAT 10

### Download
```
https://tomcat.apache.org/download-10.cgi
```

### Windows - Passo a Passo

1. **Baixe o arquivo**
   - Apache Tomcat 10.1.x - ZIP file

2. **Descompacte**
   - Extraia para: `C:\apache-tomcat`

3. **Estrutura esperada**
   ```
   C:\apache-tomcat\
   ├── bin/         ← Scripts de inicialização
   ├── conf/        ← Configurações
   ├── lib/         ← Bibliotecas
   ├── webapps/     ← Aplicações web
   ├── startup.bat  ← Iniciar
   └── shutdown.bat ← Parar
   ```

### Configurar JAVA_HOME

**Criar arquivo setenv.bat:**

1. Abra: `C:\apache-tomcat\bin\`
2. Crie arquivo: `setenv.bat`
3. Adicione conteúdo:

```batch
@echo off
set JAVA_HOME=C:\WorkSpace\Java 17
set CATALINA_HOME=C:\apache-tomcat
```

4. Salve e feche

### Iniciar Tomcat

**Opção A: Direto pelo Windows Explorer**
```
C:\apache-tomcat\bin\startup.bat
```

**Opção B: PowerShell**
```powershell
C:\apache-tomcat\bin\startup.bat
# Aguarde aparecer: "Tomcat started"
```

### Verificar Tomcat

Abra no navegador:
```
http://localhost:8080
```

**Deve aparecer:** Página do Tomcat com "Welcome to Tomcat"

### Parar Tomcat

```powershell
C:\apache-tomcat\bin\shutdown.bat
```

---

## 3️⃣ VERIFICAR/INSTALAR MYSQL 8

### Verificar se está rodando

```bash
mysql -u root -e "SELECT VERSION();"
```

**Se funcionar:** MySQL está OK ✅

**Se der erro:** MySQL não está instalado ou não está rodando

### Instalar MySQL (Windows)

1. **Download**
   - https://dev.mysql.com/downloads/mysql/
   - MySQL Installer 8.0.x

2. **Execute o instalador**
   - mysql-installer-community-8.0.x.msi

3. **Configuração**
   - Tipo: Developer Default
   - Config Type: Development Machine
   - Port: 3306
   - Usuário: root
   - Senha: (sua senha)

4. **Inicie o serviço**
   ```bash
   net start MySQL80
   ```

### Verificar MySQL

```bash
mysql -u root -p
# Digite a senha

mysql> SHOW DATABASES;
mysql> EXIT;
```

---

## 4️⃣ CRIAR BANCO PARA OSMANGER

```bash
cd C:\projetos\osmanager

# Criar banco de dados
mysql -u root -p < scripts/create_database.sql

# Verificar
mysql -u root -p -e "USE osmanager; SHOW TABLES;"
```

---

## 5️⃣ DEPLOY NO TOMCAT

### Build do projeto

```bash
cd C:\projetos\osmanager
mvn clean package
```

**Resultado:**
```
target/osmanager-1.0-SNAPSHOT.war
```

### Deploy (Opção A: Manual)

```powershell
# 1. Parar Tomcat
C:\apache-tomcat\bin\shutdown.bat

# 2. Copiar WAR
Copy-Item "target/osmanager-1.0-SNAPSHOT.war" "C:\apache-tomcat\webapps\ROOT.war"

# 3. Iniciar Tomcat
C:\apache-tomcat\bin\startup.bat

# 4. Aguardar deploy
# Deve criar pasta: C:\apache-tomcat\webapps\ROOT\
```

### Deploy (Opção B: Script)

```powershell
.\scripts\deploy.ps1
```

### Acessar Aplicação

Abra no navegador:
```
http://localhost:8080
```

---

## 🔍 TROUBLESHOOTING

### Tomcat não inicia
**Causa:** Java 8 ainda configurado
**Solução:** 
```powershell
# Verificar JAVA_HOME
echo $env:JAVA_HOME

# Deve mostrar: C:\Program Files\Java\jdk-17
```

### "Port 8080 already in use"
**Causa:** Outra aplicação usando port 8080
**Solução:**
```powershell
# Mudar port no: C:\apache-tomcat\conf\server.xml
# Linha com: port="8080"
# Alterar para: port="8081"

# Acessar em: http://localhost:8081
```

### MySQL connection refused
**Causa:** MySQL não está rodando
**Solução:**
```bash
# Verificar serviço
Get-Service MySQL80

# Iniciar se parado
Start-Service MySQL80
```

### Aplicação com erro 404
**Causa:** WAR não foi expandido
**Solução:**
```powershell
# Verificar logs
Get-Content C:\apache-tomcat\logs\catalina.out

# Deletar cache e tentar novamente
Remove-Item C:\apache-tomcat\webapps\ROOT -Recurse
```

---

## ✅ VERIFICAÇÃO FINAL

Execute este script para validar:

```powershell
Write-Host "✅ Verificando ambiente..."

# Java 17
java -version

# Tomcat
if (Test-Path "C:\apache-tomcat\bin\startup.bat") {
    Write-Host "✅ Tomcat OK"
}

# MySQL
mysql -u root -e "SELECT VERSION();"

Write-Host "✅ Tudo configurado!"
```

---

## 🚀 PRÓXIMOS PASSOS

1. ✅ Instalar Java 17
2. ✅ Instalar Tomcat 10
3. ✅ Configurar MySQL
4. ✅ Deploy da aplicação
5. ✅ Acessar em http://localhost:8080

---

**Última atualização:** 20 de Março de 2026

