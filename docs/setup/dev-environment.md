# Ambiente de Desenvolvimento - OSManager

Guia para configurar seu ambiente de desenvolvimento completo.

---

## 🖥️ Requisitos do Sistema

- **Windows 10/11** ou **Linux** ou **macOS**
- **4GB RAM mínimo** (8GB recomendado)
- **10GB espaço em disco**
- Conexão com internet para baixar dependências

---

## 1️⃣ Java Development Kit (JDK) 17

### Windows

**Download:**
- Acesse: https://www.oracle.com/java/technologies/downloads/#java17
- Baixe: Windows x64 Installer
- Execute o instalador

**Verificar Instalação:**
```powershell
java -version
javac -version
```

**Deve mostrar:**
```
java version "17.x.x"
Java(TM) SE Runtime Environment
```

**Adicionar ao PATH (se necessário):**
```
C:\Program Files\Java\jdk-17\bin
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
```

### macOS
```bash
brew install openjdk@17
java -version
```

---

## 2️⃣ Apache Maven 3.8.1+

### Download
- Acesse: https://maven.apache.org/download.cgi
- Baixe: apache-maven-3.8.x (or latest)
- Descompacte em: `C:\apache-maven` (Windows) ou `/opt/maven` (Linux)

### Verificar Instalação
```bash
mvn -version
```

**Deve mostrar:**
```
Apache Maven 3.8.x
Maven home: C:\apache-maven
Java version: 17.x.x
```

### Adicionar ao PATH

**Windows:**
- Variáveis de Ambiente → PATH
- Adicione: `C:\apache-maven\bin`

**Linux/macOS:**
```bash
export PATH="/opt/maven/bin:$PATH"
```

---

## 3️⃣ MySQL 8.0 (ou MariaDB)

### Windows

**Download:**
- Acesse: https://dev.mysql.com/downloads/mysql/
- Baixe: MySQL Installer 8.0.x

**Instalação:**
1. Execute o installer
2. Selecione: Developer Default (ou Custom)
3. Configure port: 3306 (padrão)
4. Crie usuário: root com senha
5. Inicie serviço MySQL

**Verificar:**
```bash
mysql -u root -p
# Digite a senha
mysql> SHOW DATABASES;
mysql> EXIT;
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server

# Segurança inicial
sudo mysql_secure_installation

# Testar
mysql -u root -p
```

### macOS
```bash
brew install mysql

# Iniciar
brew services start mysql

# Testar
mysql -u root
```

---

## 4️⃣ Git

### Download
- Acesse: https://git-scm.com/download
- Baixe versão para seu SO

### Verificar
```bash
git --version
```

---

## 5️⃣ IntelliJ IDEA Community Edition

### Download
- Acesse: https://www.jetbrains.com/idea/download/
- Baixe: Community Edition (gratuita)
- Execute o instalador

### Configuração Inicial

**Primeira execução:**
1. Selecione tema (Light/Dark)
2. Selecione plugins (deixe padrão)
3. Clique "Finish"

**Configurar SDK Java:**
1. File → Project Structure
2. Project → Project SDK
3. Clique "Edit" ou "Add SDK"
4. Selecione: `C:\Program Files\Java\jdk-17` (Windows)
5. Clique OK

---

## 🗂️ Estrutura de Pastas Recomendada

```
c:\dev\                         (seu diretório de desenvolvimento)
├── java/
│   ├── jdk-17/               (Java 17)
│   └── apache-maven/         (Maven)
├── projetos/
│   └── osmanager/            (este projeto)
├── tools/
│   ├── mysql/                (MySQL)
│   └── git/                  (Git)
```

---

## ✅ Verificação Completa

Execute no terminal:

```bash
# Java
java -version
javac -version

# Maven
mvn -version

# Git
git --version

# MySQL
mysql -u root -p -e "SELECT VERSION();"
```

**Todos devem retornar versão sem erros!**

---

## 🚀 Primeira Vez com o Projeto

1. **Clone/baixe o projeto**
   ```bash
   cd c:\dev\projetos
   git clone <url>
   cd osmanager
   ```

2. **Configure credenciais MySQL**
   ```bash
   mysql -u root -p < scripts/create_database.sql
   ```

3. **Baixe dependências**
   ```bash
   mvn clean install
   ```

4. **Abra no IntelliJ IDEA**
   - File → Open
   - Selecione: `c:\dev\projetos\osmanager`

5. **Aguarde indexação**
   - Pode levar 1-2 minutos na primeira vez

6. **Compile**
   - Build → Build Project

---

## 📚 Variáveis de Ambiente (Recomendado)

### Windows (PowerShell)
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\apache-maven", "User")
[Environment]::SetEnvironmentVariable("PATH", "$env:PATH;C:\Program Files\Java\jdk-17\bin;C:\apache-maven\bin", "User")

# Verificar
$env:JAVA_HOME
$env:MAVEN_HOME
```

### Linux/macOS (.bashrc ou .zshrc)
```bash
export JAVA_HOME=/usr/libexec/java_home -v 17
export MAVEN_HOME=/opt/maven
export PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH"
```

---

## 🔧 Configuração Maven (settings.xml)

Arquivo: `~/.m2/settings.xml`

**Criar se não existir:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <repositories>
        <repository>
            <id>central</id>
            <url>https://repo.maven.apache.org/maven2</url>
        </repository>
    </repositories>
    
    <pluginRepositories>
        <pluginRepository>
            <id>central</id>
            <url>https://repo.maven.apache.org/maven2</url>
        </pluginRepository>
    </pluginRepositories>
</settings>
```

---

## ⚡ Atalhos Úteis no Terminal

```bash
# Ir para pasta do projeto
cd c:\projetos\osmanager

# Compilar
mvn clean compile

# Testar
mvn test

# Build
mvn package

# Limpar tudo
mvn clean
```

---

## 🎯 Próximo Passo

Após configurar tudo, leia:
- [Setup Inicial](./setup-inicial.md)
- [Documentação](../README.md)

---

**Dúvidas?** Ver: `docs/troubleshooting/ide-configuration.md`

