# 🚀 QUICK START - Docker OSManager

## ⚡ Início em 3 passos

### 1️⃣ Copiar arquivo de configuração
```bash
cp .env.example .env
```

### 2️⃣ Iniciar o Docker
```bash
# Windows
.\scripts\docker-start.bat

# Linux/Mac
bash scripts/docker-start.sh

# Ou manual
docker-compose up -d
```

### 3️⃣ Acessar a aplicação
```
http://localhost:8080
```

---

## 📊 URLs Disponíveis

| Serviço | URL | Login |
|---------|-----|-------|
| **Aplicação** | http://localhost:8080 | - |
| **phpMyAdmin** | http://localhost:8081 | root/root_password |
| **MySQL** | localhost:3306 | osmuser/osmuser_password |

---

## 🔧 Comandos Úteis

```bash
# Ver status
docker-compose ps

# Ver logs
docker-compose logs -f app
docker-compose logs -f mysql

# Parar
docker-compose down

# Limpar (cuidado - deleta dados!)
docker-compose down -v

# Recompilar
docker-compose up -d --build

# Terminal no container
docker-compose exec app bash
```

---

## 📝 Alterar Configurações

Edite o arquivo `.env`:

```env
MYSQL_ROOT_PASSWORD=sua_senha_nova
TOMCAT_PORT=8080
PHPMYADMIN_PORT=8081
```

Depois reinicie:
```bash
docker-compose restart
```

---

## 🔍 Verificar Logs

```bash
# Logs de erro da aplicação
docker-compose logs app | grep -i error

# Logs do banco de dados
docker-compose logs mysql | tail -20

# Logs em tempo real
docker-compose logs -f
```

---

## ✅ Verificar se está funcionando

```bash
# Tomcat respondendo?
curl http://localhost:8080

# MySQL respondendo?
docker-compose exec mysql mysql -u root -proot_password -e "SELECT VERSION();"
```

---

## 📚 Documentação Completa

Ver: `docs/DOCKER_GUIDE.md`

---

**Status:** ✅ Pronto para usar!

