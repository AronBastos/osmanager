# 🏗️ Estrutura do Projeto OSManager

## Visão Geral

O projeto OSManager segue uma arquitetura em **camadas** bem definida, facilitando manutenção, testes e escalabilidade.

```
src/main/java/aron/bastos/osmanager/
├── model/              # Entidades JPA (Banco de Dados)
├── dao/                # Data Access Objects (Persistência)
├── service/            # Lógica de Negócio
├── controller/         # ManagedBeans JSF (Interface)
├── resource/           # REST Endpoints (API)
├── dto/                # Data Transfer Objects
└── util/               # Utilitários e Helpers
```

---

## 📁 Estrutura Detalhada

### **1. model/** - Entidades JPA
Contém as classes anotadas com `@Entity` que mapeiam as tabelas do banco.

**Exemplo:**
```
model/
├── Client.java              # Cliente da oficina
├── Vehicle.java             # Veículo do cliente
├── ServiceOrder.java        # Ordem de serviço
└── Service.java             # Tipo de serviço
```

**Características:**
- Anotações JPA (`@Entity`, `@Table`, `@Column`)
- Getters/Setters
- `@PrePersist` para dados padrão
- Validações com `@NotNull`, `@Email`, etc

---

### **2. dao/** - Data Access Objects
Implementam o padrão Repository para operações no banco de dados.

**Exemplo:**
```
dao/
├── GenericDAO.java          # Classe base com CRUD genérico
├── ClientDAO.java           # DAO específico para Client
├── VehicleDAO.java          # DAO para Vehicle
└── ServiceOrderDAO.java     # DAO para ServiceOrder
```

**GenericDAO:**
- Método `create(T entity)` - Inserir
- Método `findById(ID id)` - Buscar por ID
- Método `findAll()` - Listar todos
- Método `update(T entity)` - Atualizar
- Método `deleteById(ID id)` - Deletar
- Método `count()` - Contar registros

**DAOs Específicos:**
- Herdam de `GenericDAO`
- Implementam queries customizadas
- Exemplo: `findByCpf()`, `findByEmail()`

---

### **3. service/** - Lógica de Negócio
Orquestração e regras de negócio. Camada entre Controllers e DAOs.

**Exemplo:**
```
service/
├── ClientService.java       # Serviço de Cliente
├── VehicleService.java      # Serviço de Veículo
└── ServiceOrderService.java # Serviço de Ordem
```

**Responsabilidades:**
- ✓ Validações de negócio
- ✓ Cálculos e processamentos
- ✓ Orquestração entre DAOs
- ✓ Tratamento de exceções
- ✓ Transações

**Exemplo de método:**
```java
public Client createClient(Client client) throws Exception {
    // Validação
    if (client.getName() == null) {
        throw new IllegalArgumentException("Nome é obrigatório");
    }
    
    // Verificar duplicata
    if (clientDAO.findByCpf(client.getCpf()) != null) {
        throw new IllegalArgumentException("CPF já existe");
    }
    
    // Persistir
    clientDAO.create(client);
    return client;
}
```

---

### **4. controller/** - ManagedBeans JSF
Controladores que gerenciam a interação com a UI (JSF/PrimeFaces).

**Exemplo:**
```
controller/
├── IndexController.java     # Controla página inicial
├── ClientController.java    # Gerencia clientes
├── VehicleController.java   # Gerencia veículos
└── ServiceOrderController.java
```

**Anotações:**
```java
@ManagedBean(name = "clientController")
@SessionScoped
public class ClientController implements Serializable {
    
    @Inject
    private ClientService clientService;
    
    private List<Client> clients;
    
    // Métodos para CRUD
    public void loadClients() { }
    public void showCreateForm() { }
    public void saveClient() { }
    public void deleteClient(Long id) { }
}
```

---

### **5. resource/** - REST Endpoints
Expõe a API REST para consumo por aplicações externas.

**Exemplo:**
```
resource/
├── ClientResource.java      # API REST para clientes
└── ServiceOrderResource.java
```

**Métodos HTTP:**
```java
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {
    
    @GET
    public List<Client> getAll() { }
    
    @GET @Path("/{id}")
    public Client getById(@PathParam("id") Long id) { }
    
    @POST
    public Response create(Client client) { }
    
    @PUT @Path("/{id}")
    public Response update(@PathParam("id") Long id, Client client) { }
    
    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") Long id) { }
}
```

