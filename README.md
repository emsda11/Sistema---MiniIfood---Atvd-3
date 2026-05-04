# Atividade 03 - Mini iFood API

Projeto para a disciplina PPGTI - Desenvolvimento Web II.

## Requisitos atendidos

- API Spring Boot com Spring Data JPA.
- Domínio complexo inspirado no iFood.
- Spring Security com três roles:
  - ROLE_MASTER
  - ROLE_GERENTE
  - ROLE_AUDITOR
- Endpoint público: `GET /sistema/info`.
- Endpoints de listagem acessíveis às três roles.
- Endpoints de atualização acessíveis a ROLE_MASTER e ROLE_GERENTE.
- Endpoints de criação e exclusão acessíveis apenas a ROLE_MASTER.
- DTOs de entrada e saída.
- Validações nos DTOs.
- Status HTTP:
  - 201 Created em criações.
  - 204 No Content em exclusões.
  - 403 Forbidden para acesso sem permissão.
  - 404 Not Found para recurso inexistente.

## Usuários

| Usuário | Senha | Role |
|---|---|---|
| master | 123456 | ROLE_MASTER |
| gerente | 123456 | ROLE_GERENTE |
| auditor | 123456 | ROLE_AUDITOR |

## Matriz de permissões

| Endpoint | MASTER | GERENTE | AUDITOR | Público |
|---|---|---|---|---|
| GET /sistema/info | Sim | Sim | Sim | Sim |
| GET /clientes, /restaurantes, /produtos, /pedidos | Sim | Sim | Sim | Não |
| GET /{recurso}/{id} | Sim | Sim | Sim | Não |
| PUT /{recurso}/{id} | Sim | Sim | Não | Não |
| POST /{recurso} | Sim | Não | Não | Não |
| DELETE /{recurso}/{id} | Sim | Não | Não | Não |

## Como rodar

.\mvnw.cmd spring-boot:run
Acesse:
http://localhost:8080/index.html


ou abra o arquivo `index.html` diretamente.

## H2 Console

http://localhost:8080/h2-console

JDBC URL:

jdbc:h2:file:./data/ifood_atividade03_db
User: `sa`
Password: vazio.

## Para o Postman

Autenticação

Em TODAS as requisições:
Aba Authorization
Type: Basic Auth
Credenciais para teste:

master / 123456
gerente / 123456
auditor / 123456
Testes:
## 🔐 Perfis de Usuário (Roles)

A API implementa controle de acesso baseado em perfis, garantindo que cada tipo de usuário tenha permissões específicas.

### MASTER
Permissões:
- Criar
- Listar
- Atualizar
- Excluir

Exemplo de requisição (criação de cliente):

```json
{
  "nome": "João Lulu",
  "email": "joao@email.com"
}
```
### GERENTE

Permissões:
- Listar
- Atualizar

Exemplo de listagem:
```json
[
  {
    "id": 1,
    "nome": "João Lulu",
    "email": "joao@email.com",
    "quantidadePedidos": 2
  }
]

Tentativa de operação não permitida:

{
  "status": 403,
  "erro": "Forbidden",
  "mensagem": "Access Denied"
}
```
### AUDITOR

Permissões:
- Listar

Exemplo de listagem de pedidos:
```json

[
  {
    "id": 1,
    "status": "CRIADO",
    "valorTotal": 50.00,
    "clienteId": 1,
    "clienteNome": "João Lulu",
    "produtos": ["Pizza Calabresa"]
  }
]

Tentativa de exclusão:

{
  "status": 403,
  "erro": "Forbidden",
  "mensagem": "Access Denied"
}
```

### Entidade vs DTO
Entidade (exemplo Cliente)
```json
{
  "id": 1,
  "nome": "João Lulu",
  "email": "joao@email.com",
  "pedidos": [
    { "id": 1 },
    { "id": 2 }
  ]
}
DTO (resposta da API)
{
  "id": 1,
  "nome": "João Lulu",
  "email": "joao@email.com",
  "quantidadePedidos": 2
}
```
Justificativa

O uso de DTOs permite:

- Reduzir a quantidade de dados trafegados
- Evitar problemas de carregamento lazy (Hibernate)
- Melhorar a segurança da API
- Separar a camada de persistência da camada de apresentação

Ou seja:
A entidade representa o modelo completo do banco de dados, incluindo relacionamentos.
O DTO representa apenas os dados necessários para comunicação com a API.

## Endpoints da API

### Clientes

#### Criar Cliente
```http
POST http://localhost:8080/clientes
```
```json
{
  "nome": "João Lulu",
  "email": "joao@email.com"
}
```
### Listar clientes
```http
GET http://localhost:8080/clientes
```
### Buscar Cliente por ID
```http
GET http://localhost:8080/clientes/{id}
```
Exemplo:
GET http://localhost:8080/clientes/1

### Atualizar Cliente
PUT http://localhost:8080/clientes/{id}
```json
{
  "nome": "João Atualizado",
  "email": "novo@email.com"
}
```
### Excluir Cliente
DELETE http://localhost:8080/clientes/{id}

### Restaurantes

#### Criar Restaurante
POST http://localhost:8080/restaurantes
```json
{
  "nome": "Pizza Prime",
  "categoria": "Pizza"
}
```
#### Listar Restaurantes
GET http://localhost:8080/restaurantes
#### Buscar Restaurante por ID
GET http://localhost:8080/restaurantes/{id}
#### Atualizar Restaurante
PUT http://localhost:8080/restaurantes/{id}
```json
{
  "nome": "Pizza Atualizada",
  "categoria": "Massas"
}
```
 Excluir Restaurante
DELETE http://localhost:8080/restaurantes/{id}
### Produtos
#### Criar Produto
POST http://localhost:8080/produtos
```json
{
  "nome": "Pizza Calabresa",
  "preco": 50.00,
  "restauranteId": 1
}
```
#### Listar Produtos
GET http://localhost:8080/produtos
#### Buscar Produto por ID
GET http://localhost:8080/produtos/{id}
#### Atualizar Produto
PUT http://localhost:8080/produtos/{id}
```json
{
  "nome": "Pizza Grande",
  "preco": 60.00,
  "restauranteId": 1
}
```

### Excluir Produto
DELETE http://localhost:8080/produtos/{id}
### Pedidos
#### Criar Pedido
POST http://localhost:8080/pedidos
```json
{
  "clienteId": 1,
  "status": "CRIADO",
  "produtosIds": [1]
}
```
#### Listar Pedidos
GET http://localhost:8080/pedidos
#### Buscar Pedido por ID
GET http://localhost:8080/pedidos/{id}
#### Atualizar Pedido
PUT http://localhost:8080/pedidos/{id}
```json
{
  "clienteId": 1,
  "status": "ENTREGUE",
  "produtosIds": [1]
}
```
#### Excluir Pedido
DELETE http://localhost:8080/pedidos/{id}
### Público
#### Informações da API
GET http://localhost:8080/sistema/info
