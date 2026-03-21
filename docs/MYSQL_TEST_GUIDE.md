# 🗄️ Guia: Testar Banco de Dados MySQL

## 📋 Situação Atual
- ✅ Java 17: OK
- ✅ Maven: OK
- ✅ Tomcat 9: OK
- ❌ MySQL: **NÃO INSTALADO**

## 🚀 Opções para Testar o Banco

### Opção 1: Usar Docker (Recomendado - Mais Rápido)

```bash
# 1. Iniciar apenas o MySQL com Docker
docker run --name mysql-test -e MYSQL_ROOT_PASSWORD=root_password -e MYSQL_DATABASE=osmanager -p 3306:3306 -d mysql:8.0

# 2. Aguardar inicialização
sleep 10

# 3. Testar conexão
docker exec -it mysql-test mysql -u root -proot_password -e "SELECT VERSION();"

# 4. Criar tabelas
docker exec -i mysql-test mysql -u root -proot_password osmanager < scripts/create_database.sql

# 5. Verificar tabelas
docker exec -it mysql-test mysql -u root -proot_password osmanager -e "SHOW TABLES;"

# 6. Parar e remover
docker stop mysql-test && docker rm mysql-test
```

### Opção 2: Instalar MySQL Local (Completo)

#### Download e Instalação

1. **Baixar MySQL Installer:**
   - Acesse: https://dev.mysql.com/downloads/mysql/
   - Baixe: `mysql-installer-community-8.0.x.msi`

2. **Executar Instalador:**
   - Tipo: `Developer Default`
   - Config Type: `Development Machine`
   - Port: `3306`
   - Usuário: `root`
   - Senha: `root_password`

3. **Iniciar Serviço:**
   ```bash
   net start MySQL80
   ```

#### Testar Instalação

```bash
# Verificar versão
mysql -u root -proot_password -e "SELECT VERSION();"

# Criar banco
mysql -u root -proot_password < scripts/create_database.sql

# Verificar tabelas
mysql -u root -proot_password -e "USE osmanager; SHOW TABLES;"
```

### Opção 3: Usar Docker Compose (Ambiente Completo)

```bash
# 1. Copiar arquivo de configuração
cp .env.example .env

# 2. Iniciar apenas MySQL
docker-compose up -d mysql

# 3. Aguardar
sleep 15

# 4. Testar
docker-compose exec mysql mysql -u root -proot_password -e "SELECT VERSION();"

# 5. Criar banco
docker-compose exec -T mysql mysql -u root -proot_password osmanager < scripts/create_database.sql

# 6. Verificar
docker-compose exec mysql mysql -u root -proot_password osmanager -e "SHOW TABLES;"

# 7. Parar
docker-compose down
```

---

## 🔍 Scripts de Teste

### Teste Básico de Conexão

```bash
# Criar arquivo test_mysql.sql
echo "SELECT VERSION() AS 'MySQL Version';" > test_mysql.sql
echo "SHOW DATABASES;" >> test_mysql.sql

# Executar teste
mysql -u root -proot_password < test_mysql.sql
```

### Teste do Banco OSManager

```bash
# Conectar ao banco
mysql -u root -proot_password osmanager

# Dentro do MySQL:
SHOW TABLES;
DESCRIBE clients;
SELECT COUNT(*) FROM clients;
EXIT;
```

---

## ⚙️ Configuração da Aplicação

### Arquivo: `src/main/resources/application.properties`

```properties
# MySQL Local
db.url=jdbc:mysql://localhost:3306/osmanager?serverTimezone=America/Sao_Paulo&useSSL=false&allowPublicKeyRetrieval=true
db.username=root
db.password=root_password

# MySQL Docker
# db.url=jdbc:mysql://localhost:3306/osmanager?serverTimezone=America/Sao_Paulo&useSSL=false&allowPublicKeyRetrieval=true
# db.username=osmuser
# db.password=osmuser_password
```

### Arquivo: `src/main/resources/META-INF/persistence.xml`

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/osmanager?serverTimezone=America/Sao_Paulo"/>
<property name="javax.persistence.jdbc.user" value="root"/>
<property name="javax.persistence.jdbc.password" value="root_password"/>
```

---

## 🧪 Testes da Aplicação

### 1. Compilar e Testar

```bash
# Compilar
mvn clean compile

# Executar testes unitários
mvn test

# Gerar WAR
mvn package -DskipTests
```

### 2. Deploy no Tomcat

```bash
# Parar Tomcat
C:\WorkSpace\ApacheTomCat\apache-tomcat-9.0.115\bin\shutdown.bat

# Copiar WAR
copy target\osmanager-1.0-SNAPSHOT.war "C:\WorkSpace\ApacheTomCat\apache-tomcat-9.0.115\webapps\ROOT.war"

# Iniciar Tomcat
C:\WorkSpace\ApacheTomCat\apache-tomcat-9.0.115\bin\startup.bat
```

### 3. Verificar Logs

```bash
# Logs do Tomcat
type "C:\WorkSpace\ApacheTomCat\apache-tomcat-9.0.115\logs\catalina.out" | findstr -i error

# Logs da aplicação
type "C:\WorkSpace\ApacheTomCat\apache-tomcat-9.0.115\logs\catalina.out" | findstr -i hibernate
```

---

## 🔧 Troubleshooting

### "Access denied for user"

```bash
# Resetar senha root
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nova_senha';
FLUSH PRIVILEGES;
```

### "Unknown database 'osmanager'"

```bash
# Criar banco manualmente
mysql -u root -proot_password -e "CREATE DATABASE osmanager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Depois executar o script
mysql -u root -proot_password osmanager < scripts/create_database.sql
```

### "Port 3306 already in use"

```bash
# Ver qual processo usa a porta
netstat -ano | findstr :3306

# Matar processo
taskkill /PID <PID> /F
```

### "Connection refused"

```bash
# Verificar se MySQL está rodando
net start MySQL80

# Ou com Docker
docker ps | findstr mysql
```

---

## 📊 Verificação Final

Execute o script de validação:

```bash
.\scripts\validate-environment.ps1
```

Deve aparecer:
```
✅ Java 17 OK
✅ Maven OK
✅ Tomcat OK
✅ MySQL OK
✅ Projeto OK
```

---

**Recomendação:** Comece com **Docker** para testar rapidamente, depois instale MySQL local se precisar de desenvolvimento persistente.

