# 🗺️ Mapa de Documentação - OSManager

Guia visual para encontrar rapidamente o que você precisa!

---

## 🎯 ENCONTRE O QUE PRECISA

### 🚨 "Estou com ERRO..."

```
┌─ Cannot resolve symbol 'junit'?
│  └─► docs/troubleshooting/junit-error-fix.md ⭐
│
├─ Erro ao compilar?
│  └─► docs/troubleshooting/build-errors.md
│
├─ Erro ao fazer build?
│  └─► docs/troubleshooting/maven-issues.md
│
├─ Erro ao conectar banco?
│  └─► docs/troubleshooting/connection-issues.md
│
├─ Erro ao fazer deploy?
│  └─► docs/troubleshooting/deploy-issues.md
│
└─ Algum outro erro?
   └─► Procure em docs/troubleshooting/
```

---

### 🆕 "Sou NOVO no projeto..."

```
┌─ Nunca mexeu no projeto?
│  └─► docs/setup/setup-inicial.md
│     └─ Leia tudo!
│
├─ Configurou? Agora quer entender a estrutura?
│  └─► docs/guias/estrutura-projeto.md
│     └─ Leia tudo, é importante!
│
├─ Entendeu a estrutura? Quer saber os padrões?
│  └─► docs/guias/padroes-utilizados.md
│     └─ Leia especialmente as seções que vai usar
│
└─ Pronto! Agora pode começar a programar 🚀
   └─► docs/guias/nova-feature.md
```

---

### 🧪 "Quero ESCREVER TESTES..."

```
┌─ Teste unitário (sem banco)?
│  └─► docs/guias/testes-unitarios.md
│     └─ Use Mockito → docs/guias/mockito-guide.md
│
├─ Teste de integração (com banco)?
│  └─► docs/guias/testes-integracao.md
│
└─ Teste REST API?
   └─► docs/guias/testes-unitarios.md
      └─ Seção sobre REST
```

---

### 🚀 "Quero FAZER DEPLOY..."

```
┌─ Deploy local para testar?
│  └─► docs/guias/deploy-local.md
│     └─ Teste tudo antes!
│
├─ Deploy em produção?
│  └─► docs/guias/deploy-producao.md
│     └─ Leia o checklist de segurança
│
└─ Deu erro no deploy?
   └─► docs/troubleshooting/deploy-issues.md
```

---

### ➕ "Quero ADICIONAR NOVO RECURSO..."

```
┌─ Entendo a estrutura?
│  Sim? → Próximo
│  Não? → docs/guias/estrutura-projeto.md
│
├─ Entendo os padrões?
│  Sim? → Próximo
│  Não? → docs/guias/padroes-utilizados.md
│
├─ Pronto? Segue este roteiro:
│  └─► docs/guias/nova-feature.md
│     └─ 1. Criar Entity (model/)
│        2. Criar DAO (dao/)
│        3. Criar Service (service/)
│        4. Criar Controller (controller/)
│        5. Criar View (webapp/)
│        6. Criar Testes (test/)
│
└─ Escrever testes?
   └─► docs/guias/testes-unitarios.md
```

---

### 📖 "Preciso APRENDER..."

```
├─ Estrutura do projeto?
│  └─► docs/guias/estrutura-projeto.md
│
├─ Padrões de código?
│  └─► docs/guias/padroes-utilizados.md
│
├─ Boas práticas?
│  └─► docs/guias/boas-praticas.md
│
├─ Como escrever testes?
│  └─► docs/guias/testes-unitarios.md
│      docs/guias/mockito-guide.md
│
└─ Referências técnicas?
   └─► docs/referencias/
```

---

### 🔧 "Preciso CONFIGURAR..."

```
├─ Ambiente de desenvolvimento?
│  └─► docs/setup/dev-environment.md
│
├─ Projeto (persistence.xml, web.xml, etc)?
│  └─► docs/setup/configuracoes.md
│
└─ IDE (IntelliJ IDEA)?
   └─► docs/troubleshooting/ide-configuration.md
```

---

## 📂 ESTRUTURA COMPLETA