---

### **6. dto/** - Data Transfer Objects
Objetos para transferência de dados entre camadas (sem entidades completas).

**Exemplo:**
```
dto/
├── ClientDTO.java           # DTO de Cliente
└── ServiceOrderDTO.java     # DTO de Ordem
```

**Uso:**
- API REST retorna DTOs em vez de entidades
- Reduz tráfego de dados
- Esconde implementação interna

```java
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    // Getters/Setters
}
```

---

### **7. util/** - Utilitários
Funções auxiliares reutilizáveis.

**Exemplo:**
```
util/
├── DateUtils.java          # Funções de data
├── StringUtils.java        # Funções de string
├── ValidationUtils.java    # Validações
└── CurrencyUtils.java      # Conversões monetárias
```

---

## 🔄 Fluxo de Dados

```
[JSF View] 
    ↓
[Controller (ManagedBean)]
    ↓
[Service (Lógica)]
    ↓
[DAO (Persistência)]
    ↓
[Entity (Modelo)]
    ↓
[Database (MySQL)]
```

**Exemplo Prático:**

1. Usuário preenche formulário em `clients.xhtml`
2. Clica em "Salvar"
3. `ClientController.saveClient()` é chamado
4. Controller valida dados e chama `ClientService.createClient()`
5. Service faz validações de negócio
6. Service chama `ClientDAO.create(client)`
7. DAO persiste no banco via Hibernate
8. Retorno passa pelas camadas até voltar à view

---

## 📚 Estrutura de Testes

Os testes espelham a estrutura de `main`:

```
src/test/java/aron/bastos/osmanager/
├── dao/
│   ├── ClientDAOTest.java
│   └── GenericDAOTest.java
├── service/
│   ├── ClientServiceTest.java
│   └── ServiceOrderServiceTest.java
├── controller/
│   └── ClientControllerTest.java
├── resource/
│   └── ClientResourceTest.java
└── util/
    └── ValidationUtilsTest.java
```

---

## 📁 Recursos

```
src/main/resources/
├── META-INF/
│   └── persistence.xml      # Configuração JPA
├── application.properties   # Propriedades da app
└── log4j2.xml              # Configuração de logs

src/main/webapp/
├── WEB-INF/
│   ├── web.xml             # Descriptor da aplicação
│   └── faces-config.xml    # Configuração JSF
├── index.xhtml             # Página inicial
├── clients/
│   ├── list.xhtml          # Listar clientes
│   ├── create.xhtml        # Criar cliente
│   └── edit.xhtml          # Editar cliente
├── orders/                 # Ordens de serviço
├── vehicles/               # Veículos
├── services/               # Serviços
└── resources/
    ├── css/
    │   └── style.css
    ├── js/
    └── images/
```

---

## 🎯 Quando Usar Cada Camada

| Camada | Quando Usar | Exemplo |
|--------|-------------|---------|
| **Model** | Representar dados | Entidade `Client` |
| **DAO** | Operações no BD | `ClientDAO.findById(1)` |
| **Service** | Lógica de negócio | Validar CPF único |
| **Controller** | Controlar UI | Carregar lista de clientes |
| **Resource** | API REST | GET `/api/clients` |
| **DTO** | Transferir dados | Resposta da API |
| **Util** | Reutilizar código | Validar email |

---

## 📝 Boas Práticas

✓ **Uma responsabilidade por classe**
✓ **DAOs só fazem persistência**
✓ **Services contêm lógica**
✓ **Controllers gerenciam UI**
✓ **Testes espelham structure**
✓ **DTOs para APIs**
✓ **Injeção de dependência**

---

## 🚀 Adicionando Nova Feature

1. Criar `Entity` em `model/`
2. Criar `DAO` em `dao/`
3. Criar `Service` em `service/`
4. Criar `Controller` em `controller/`
5. Criar views em `webapp/`
6. Criar testes em `src/test/`

**Veja:** [Como Adicionar Novas Features](../guias/nova-feature.md)

---

**Próximo passo:** [Padrões Utilizados](./padroes-utilizados.md)

