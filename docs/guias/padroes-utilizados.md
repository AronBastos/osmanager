# ✨ Padrões de Arquitetura Utilizados

Este documento descreve os padrões de design e arquitetura implementados no OSManager.

---

## 1️⃣ DAO Pattern (Data Access Object)

### O que é?
O padrão DAO encapsula a lógica de acesso ao banco de dados, isolando a camada de persistência.

### Implementação

**GenericDAO** - Classe base abstrata:
```java
public abstract class GenericDAO<T, ID> {
    
    @PersistenceContext
    protected EntityManager em;
    
    private final Class<T> entityClass;
    
    public void create(T entity) { }
    public T findById(ID id) { }
    public List<T> findAll() { }
    public T update(T entity) { }
    public void deleteById(ID id) { }
    public Long count() { }
}
```

**ClientDAO** - Implementação específica:
```java
public class ClientDAO extends GenericDAO<Client, Long> {
    public ClientDAO() {
        super(Client.class);
    }
    
    public Client findByCpf(String cpf) { }
    public List<Client> findByName(String name) { }
}
```

### Benefícios
✓ Reutilização de código (CRUD genérico)
✓ Fácil testar com mocks
✓ Trocar implementação do BD sem afetar negócio
✓ Queries centralizadas

---

## 2️⃣ Service Layer Pattern

### O que é?
A camada Service contém toda a lógica de negócio, separada da persistência e da UI.

### Implementação

```java
@Stateless
public class ClientService {
    
    @Inject
    private ClientDAO clientDAO;
    
    public Client createClient(Client client) throws Exception {
        // Validação
        if (client.getName() == null) {
            throw new IllegalArgumentException("Nome obrigatório");
        }
        
        // Verificar regra de negócio
        if (clientDAO.findByCpf(client.getCpf()) != null) {
            throw new IllegalArgumentException("CPF duplicado");
        }
        
        // Persistir
        clientDAO.create(client);
        return client;
    }
}
```

### Benefícios
✓ Lógica reutilizável (via REST e JSF)
✓ Fácil testar
✓ Transações centralizadas
✓ Validações consistentes

---

## 3️⃣ MVC Pattern (Model-View-Controller)

### O que é?
Separação entre dados (Model), apresentação (View) e lógica (Controller).

### Implementação em JSF

**Model** (Entity):
```java
@Entity
public class Client {
    @Id
    private Long id;
    private String name;
    private String email;
}
```

**View** (XHTML/Facelets):
```html
<h:form>
    <h:inputText value="#{clientController.client.name}"/>
    <h:commandButton value="Salvar" action="#{clientController.saveClient()}"/>
</h:form>
```

**Controller** (ManagedBean):
```java
@ManagedBean
@SessionScoped
public class ClientController {
    @Inject
    private ClientService clientService;
    
    private Client client;
    
    public void saveClient() {
        clientService.createClient(client);
        // Atualizar lista
    }
}
```

### Benefícios
✓ Separação de responsabilidades
✓ Fácil manter e testar
✓ Reutilizar lógica (REST + JSF)

---

## 4️⃣ Repository Pattern

### O que é?
Padrão que media entre camada de domínio e persistência, fornecendo interface de coleção.

### Nossa Implementação
Usamos o padrão Repository através dos DAOs:

```java
// DAO atua como repositório
public interface ClientRepository {
    void add(Client client);
    void remove(Client client);
    Client getById(Long id);
    List<Client> getAll();
}

// ClientDAO implementa
public class ClientDAO extends GenericDAO<Client, Long> 
    implements ClientRepository { }
```

---

## 5️⃣ Dependency Injection (DI)

### O que é?
Injetar dependências em vez de criá-las dentro da classe.

### Implementação

**Com CDI:**
```java
@Stateless
public class ClientService {
    @Inject
    private ClientDAO clientDAO;  // Injetado automaticamente
    
    public void saveClient(Client client) {
        clientDAO.create(client);
    }
}
```

**Em ManagedBean JSF:**
```java
@ManagedBean
@SessionScoped
public class ClientController {
    @Inject
    private ClientService clientService;  // Injetado
}
```

### Benefícios
✓ Fácil testar (injetar mocks)
✓ Baixo acoplamento
✓ Configuração centralizada

---

## 6️⃣ DTO Pattern (Data Transfer Object)

### O que é?
Objeto simples para transferir dados entre processos, sem lógica complexa.

### Implementação

```java
// DTO
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    // Getters/Setters
}

// REST Resource usando DTO
@Path("/clients")
public class ClientResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClientDTO> getAll() {
        List<Client> clients = clientService.getAllClients();
        return clients.stream()
            .map(c -> new ClientDTO(c.getId(), c.getName(), c.getEmail()))
            .collect(toList());
    }
}
```

