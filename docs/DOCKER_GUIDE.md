# 🐳 Guia Docker - OSManager

## 📋 Índice
1. [Pré-requisitos](#pré-requisitos)
2. [Início Rápido](#início-rápido)
3. [Comandos Docker](#comandos-docker)
4. [Configurações](#configurações)
5. [Troubleshooting](#troubleshooting)

---

## 🔧 Pré-requisitos

Você precisa ter instalado:

### Windows
```powershell
# 1. Instalar Docker Desktop
# https://www.docker.com/products/docker-desktop

# 2. Verificar instalação
docker --version
docker-compose --version
```

### Mac/Linux
```bash
# 1. Instalar Docker e Docker Compose
sudo apt-get install docker.io docker-compose  # Linux
brew install docker docker-compose  # Mac

# 2. Verificar
docker --version
docker-compose --version
```

---

## 🚀 Início Rápido

### 1. Iniciar os Serviços

```bash
# Construir as imagens e iniciar os containers
docker-compose up -d

# Ver os logs em tempo real
docker-compose logs -f

# Aguarde até ver: "Catalina started"
```

### 2. Acessar a Aplicação

- **Aplicação:** http://localhost:8080
- **phpMyAdmin:** http://localhost:8081
- **Banco de dados:** localhost:3306

### 3. Credenciais

**MySQL:**
```
Host: localhost (ou mysql do Docker)
Username: osmuser
Password: osmuser_password
Database: osmanager
```

**phpMyAdmin:**
```
Username: root
Password: root_password
```

---

## 🐳 Comandos Docker

### Iniciar/Parar

```bash
# Iniciar containers em background
docker-compose up -d

# Parar containers (sem deletar)
docker-compose down

# Parar e remover volumes (cuidado! deleta dados)
docker-compose down -v

# Reiniciar
docker-compose restart

# Restart específico
docker-compose restart app
docker-compose restart mysql
```

### Verificar Status

```bash
# Ver containers rodando
docker-compose ps

# Ver logs
docker-compose logs
docker-compose logs app
docker-compose logs mysql

# Ver logs em tempo real (últimas 100 linhas)
docker-compose logs -f -n 100

# Verificar uso de recursos
docker stats
```

### Executar Comandos

```bash
# Acessar terminal do container app
docker-compose exec app bash

# Acessar terminal do MySQL
docker-compose exec mysql bash

# Executar comando no container
docker-compose exec app ls -la

# Recompilar projeto dentro do container
docker-compose exec app mvn clean package
```

### Limpeza

```bash
# Remover containers inativos
docker-compose down

# Remover imagens não utilizadas
docker image prune

# Remover volumes não utilizados
docker volume prune

# Limpeza completa (cuidado!)
docker-compose down -v && docker system prune -a
```

---

## ⚙️ Configurações

### Variáveis de Ambiente

Edite `docker-compose.yml` para alterar:

#### MySQL
```yaml
environment:
  MYSQL_ROOT_PASSWORD: root_password      # Senha root
  MYSQL_USER: osmuser                      # Usuário
  MYSQL_PASSWORD: osmuser_password         # Senha do usuário
  MYSQL_DATABASE: osmanager                # Nome do banco
```

#### Tomcat/App
```yaml
environment:
  JAVA_OPTS: "-Xmx512m -Xms256m"          # Memória JVM
  DB_HOST: mysql                           # Host do banco
  APP_ENV: production                      # Ambiente
```

### Portas

No `docker-compose.yml`:

```yaml
ports:
  - "8080:8080"    # Tomcat
  - "3306:3306"    # MySQL
  - "8081:80"      # phpMyAdmin
```

Altere o número da esquerda para mudar a porta externa.

### Volumes (Persistência)

```yaml
volumes:
  mysql_data:/var/lib/mysql          # Dados do MySQL
  app_logs:/usr/local/tomcat/logs    # Logs da app
```

Locais no host:
```bash
# Linux/Mac
ls -la /var/lib/docker/volumes/

# Windows
\\wsl$\docker-desktop-data\version-pack-data\community\docker\volumes\
```

---

## 🔍 Verificações e Testes

### Verificar Conectividade

```bash
# MySQL está respondendo?
docker-compose exec app mysql -h mysql -u osmuser -p -e "SELECT VERSION();"

# Tomcat está rodando?
curl http://localhost:8080

# phpMyAdmin
curl http://localhost:8081
```

### Testar Aplicação

```bash
# Acessar logs de erro
docker-compose logs app | grep -i error

# Verificar saúde dos containers
docker-compose exec app curl http://localhost:8080/health
```

---

## 🐛 Troubleshooting

### "Cannot connect to Docker daemon"

```bash
# Windows
# Abra Docker Desktop

# Linux
sudo systemctl start docker
sudo usermod -aG docker $USER
```

### "Port already in use"

```bash
# Ver qual processo usa a porta
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Mac/Linux

# Mudar porta no docker-compose.yml
ports:
  - "8080:8080"  →  "8082:8080"
```

### "MySQL connection refused"

```bash
# Verificar se MySQL está saudável
docker-compose ps mysql

# Ver logs do MySQL
docker-compose logs mysql

# Reiniciar MySQL
docker-compose restart mysql

# Aguarde 30 segundos e tente novamente
```

### "Application not accessible"

```bash
# Verificar logs de erro
docker-compose logs app | tail -50

# Acessar container para debugar
docker-compose exec app bash
cd /usr/local/tomcat/logs
tail -f catalina.out
```

### Permissões negadas

```bash
# Linux - executar com sudo
sudo docker-compose up -d

# Ou adicionar seu usuário ao grupo docker
sudo usermod -aG docker $USER
```

### Limpeza após erro

```bash
# Parar tudo
docker-compose down

# Remover volumes (cuidado!)
docker volume prune

# Reconstruir tudo
docker-compose up -d --build
```

---

## 📊 Monitoramento

### Ver uso de recursos

```bash
# Em tempo real
docker stats

# Para um container específico
docker stats osmanager-app
```

### Ver histórico de logs

```bash
# Últimas 100 linhas
docker-compose logs --tail=100

# Seguir logs em tempo real
docker-compose logs -f

# Apenas do app
docker-compose logs app --tail=50 -f
```

---

## 🔐 Segurança

### Alterar Senhas (IMPORTANTE!)

1. **Edite `docker-compose.yml`:**
   ```yaml
   MYSQL_ROOT_PASSWORD: nova_senha_root
   MYSQL_PASSWORD: nova_senha_user
   ```

2. **Remova volumes antigos:**
   ```bash
   docker-compose down -v
   ```

3. **Reinicie:**
   ```bash
   docker-compose up -d
   ```

### Não commit de .env

Se usar arquivo `.env`:
```bash
# Adicione ao .gitignore
echo ".env" >> .gitignore
echo ".env.local" >> .gitignore
```

---

## 📝 Exemplos de Uso

### Desenvolvimento com hot reload

```bash
# Execute com volumes do código
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### Backup do Banco de Dados

```bash
# Fazer dump
docker-compose exec mysql mysqldump -u root -proot_password osmanager > backup.sql

# Restaurar
docker-compose exec -T mysql mysql -u root -proot_password osmanager < backup.sql
```

### Atualizar Aplicação

```bash
# Recompilar e reimplantar
docker-compose down
docker-compose up -d --build
```

---

## 📚 Documentação Adicional

- [Docker Compose Official Docs](https://docs.docker.com/compose/)
- [Docker CLI Reference](https://docs.docker.com/engine/reference/commandline/cli/)
- [Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Data:** 21 de Março de 2026
**Status:** ✅ Pronto para produção

