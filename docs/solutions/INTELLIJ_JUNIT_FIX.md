# FIX: Cannot Resolve Symbol 'junit'

## Problema
Erro: `cannot resolve symbol 'junit'` no IntelliJ IDEA

## Causa
- Dependências JUnit não foram baixadas pelo Maven
- Cache do IntelliJ corrompido
- Versão incorreta do Java sendo usada

## Solução

### 1. Verificar Java 17
```bash
java -version
```
Deve mostrar: `java version "17.x.x"`

### 2. Limpar cache do Maven
```bash
mvn clean
```

### 3. Baixar dependências
```bash
mvn dependency:resolve
```

### 4. Invalidar cache do IntelliJ
- File → Invalidate Caches / Restart
- Selecionar "Invalidate and Restart"

### 5. Reimportar projeto
- View → Tool Windows → Maven
- Clique no ícone de reimportar (setas circulares)

### 6. Verificar pom.xml
Certifique-se de que as dependências estão corretas:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

### 7. Compilar projeto
```bash
mvn compile test-compile
```

## Verificação
Após os passos acima, o erro deve desaparecer. Se persistir:
- Verifique se o JDK do projeto está configurado para Java 17
- Reinicie o IntelliJ
- Delete a pasta `.idea` e reabra o projeto

## Data da Solução
21 de Março de 2026

## Status
✅ Resolvido</content>
<parameter name="filePath">C:\projetos\osmanager\docs\solutions\INTELLIJ_JUNIT_FIX.md
