# Fiber Splice Locator — BackEnd

API REST em Spring Boot para gestão de Caixas de Emenda Óptica (CEOs) e das
ordens de serviço de manutenção em campo.

## Sobre o projeto

Este aplicativo foi desenvolvido como trabalho da disciplina Programação para
Dispositivos Móveis, do curso de Análise e Desenvolvimento de Sistemas da
Universidade Unisinos.

O projeto atende a uma necessidade real da [POP-RS/RNP](https://pop-rs.rnp.br/),
que hoje controla suas Caixas de Emenda Óptica (CEOs) por planilhas de Excel e
fotos trocadas por WhatsApp. Este repositório é o BackEnd; o app mobile usado
pelos técnicos em campo está em
[fiber-splice-locator-front](https://github.com/felipeschwartz/fiber-splice-locator-front).

**Desenvolvedor principal:** [Felipe Schwartz](https://github.com/felipeschwartz)
**Colaboradores:** Eduardo Ribeiro Silveira, Vorni Valpir Fagundes da Cunha
Junior, Diego Ribeiro Torres, Lucas Candido Vargas

## Tecnologias utilizadas

- **Java 25** + **Spring Boot 4**
- **Spring Security** com autenticação **JWT** (`jjwt`)
- **Spring Data JPA** / **Hibernate** sobre **PostgreSQL**
- **Spring HATEOAS** (respostas com links de navegação)
- **MapStruct** (conversão entre entidades e DTOs) + **Lombok**
- **springdoc-openapi** (Swagger UI, gerado automaticamente a partir dos endpoints)
- **Docker** / **Docker Compose** para empacotar e rodar toda a stack (API + Postgres)
- Testes: **JUnit**, **Mockito**, **RestAssured**, **Testcontainers**

## Pré-requisitos

Você tem duas formas de rodar o projeto — escolha uma:

### Opção A — Docker (recomendado, menos instalação)

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows/Mac)
  ou Docker Engine + plugin Docker Compose (Linux)

Não precisa instalar Java, Maven nem PostgreSQL — tudo roda dentro dos containers.

### Opção B — Rodar localmente (sem Docker)

- **JDK 25** instalado ([Eclipse Temurin](https://adoptium.net/), por exemplo)
- **PostgreSQL** rodando localmente, com um banco de dados criado (o nome e as
  credenciais precisam bater com o que está em
  `src/main/resources/application.yml`, seção `spring.datasource`)
- Maven **não precisa ser instalado à parte** — o projeto já inclui o Maven
  Wrapper (`mvnw` / `mvnw.cmd`)

## Como rodar

### Opção A — Docker Compose

1. Na raiz do projeto, crie um arquivo `.env` (ele não vai para o Git) com:
   ```env
   POSTGRES_DB=fiber_splice_locator
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=escolha_uma_senha
   ```
2. Suba os containers:
   ```bash
   docker compose up --build
   ```
   *(em instalações mais antigas do Docker, o comando é `docker-compose up --build`, com hífen)*
3. A API fica disponível em `http://localhost:8080`.

Isso sobe três containers: a API, um PostgreSQL próprio (dados persistidos em
volume Docker, não é o mesmo banco de uma instalação local do Postgres) e o
Portainer (interface web opcional para gerenciar os containers, em
`http://localhost:9000`).

### Opção B — Local, via Maven Wrapper

Com o PostgreSQL local rodando e o banco criado:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

> **Atenção:** o projeto está configurado com `ddl-auto: create-drop` — a cada
> vez que a aplicação inicia, o banco é recriado do zero (e apagado ao
> desligar). É proposital para desenvolvimento/avaliação, mas significa que
> nenhum dado sobrevive a um restart.

## Dados de teste

Ao subir com o banco vazio, o `DevDatabaseSeeder` popula automaticamente
usuários, CEOs e ordens de serviço de exemplo. Contas para testar o login
(usadas também pelo app mobile):

| E-mail | Senha | Perfil |
|---|---|---|
| god@fiberlocator.com | god123 | GOD_ADMIN |
| admin@fiberlocator.com | admin123 | ADMIN |
| carlos.silva@fiberlocator.com | tech123 | FIELD_TECHNICIAN |
| mariana.souza@fiberlocator.com | tech123 | FIELD_TECHNICIAN |

## Documentação da API

Com a aplicação rodando, o Swagger UI fica disponível em `http://localhost:8080/`
(gerado automaticamente pelo springdoc a partir dos controllers).

## Estrutura do projeto

```
controller/   endpoints REST (um por domínio: auth, user, ceo, service order,
              fotos, descrições de status)
service/      regras de negócio e permissões (@PreAuthorize)
repository/   acesso a dados (Spring Data JPA)
mapper/       conversão entidade ↔ DTO (MapStruct)
model/        entidades JPA, DTOs e enums
config/       segurança (JWT, CORS, roles), seed de dados de dev
```

## Repositórios relacionados

- **Mobile:** [fiber-splice-locator-front](https://github.com/felipeschwartz/fiber-splice-locator-front)
