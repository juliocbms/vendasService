# Microsserviço de Gestão de Vendas

Este microserviço é a interface de criação de pedidos e o **orquestrador da transação de venda**. Ele interage com o microserviço de `Estoque` para verificar a disponibilidade de produtos e com o microserviço de `Usuários` para autenticação, garantindo que a transação seja completa e correta.

### Tecnologias Utilizadas

-   **Linguagem:** Java 21
-   **Framework:** Spring Boot 3
-   **Segurança:** Spring Security e JWT (JSON Web Tokens)
-   **Persistência:** Spring Data JPA
-   **Banco de Dados:** PostgreSQL (via Docker)
-   **Mensageria:** RabbitMQ (para comunicação assíncrona)
-   **Ferramenta de Build:** Maven

### Funcionalidades (Endpoints)

| Método | Endpoint | Descrição | Restrição de Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/pedidos` | Cria um novo pedido de venda, validando o estoque e publicando um evento de notificação. | Autenticado |
| `GET` | `/pedidos` | Lista todos os pedidos no sistema. | Autenticado |
| `GET` | `/pedidos/meus-pedidos` | Retorna todos os pedidos associados ao usuário autenticado. | Autenticado |

### Detalhes da Implementação

#### Comunicação Mista: Síncrona e Assíncrona

Este microserviço utiliza uma abordagem de comunicação híbrida:
-   **Síncrona (via API Gateway):** Interage com o microserviço de `Usuários` para obter informações de autenticação, acessando-o através do API Gateway do Nginx.
-   **Assíncrona (com RabbitMQ):** Após a criação bem-sucedida de um pedido, ele atua como um **publisher de eventos**, enviando uma mensagem ao RabbitMQ para que o microserviço de `Estoque` possa consumir e processar a atualização da quantidade de produtos. Essa abordagem garante que a transação de venda seja rápida e resiliente, sem depender da disponibilidade imediata do serviço de estoque.

#### Persistência de Dados
O banco de dados, uma instância do **PostgreSQL**, é executado em um container **Docker**. Isso garante o **isolamento de dados**, com o serviço de Vendas sendo o único a ter acesso e controle sobre suas próprias tabelas.

### Como Rodar o Projeto

#### Requisitos do Sistema
-   Java 21
-   Maven 3
-   Docker e Docker Compose
-   RabbitMQ (CloudAMQP)

#### 1. Clonar o Repositório
```bash
git clone [https://github.com/juliocbms/vendasService.git](https://github.com/juliocbms/vendasService.git)
cd vendasService
