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
[fiber-splice-locator-front-app](https://github.com/felipeschwartz/fiber-splice-locator-front-app),
e o painel web dos administradores em
[fiber-splice-locator-front-web](https://github.com/felipeschwartz/fiber-splice-locator-front-web).

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
   JWT_SECRET=cole_aqui_um_segredo_longo
   ```
   Para gerar o `JWT_SECRET`, use `openssl rand -base64 64`. Ele precisa ter pelo
   menos 32 caracteres (256 bits), senão o login falha.
2. Suba os containers:
   ```bash
   docker compose up --build
   ```
   *(em instalações mais antigas do Docker, o comando é `docker-compose up --build`, com hífen)*
3. A API fica disponível em `http://localhost:8080`.

Isso sobe quatro containers: a API, um PostgreSQL próprio (dados persistidos em
volume Docker, não é o mesmo banco de uma instalação local do Postgres), o
painel web (`http://localhost:8081`) e o Portainer (interface web opcional para
gerenciar os containers, em `https://localhost:9443`, acessível só a partir da
própria máquina).

### Opção B — Local, via Maven Wrapper

Com o PostgreSQL local rodando e o banco criado:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Os valores locais (banco em
`localhost:5432`, segredo JWT de desenvolvimento, log de segurança em DEBUG)
ficam no `application-dev.yml`, usado pelo perfil `dev`, que é o padrão.

> **Atenção:** a aplicação usa `ddl-auto: update`, então os dados persistem entre
> reinícios. Já os **testes** usam `create-drop` no mesmo banco local: rodar a
> suíte de testes apaga os dados de desenvolvimento, que são recriados pelo
> seeder na próxima subida.

## Rodando em um servidor (produção)

Em servidor, rode com o perfil `prod`. Nele não há valores padrão para banco
nem segredo: se faltar algum, a aplicação não sobe (em vez de subir com um
segredo conhecido). O seeder de dados de exemplo também não roda.

| Variável | Obrigatória | Para quê |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=prod` | Sim | Ativa o perfil de produção |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Sim | Conexão com o PostgreSQL |
| `JWT_SECRET` | Sim | Assinatura dos tokens (mínimo de 32 caracteres) |
| `CORS_ORIGINPATTERNS` | Se houver painel web em outra origem | Substitui a lista inteira de origens liberadas |
| `SERVER_FORWARD_HEADERS_STRATEGY=framework` | Atrás de proxy HTTPS (Caddy, nginx) | Faz as URLs geradas (ex.: fotos) saírem com `https` |
| `RESEND_API_KEY`, `RESEND_FROM` | Para enviar e-mails | Credenciais da Resend |
| `INITIAL_ADMIN_EMAIL`, `INITIAL_ADMIN_PASSWORD`, `INITIAL_ADMIN_NAME` | Na primeira subida | Criam o primeiro SUPER_ADMIN (só se o banco não tiver nenhum usuário) |

Depois que o primeiro administrador for criado, remova o
`INITIAL_ADMIN_PASSWORD` do ambiente e troque a senha pelo sistema.

## Dados de teste

No perfil `dev`, ao subir com o banco vazio, o `DevDatabaseSeeder` popula
automaticamente usuários, CEOs e ordens de serviço de exemplo. Contas para
testar o login (usadas também pelo app mobile):

| E-mail | Senha | Perfil |
|---|---|---|
| superadmin@fiberlocator.com | superadmin123 | SUPER_ADMIN |
| admin@fiberlocator.com | admin123 | ADMIN |
| carlos.silva@fiberlocator.com | tech123 | FIELD_TECHNICIAN |
| mariana.souza@fiberlocator.com | tech123 | FIELD_TECHNICIAN |

Senhas novas (criação, troca e redefinição) precisam ter pelo menos 8
caracteres. As contas acima são gravadas direto pelo seeder e funcionam mesmo
com senhas mais curtas.

## Documentação da API

Com a aplicação rodando, o Swagger UI fica disponível em `http://localhost:8080/`
(gerado automaticamente pelo springdoc a partir dos controllers).

Para testar rotas protegidas pelo Swagger, faça login em
`POST /api/auth/v1/login`, copie o `token` da resposta e cole no botão
**Authorize**, no topo da página.

## Estrutura do projeto

```
controller/   endpoints REST (um por domínio: auth, user, ceo, service order,
              fotos, descrições de status)
service/      regras de negócio e permissões (@PreAuthorize, técnico só
              altera a própria OS), limite de tentativas de login
repository/   acesso a dados (Spring Data JPA)
mapper/       conversão entidade ↔ DTO (MapStruct)
model/        entidades JPA, DTOs e enums
config/       segurança (JWT, CORS, roles), seed de dados de dev e criação
              do primeiro administrador em produção
```

## Repositórios relacionados

- **App mobile:** [fiber-splice-locator-front-app](https://github.com/felipeschwartz/fiber-splice-locator-front-app)
- **Painel web:** [fiber-splice-locator-front-web](https://github.com/felipeschwartz/fiber-splice-locator-front-web)