### Benefícios
✓ Reduz tráfego de dados
✓ Esconde implementação interna
✓ Flexibilidade (enviar dados parciais)

---

## 7️⃣ Unit of Work Pattern

### O que é?
Mantém registro de todas as entidades modificadas e as salva em uma única transação.

### Implementação com Hibernate

```java
@Stateless
public class ServiceOrderService {
    
    @Inject
    private ServiceOrderDAO orderDAO;
    
    @Inject
    private ItemDAO itemDAO;
    
    @Transactional  // Transação única
    public void createOrderWithItems(ServiceOrder order, List<Item> items) {
        // Ambas operações na mesma transação
        orderDAO.create(order);
        for (Item item : items) {
            item.setOrder(order);
            itemDAO.create(item);
        }
        // Commit automático ao fim do método
    }
}
```

---

## 8️⃣ Strategy Pattern

### O que é?
Define uma família de algoritmos, encapsula cada um e os torna intercambiáveis.

### Exemplo de Uso

```java
// Estratégia de busca
public interface ClientSearchStrategy {
    List<Client> search(String criteria);
}

// Implementações
public class ClientSearchByName implements ClientSearchStrategy {
    public List<Client> search(String name) {
        return clientDAO.findByName(name);
    }
}

public class ClientSearchByCPF implements ClientSearchStrategy {
    public List<Client> search(String cpf) {
        Client client = clientDAO.findByCpf(cpf);
        return Arrays.asList(client);
    }
}

// Usar estratégia
public class ClientSearchService {
    public List<Client> search(String criteria, ClientSearchStrategy strategy) {
        return strategy.search(criteria);
    }
}
```

---

## 9️⃣ Template Method Pattern

### O que é?
Define estrutura de um algoritmo na classe base, deixando detalhes para subclasses.

### Implementação

```java
// Classe base
public abstract class GenericDAO<T, ID> {
    // Template method
    public final void save(T entity) {
        validate(entity);          // Pode ser sobrescrito
        prePersist(entity);        // Hook
        em.persist(entity);
        postPersist(entity);       // Hook
    }
    
    protected void validate(T entity) {
        // Implementação padrão
    }
    
    protected void prePersist(T entity) {
        // Vazio, subclasses podem sobrescrever
    }
    
    protected void postPersist(T entity) {
        // Vazio
    }
}

// Subclasse customizando
public class ClientDAO extends GenericDAO<Client, Long> {
    @Override
    protected void validate(Client client) {
        super.validate(client);
        if (client.getCpf() == null) {
            throw new IllegalArgumentException("CPF obrigatório");
        }
    }
}
```

---

## 🔟 Builder Pattern

### O que é?
Constrói objetos complexos passo a passo.

### Exemplo

```java
// Client com Builder
public class Client {
    private String name;
    private String email;
    private String phone;
    private String cpf;
    
    private Client(ClientBuilder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.cpf = builder.cpf;
    }
    
    public static class ClientBuilder {
        private String name;
        private String email;
        private String phone;
        private String cpf;
        
        public ClientBuilder withName(String name) {
            this.name = name;
            return this;
        }
        
        public ClientBuilder withEmail(String email) {
            this.email = email;
            return this;
        }
        
        public Client build() {
            return new Client(this);
        }
    }
}

// Usar
Client client = new Client.ClientBuilder()
    .withName("João")
    .withEmail("joao@email.com")
    .withPhone("(11) 9876-5432")
    .build();
```

---

## 📊 Resumo de Padrões Utilizados

| Padrão | Uso | Classe |
|--------|-----|--------|
| DAO | Persistência | `GenericDAO`, `ClientDAO` |
| Service Layer | Lógica de negócio | `ClientService` |
| MVC | UI | `ClientController`, `.xhtml` |
| Repository | Coleção de dados | `ClientDAO` |
| DI | Injeção | `@Inject` |
| DTO | Transferência | `ClientDTO` |
| Unit of Work | Transações | `@Transactional` |
| Strategy | Algoritmos | Busca variável |
| Template Method | Base comum | `GenericDAO` |
| Builder | Construção | `ClientBuilder` |

---

## 🎯 Quando Usar Cada Padrão

```
Persistência         → DAO Pattern
Lógica Complexa      → Service Layer
Interface Web        → MVC Pattern
Testes               → Dependency Injection
APIs REST            → DTO Pattern
Transações Múltiplas → Unit of Work
Múltiplas Estratégias → Strategy Pattern
Algoritmo Base       → Template Method
Construção Complexa  → Builder Pattern
```

---

**Próximo:** [Boas Práticas](./boas-praticas.md)

