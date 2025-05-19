# Spring Event Service

## Descrição
O Spring Event Service é um microserviço para gerenciamento de eventos, organizadores e tipos de ingressos. Ele fornece uma API RESTful para criar, atualizar, listar e excluir eventos e organizadores, com suporte a autenticação e autorização via Keycloak.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Cloud 2024.0.0**
- **Spring Security com OAuth2**
- **Spring WebFlux** para programação reativa
- **Keycloak 25.0.3** para autenticação e autorização
- **OpenAPI/Swagger** para documentação da API
- **Docker & Kubernetes** para conteinerização e orquestração
- **OpenTelemetry** para rastreamento distribuído
- **Micrometer & Prometheus** para métricas
- **Loki** para logging centralizado
- **RabbitMQ** para barramento de mensagens
- **MapStruct** para mapeamento de objetos
- **Lombok** para redução de código boilerplate
- **JUnit 5 & WireMock** para testes
- **MapStruct** para mapeamento entre objetos

## Estrutura do Projeto

### Pacotes Principais
```
src/main/java/br/dev/leandro/spring/event/
├── config/           # Configurações do Spring
├── controller/       # Controladores REST
├── dto/              # Objetos de transferência de dados
├── entity/           # Entidades JPA
├── exception/        # Exceções personalizadas
├── filter/           # Filtros HTTP
├── mapper/           # Mapeadores (MapStruct)
├── repository/       # Repositórios JPA
├── security/         # Configurações de segurança
└── service/          # Serviços de negócio
```

### Modelo de Domínio
O serviço gerencia três entidades principais:

1. **Organizer (Organizador)**
   - Representa uma organização que pode criar e gerenciar eventos
   - Integrado com usuários do Keycloak

2. **Event (Evento)**
   - Representa um evento com detalhes como nome, descrição, local, datas
   - Pertence a um organizador

3. **TicketType (Tipo de Ingresso)**
   - Representa diferentes tipos de ingressos disponíveis para um evento
   - Cada tipo tem seu próprio preço e quantidade disponível

## Configuração e Execução

### Pré-requisitos
- Java 21
- Maven
- Docker e Docker Compose
- MySQL (ou use o contêiner Docker)
- Keycloak para autenticação

### Variáveis de Ambiente
```
# Configuração do Spring
SERVER_PORT=8092
SPRING_PROFILES_ACTIVE=local
SPRING_APP_NAME=spring-event-service
SPRING_CONFIG_SERVER=http://config-server:8888

# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/event_service
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# Keycloak
KEYCLOAK_URL=http://keycloak:8080
KEYCLOAK_AUTH_SERVER_URL=http://keycloak:8080/auth
KEYCLOAK_JWK_SET_URI=http://keycloak:8080/auth/realms/event-service/protocol/openid-connect/certs
KEYCLOAK_ISSUER_URI=http://keycloak:8080/auth/realms/event-service
KEYCLOAK_CLIENT_SECRET=your-client-secret

# Observabilidade
LOKI_URL=http://loki:3100
OTEL_SERVICE_NAME=spring-event-service
OTEL_TRACES_EXPORTER=otlp
OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
```

### Executando com Docker Compose
```bash
# Construir a imagem Docker
mvn clean package -Pdocker

# Iniciar os serviços
docker-compose up -d
```

### Executando Localmente
```bash
# Compilar o projeto
mvn clean package

# Executar a aplicação
java -jar target/spring-event-service.jar
```

## API Endpoints

### Organizadores
- `POST /organizers` - Criar um novo organizador
- `PUT /organizers/{id}` - Atualizar um organizador existente
- `GET /organizers/{id}` - Buscar um organizador por ID
- `GET /organizers` - Listar todos os organizadores (paginado)
- `DELETE /organizers/{id}` - Remover um organizador

### Eventos
- `POST /events` - Criar um novo evento
- `PUT /events/{id}` - Atualizar um evento existente
- `GET /events/{id}` - Buscar um evento por ID
- `GET /events` - Listar todos os eventos (paginado)
- `DELETE /events/{id}` - Remover um evento (soft delete)

## Documentação da API
A documentação completa da API está disponível através do Swagger UI:
```
http://localhost:8092/event/swagger-ui.html
```

## Segurança
O serviço utiliza OAuth2 com Keycloak para autenticação e autorização. Existem dois níveis de acesso:
- **ADMIN**: Acesso completo a todas as operações
- **ORGANIZER**: Pode gerenciar seus próprios eventos e informações de organizador

## Testes
O projeto inclui testes unitários e de integração:

```bash
# Executar testes unitários
mvn test

# Executar testes de integração
mvn verify
```

## Monitoramento e Observabilidade
- **Health Check**: `/actuator/health`
- **Métricas**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

## Contribuição
1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença
Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.

## Contato
- Email: lsilva.info@gmail.com
