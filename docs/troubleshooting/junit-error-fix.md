# 🔧 Guia Prático: Resolver "Cannot resolve symbol 'junit'" no IntelliJ IDEA

## ⚡ Quick Fix (30 segundos)

### Passo 1: Invalidar Cache
```
Ctrl + Shift + I → Invalidate Caches... → Invalidate and Restart
```

### Passo 2: Recarregar Maven
1. Abra o painel Maven (lado direito da tela)
2. Clique no ícone de **Reload** (↻)

### Passo 3: Marcar pasta de testes
1. Clique com botão direito em `src/test/java`
2. **Mark Directory as** → **Test Sources Root**

---

## 📋 Passo a Passo Detalhado

### **1. Executar Maven no Terminal**

Abra PowerShell e execute:
```powershell
cd C:\projetos\osmanager
mvn clean install
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXs
```

---

### **2. No IntelliJ IDEA**

#### **A. Invalidar Caches**
- Menu: **File** → **Invalidate Caches**
- Selecione: ✓ Invalidate and Restart
- Clique: **Invalidate and Restart**

O IDE vai fechar e reabrir automaticamente (aguarde ~2 minutos)

#### **B. Recarregar Projetos Maven**
Após o restart:
1. Abra o painel Maven (View → Tool Windows → Maven)
2. No painel Maven, clique no botão **Reload** (↻)
3. Aguarde a indexação completar

#### **C. Marcar Pastas como Source Roots**

No Project Explorer (esquerda):

1. **src/main/java** → Clique direito → **Mark Directory as** → **Sources Root**
2. **src/test/java** → Clique direito → **Mark Directory as** → **Test Sources Root**
3. **src/main/resources** → Clique direito → **Mark Directory as** → **Resources Root**
4. **src/test/resources** → Clique direito → **Mark Directory as** → **Test Resources Root**

---

### **3. Verificar SDK do Java**

1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. Vá para **Project** (lado esquerdo)
3. Em **Project SDK**, verifique se está Java 17
4. Se não estiver:
   - Clique em **Edit** ou em **[None]**
   - Selecione **C:\Program Files\Java\jdk-17** (ou seu caminho)
   - Clique **OK**

---

### **4. Sincronizar com Maven**

No painel Maven (lado direito):
1. Clique com botão direito no projeto **osmanager**
2. Selecione **Reload Projects**
3. Aguarde completar

---

## ✅ Como Saber se Funcionou

Após seguir os passos, você verá:

✓ Imports em verde (sem erro):
```java
import org.junit.jupiter.api.BeforeEach;  // ✓ Verde
import org.mockito.Mock;                  // ✓ Verde
```

✓ Métodos reconhecidos:
```java
@Mock           // ✓ Reconhecido
@BeforeEach     // ✓ Reconhecido
@Test           // ✓ Reconhecido
```

✓ Sugestões de autocompletar funcionam

---

## 🐛 Se Ainda Não Funcionar

### Opção 1: Limpar e Revalidar
```powershell
# Terminal
cd C:\projetos\osmanager
rmdir /S /Q target
mvn clean install
```

Depois no IDE:
- Ctrl + Shift + I (Invalidate Caches and Restart)

### Opção 2: Reimportar Projeto
1. **File** → **Close Project**
2. **File** → **Open** → Selecione a pasta `c:\projetos\osmanager`
3. Aguarde o Maven indexar

### Opção 3: Forçar Atualizar IDE
```powershell
# Terminal
cd C:\projetos\osmanager
mvn idea:idea
```

Depois reabra o projeto no IntelliJ.

---

## 📚 Verificação em Terminal

Se quiser confirmar que tudo está OK via terminal:

```powershell
# Compilar
mvn compile
# ✓ Deve dar BUILD SUCCESS

# Rodar testes
mvn test
# ✓ Deve dar BUILD SUCCESS

# Ver dependências
mvn dependency:tree | Select-String "junit"
# ✓ Deve listar junit-jupiter-api e junit-jupiter-engine
```

---

## 🎯 Resumo Rápido

| Ação | Comando/Menu |
|------|---|
| Baixar deps | `mvn clean install` |
| Invalidar cache | Ctrl+Shift+I → Invalidate and Restart |
| Recarregar Maven | Maven panel → Reload (↻) |
| Marcar sources | Clique direito → Mark as → Sources Root |
| Verificar SDK | File → Project Structure → Java 17 |

---

## ✨ Pronto!

Após executar estes passos, o erro de "Cannot resolve symbol 'junit'" deve desaparecer e todos os imports funcionarão normalmente.

**Happy coding! 🚀**

