# Sistema de Agendamento para Barbearia

Sistema de agendamento desenvolvido com **Java 21 e Spring Boot**, utilizando uma arquitetura baseada em microsserviços.

O projeto foi desenvolvido com o objetivo de aplicar conceitos de **arquitetura de microsserviços, autenticação e autorização, comunicação síncrona e assíncrona, persistência relacional e NoSQL, testes de integração e containerização**.

## Arquitetura

A aplicação é dividida nos seguintes serviços:

```text
                         ┌──────────────────┐
                         │   Auth Service   │
                         │ Spring Security  │
                         │ OAuth2 + JWT     │
                         │      MySQL       │
                         └────────┬─────────┘
                                  │
                                  │ JWT
                                  ▼
┌──────────────────┐      ┌──────────────────┐
│  Procedimentos   │◄─────│   Agendamento    │
│     Service      │      │     Service      │
│                  │      │                  │
│ MySQL            │      │ MongoDB          │
│ JPA / Hibernate  │      │ WebClient        │
│ MapStruct        │      │                  │
└──────────────────┘      └────────┬─────────┘
                                   │
                                   │ Evento
                                   ▼
                            ┌──────────────┐
                            │   RabbitMQ   │
                            └──────┬───────┘
                                   │
                                   ▼
                         ┌──────────────────┐
                         │   Notification   │
                         │     Service      │
                         │                  │
                         │ JavaMailSender   │
                         └──────────────────┘
```

## Serviços

### Auth Service

Responsável pela autenticação e autorização dos usuários.

Principais recursos:

* Spring Security
* OAuth2 Resource Server
* JWT
* Autorização baseada em roles
* BCrypt para armazenamento seguro de senhas
* Flyway para controle das migrações do banco
* MySQL

### Procedimentos Service

Responsável pelo gerenciamento dos procedimentos oferecidos pela barbearia.

Principais recursos:

* CRUD de procedimentos
* Gerenciamento de categorias
* Spring Data JPA
* Hibernate
* MySQL
* MapStruct para conversão entre entidades e DTOs

### Agendamento Service

Responsável pelo gerenciamento dos agendamentos.

Principais recursos:

* Criação, alteração e cancelamento de agendamentos
* Validação de conflitos de horário
* Regras de negócio relacionadas aos agendamentos
* Persistência com MongoDB
* Comunicação com o Procedimentos Service através do WebClient

### Notification Service

Responsável pelo envio de notificações por e-mail.

O serviço recebe eventos de forma assíncrona através do RabbitMQ e realiza o envio das notificações utilizando JavaMailSender.

Fluxo:

```text
Agendamento Service
        │
        │ publica evento
        ▼
     RabbitMQ
        │
        │ consome evento
        ▼
Notification Service
        │
        ▼
   JavaMailSender
        │
        ▼
      E-mail
```

## Tecnologias

| Tecnologia      | Utilização                    |
| --------------- | ----------------------------- |
| Java 21         | Linguagem principal           |
| Spring Boot     | Desenvolvimento dos serviços  |
| Spring Security | Segurança                     |
| OAuth2          | Autenticação e autorização    |
| JWT             | Tokens de acesso              |
| MySQL           | Persistência relacional       |
| MongoDB         | Persistência dos agendamentos |
| Spring Data JPA | Acesso ao MySQL               |
| Hibernate       | ORM                           |
| Flyway          | Migrações do banco            |
| MapStruct       | Mapeamento DTO/Entity         |
| WebClient       | Comunicação entre serviços    |
| RabbitMQ        | Mensageria assíncrona         |
| JavaMailSender  | Envio de e-mails              |
| Testcontainers  | Testes de integração          |
| Docker          | Containerização               |
| Docker Compose  | Orquestração do ambiente      |

## Comunicação entre os serviços

O projeto utiliza dois tipos principais de comunicação.

### Comunicação síncrona

O **Agendamento Service** utiliza WebClient para consultar o **Procedimentos Service**.

```text
Agendamento Service
        │
        │ HTTP / WebClient
        ▼
Procedimentos Service
```

Essa comunicação permite validar e obter informações do procedimento durante o processo de criação do agendamento.

### Comunicação assíncrona

Após a criação de um agendamento, o **Agendamento Service** publica um evento no RabbitMQ.

```text
Agendamento Service
        │
        ▼
     RabbitMQ
        │
        ▼
Notification Service
```

Dessa forma, o envio do e-mail não fica diretamente acoplado ao fluxo principal de criação do agendamento.

## Segurança

A autenticação e autorização são implementadas utilizando **Spring Security e OAuth2 Resource Server**.

Os serviços protegidos validam os tokens JWT antes de permitir o acesso aos endpoints.

O acesso aos recursos também é controlado através de **roles**, permitindo separar as permissões de diferentes tipos de usuários.

## Testes

O projeto utiliza diferentes níveis de testes, incluindo testes de integração.

Para os testes que dependem de infraestrutura externa, foi utilizado **Testcontainers**, permitindo executar dependências como bancos de dados em containers durante os testes.

Exemplo:

```java
@Testcontainers
@SpringBootTest
class AgendamentoServiceIntegrationTest {
    
    @Container
    static MongoDBContainer mongo =
        new MongoDBContainer("mongo:8");
}
```

## Execução do projeto

### Pré-requisitos

* Java 21
* Docker
* Docker Compose
* Maven

### Executando com Docker Compose

Clone o repositório:

```bash
git clone https://github.com/jota-aga/agendamentos-jh
```

Entre na pasta do projeto:

```bash
cd agendamentos-jh
```

Suba os serviços:

```bash
docker compose up --build
```

Para executar em segundo plano:

```bash
docker compose up --build -d
```

Para parar os containers:

```bash
docker compose down
```

## Variáveis de ambiente

As informações sensíveis, como credenciais de banco de dados, autenticação e configuração de e-mail, não são armazenadas diretamente no código.

Configure as variáveis necessárias no arquivo `.env` antes de iniciar os serviços.

Exemplo:

```env
SECRET_API_NOTIFICACAO_SERVICE=
SPRING_MAIL_HOST=
SPRING_MAIL_PORT=
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=
NOTIFICATION_EMAIL_FROM=
```

> Os valores utilizados no ambiente real não devem ser versionados no repositório.

## Principais conceitos aplicados

Durante o desenvolvimento, foram colocados em prática conceitos como:

* Arquitetura de microsserviços
* Separação de responsabilidades
* Autenticação e autorização com OAuth2
* JWT
* Comunicação síncrona entre serviços
* Comunicação assíncrona com RabbitMQ
* Bancos de dados relacionais e NoSQL
* Event-driven communication
* Testes de integração
* Testcontainers
* Docker e Docker Compose
* DTOs e mapeamento com MapStruct
* Migrações de banco de dados com Flyway
* Aplicação de Conceitos SOLID, Design Pattern e Clean Code

## Próximos passos

Algumas melhorias que podem ser implementadas futuramente:

* Implementação de API Gateway
* Observabilidade e centralização de logs
* Resilience4j para circuit breaker, retry e timeout
* Monitoramento dos serviços
* CI/CD
* Melhorias na estratégia de tratamento de falhas de mensagens

## Autor

**João Henrique**

Desenvolvedor Backend Java

* LinkedIn: [LinkedIn](https://www.linkedin.com/in/joao-henrique-araujo-de-souza-/)