```
docs/
│
├── README.md                    ◄─ COMECE AQUI! Índice principal
├── SOLUTIONS_INDEX.md           ◄─ Índice de soluções
├── MAPA.md                      ◄─ Este arquivo
│
├── setup/                       ◄─ CONFIGURAÇÃO INICIAL
│   ├── setup-inicial.md         • Como começar
│   ├── dev-environment.md       • Instalar Java, Maven, MySQL
│   └── configuracoes.md         • Arquivos de config
│
├── guias/                       ◄─ GUIAS PRÁTICOS
│   ├── estrutura-projeto.md     • Como o projeto é organizado ⭐
│   ├── padroes-utilizados.md    • Padrões de design ⭐
│   ├── nova-feature.md          • Adicionar novo recurso
│   ├── boas-praticas.md         • Recomendações
│   ├── testes-unitarios.md      • Testes unitários
│   ├── testes-integracao.md     • Testes com banco
│   ├── mockito-guide.md         • Como usar Mockito
│   ├── deploy-local.md          • Deploy local
│   └── deploy-producao.md       • Deploy produção
│
├── troubleshooting/             ◄─ SOLUÇÕES DE PROBLEMAS
│   ├── junit-error-fix.md       • Cannot resolve symbol 'junit' ⭐⭐⭐
│   ├── maven-issues.md          • Problemas com Maven
│   ├── ide-configuration.md     • Configuração IntelliJ IDEA
│   ├── database-setup.md        • Setup MySQL
│   ├── connection-issues.md     • Erro de conexão
│   ├── build-errors.md          • Erros de compilação
│   └── deploy-issues.md         • Problemas de deploy
│
└── referencias/                 ◄─ REFERÊNCIAS
    ├── links-uteis.md           • Documentação oficial
    ├── glossario.md             • Termos técnicos
    └── changelog.md             • Histórico de mudanças
```

---

## ⭐ ARQUIVOS MAIS IMPORTANTES

**Leia OBRIGATORIAMENTE:**

1. **[README.md](./README.md)** - Visão geral (5 min)
2. **[estrutura-projeto.md](./guias/estrutura-projeto.md)** - Como está organizado (15 min)
3. **[padroes-utilizados.md](./guias/padroes-utilizados.md)** - Padrões de código (20 min)

**Em caso de erro:**

1. **[junit-error-fix.md](./troubleshooting/junit-error-fix.md)** - Erro mais comum ⭐⭐⭐
2. **[Busque na pasta troubleshooting/](./troubleshooting/)** - Outros problemas

---

## 🎯 EXEMPLOS RÁPIDOS

### "Como criar um novo DAO?"
→ docs/guias/estrutura-projeto.md (seção dao/)
→ docs/guias/padroes-utilizados.md (seção DAO Pattern)

### "Como escrever um teste?"
→ docs/guias/testes-unitarios.md
→ docs/guias/mockito-guide.md

### "Como adicionar uma tabela no banco?"
→ docs/guias/nova-feature.md
→ docs/troubleshooting/database-setup.md

### "Como fazer deploy?"
→ docs/guias/deploy-local.md (teste)
→ docs/guias/deploy-producao.md (produção)

---

## 📱 MOBILE-FRIENDLY TIPS

Se está no celular/tablet:

1. Abra `docs/README.md` no browser
2. Use Ctrl+F (Cmd+F) para buscar
3. Clique nos links em azul
4. Use "Back" para voltar

---

## 🚀 PRIMEIROS PASSOS

```
1. Abra: docs/README.md (início)
        ↓
2. Siga para: docs/setup/setup-inicial.md (20 min)
        ↓
3. Depois: docs/guias/estrutura-projeto.md (15 min)
        ↓
4. Por fim: docs/guias/padroes-utilizados.md (20 min)
        ↓
5. PRONTO! Você entende o projeto! ✅
        ↓
6. docs/guias/nova-feature.md (Criar seu primeiro recurso)
```

---

## ❓ NÃO ENCONTROU?

1. **Use Ctrl+F** em cada arquivo para buscar palavras-chave
2. **Veja em troubleshooting/** - Problemas comuns
3. **Consulte docs/referencias/links-uteis.md** - Documentação oficial
4. **Abra uma issue** - Se realmente não encontrou

---

## 🔄 COMO NAVEGAR ENTRE DOCS

Cada documento tem links para:
- **Próximo** - Próximo passo recomendado
- **Voltar** - Documento anterior
- **Ver também** - Documentos relacionados

Aproveite os links internos! 🔗

---

## 💡 DICA PROFISSIONAL

Salve os 3 documentos mais importantes nos favoritos:

1. docs/README.md
2. docs/guias/estrutura-projeto.md
3. docs/troubleshooting/junit-error-fix.md

Você vai acessar muito! 📌

---

**Versão**: 1.0  
**Última atualização**: 20 de Março de 2026  
**Status**: ✅ Completo e funcional

**Próximo**: [README.md](./README.md) ← Comece por aqui!

