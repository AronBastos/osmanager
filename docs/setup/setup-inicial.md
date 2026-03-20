# Setup Inicial - OSManager

Como começar a trabalhar com o projeto OSManager do zero.

---

## ✅ Pré-requisitos

Você precisa ter instalado:

- **Java 17** (ou superior)
- **Maven 3.8.1+**
- **MySQL 8.0+** (ou MariaDB)
- **Git**
- **IntelliJ IDEA** (ou IDE Java compatível)

---

## 🚀 Passo 1: Clonar/Baixar o Projeto

```bash
# Se usando Git
git clone <url-do-repositorio>
cd osmanager

# Ou se já tem a pasta
cd c:\projetos\osmanager
```

---

## 🔧 Passo 2: Configurar Dependências

### 2.1 Verificar Java
```bash
java -version
# Deve mostrar: java 17.x.x
```

### 2.2 Verificar Maven
```bash
mvn -version
# Deve mostrar: Apache Maven 3.8.1+
```

### 2.3 Baixar Dependências do Projeto
```bash
mvn clean install
```

---

## 💾 Passo 3: Configurar Banco de Dados

### 3.1 Criar Banco MySQL
```bash
mysql -u root -p < scripts/create_database.sql
```

### 3.2 Atualizar Credenciais
Edite: `src/main/resources/META-INF/persistence.xml`

```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:mysql://localhost:3306/osmanager?serverTimezone=America/Sao_Paulo"/>
<property name="javax.persistence.jdbc.user" value="seu_usuario"/>
<property name="javax.persistence.jdbc.password" value="sua_senha"/>
```

---

## 🎯 Passo 4: Compilar e Validar

```bash
# Compilar
mvn clean compile

# Rodar testes
mvn test

# Gerar WAR
mvn package
```

**Resultado esperado:** BUILD SUCCESS

---

## 🖥️ Passo 5: Configurar IDE (IntelliJ IDEA)

1. **Abrir projeto**
   - File → Open → Selecione pasta `c:\projetos\osmanager`

2. **Configurar SDK**
   - File → Project Structure → Project
   - Selecione Java 17 em "Project SDK"

3. **Marcar Folders**
   - Clique direito em `src/main/java` → Mark Directory as → Sources Root
   - Clique direito em `src/test/java` → Mark Directory as → Test Sources Root
   - Clique direito em `src/main/resources` → Mark Directory as → Resources Root

4. **Reload Maven**
   - Maven panel (direita) → Clique no ícone Reload (↻)

5. **Invalidar Cache** (se tiver erros)
   - Ctrl+Shift+I → Invalidate and Restart

---

## ✨ Passo 6: Primeiro Build

No IntelliJ IDEA:
1. Build → Build Project (Ctrl+F9)
2. Aguarde compilar
3. Não deve ter erros em vermelho

---

## 🚀 Passo 7: Entender a Estrutura

Leia (nessa ordem):
1. `docs/README.md` - Visão geral
2. `docs/MAPA.md` - Guia visual
3. `docs/guias/estrutura-projeto.md` - Estrutura
4. `docs/guias/padroes-utilizados.md` - Padrões

---

## ✅ Verificação Final

Marque quando completar:

- [ ] Java 17 instalado e verificado
- [ ] Maven 3.8.1+ instalado e verificado
- [ ] MySQL 8.0+ instalado e verificado
- [ ] Banco de dados criado
- [ ] `mvn clean install` executado com sucesso
- [ ] IDE configurada com SDK Java 17
- [ ] Folders marcados corretamente
- [ ] `mvn clean compile` sem erros
- [ ] `mvn test` passando

---

## 🐛 Problemas Comuns

### "Cannot resolve symbol 'junit'"
→ Ver: `docs/troubleshooting/junit-error-fix.md`

### Erro de conexão com MySQL
→ Ver: `docs/troubleshooting/connection-issues.md`

### Erro ao compilar
→ Ver: `docs/troubleshooting/build-errors.md`

---

## 🎉 Pronto!

Seu projeto está configurado! Próximas ações:

1. Explore `docs/guias/estrutura-projeto.md`
2. Aprenda os padrões em `docs/guias/padroes-utilizados.md`
3. Comece a programar!

---

**Próximo**: [Documentação Completa](../README.md)

