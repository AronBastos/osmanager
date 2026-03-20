# 📚 Documentação OSManager

Bem-vindo à documentação centralizada do projeto OSManager! Aqui você encontrará guias, soluções e referências.

## 🚀 Início Rápido

- **[Setup Inicial](./setup/setup-inicial.md)** - Como começar do zero
- **[Estrutura do Projeto](./guias/estrutura-projeto.md)** - Entender a organização das pastas
- **[Padrões Utilizados](./guias/padroes-utilizados.md)** - Convenções e arquitetura

---

## 🔧 Troubleshooting

Tendo problemas? Consulte os guias de solução:

### IDE & Maven
- **[Cannot resolve symbol 'junit'](./troubleshooting/junit-error-fix.md)** - ⭐ Problema mais comum
- **[Maven Issues](./troubleshooting/maven-issues.md)** - Problemas com dependências
- **[IDE Configuration](./troubleshooting/ide-configuration.md)** - Configurar IntelliJ IDEA

### Banco de Dados
- **[Database Setup](./troubleshooting/database-setup.md)** - Configurar MySQL
- **[Connection Issues](./troubleshooting/connection-issues.md)** - Erro de conexão

### Build & Deploy
- **[Build Errors](./troubleshooting/build-errors.md)** - Erros ao compilar
- **[Deploy Issues](./troubleshooting/deploy-issues.md)** - Problemas ao fazer deploy

---

## 📖 Guias Completos

### Configuração
1. **[Setup Inicial](./setup/setup-inicial.md)** - Instalação e primeiras configurações
2. **[Ambiente de Desenvolvimento](./setup/dev-environment.md)** - IDE, Java, Maven, MySQL
3. **[Configurações do Projeto](./setup/configuracoes.md)** - persistence.xml, properties, etc

### Desenvolvimento
1. **[Estrutura do Projeto](./guias/estrutura-projeto.md)** - Como o projeto está organizado
2. **[Padrões de Código](./guias/padroes-utilizados.md)** - DAO, Service, Controller, etc
3. **[Como Adicionar Novas Features](./guias/nova-feature.md)** - Passo a passo prático
4. **[Boas Práticas](./guias/boas-praticas.md)** - Recomendações

### Testes
1. **[Testes Unitários](./guias/testes-unitarios.md)** - Como escrever testes
2. **[Testes de Integração](./guias/testes-integracao.md)** - Testar com banco de dados
3. **[Mockito Guide](./guias/mockito-guide.md)** - Usando mocks nos testes

### Deploy
1. **[Deploy Local](./guias/deploy-local.md)** - Rodar no Tomcat local
2. **[Deploy em Produção](./guias/deploy-producao.md)** - Boas práticas para prod
3. **[Troubleshooting Deploy](./troubleshooting/deploy-issues.md)** - Problemas comuns

---

## 📚 Referências

- **[Links Úteis](./referencias/links-uteis.md)** - Documentação oficial, tutoriais, ferramentas
- **[Glossário](./referencias/glossario.md)** - Termos e abreviações usadas
- **[Changelog](./referencias/CHANGELOG.md)** - Histórico de alterações

---

## 🗂️ Estrutura desta Documentação

```
docs/
├── README.md                              # Este arquivo
├── setup/
│   ├── setup-inicial.md                  # Guia de início
│   ├── dev-environment.md                # Configurar ambiente
│   └── configuracoes.md                  # Arquivos de config
├── guias/
│   ├── estrutura-projeto.md              # Organização das pastas
│   ├── padroes-utilizados.md             # Padrões de arquitetura
│   ├── nova-feature.md                   # Como adicionar features
│   ├── boas-praticas.md                  # Recomendações
│   ├── testes-unitarios.md               # Testes unitários
│   ├── testes-integracao.md              # Testes de integração
│   ├── mockito-guide.md                  # Mockito
│   ├── deploy-local.md                   # Deploy local
│   └── deploy-producao.md                # Deploy prod
├── troubleshooting/
│   ├── junit-error-fix.md                # ⭐ JUnit error
│   ├── maven-issues.md                   # Maven
│   ├── ide-configuration.md              # IntelliJ IDEA
│   ├── database-setup.md                 # MySQL
│   ├── connection-issues.md              # Conexão
│   ├── build-errors.md                   # Build
│   └── deploy-issues.md                  # Deploy
└── referencias/
    ├── links-uteis.md                    # Links e recursos
    ├── glossario.md                      # Termos
    └── changelog.md                      # Histórico
```

---

## 🎯 Por Onde Começar?

### Se você é novo no projeto:
1. [Setup Inicial](./setup/setup-inicial.md)
2. [Estrutura do Projeto](./guias/estrutura-projeto.md)
3. [Padrões Utilizados](./guias/padroes-utilizados.md)

### Se está tendo um erro:
1. Procure no índice de **Troubleshooting** acima
2. Não encontrou? Abra uma issue no GitHub

### Se quer desenvolver uma nova feature:
1. [Estrutura do Projeto](./guias/estrutura-projeto.md)
2. [Como Adicionar Novas Features](./guias/nova-feature.md)
3. [Testes Unitários](./guias/testes-unitarios.md)

---

## 💡 Quick Links

| Problema | Solução |
|----------|---------|
| Cannot resolve symbol 'junit' | [Clique aqui](./troubleshooting/junit-error-fix.md) |
| Maven download dependencies | [Clique aqui](./troubleshooting/maven-issues.md) |
| Configurar MySQL | [Clique aqui](./troubleshooting/database-setup.md) |
| Deploy no Tomcat | [Clique aqui](./guias/deploy-local.md) |
| Escrever testes | [Clique aqui](./guias/testes-unitarios.md) |

---

## 📝 Contribuindo

Se encontrou um erro na documentação ou quer adicionar um novo guia:

1. Edite o arquivo correspondente
2. Atualize este README com um link se necessário
3. Faça um commit com mensagem descritiva

---

## ❓ Dúvidas?

- 📖 Consulte a documentação relacionada
- 🔍 Use Ctrl+F para buscar por palavras-chave
- 📧 Abra uma issue se tiver dúvidas

---

**Última atualização**: 20 de Março de 2026
**Versão do Projeto**: 1.0-SNAPSHOT

