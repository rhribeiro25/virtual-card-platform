# 🎫 Virtual Card Platform - Backend API

## 💼 Problem Overview

You are tasked with building the backend API for a **Virtual Card Platform**. Users should be able to:

- Create virtual cards
- Add funds (top-up)
- Spend funds from the cards

The system must guarantee **data consistency**, **prevent overspending**, and remain **robust under concurrent usage**.

---

## ✅ Core Requirements

### 📘 Entities

**Card**

- `id: UUID`
- `cardholderName: String`
- `balance: BigDecimal`
- `createdAt: Timestamp`

**Transaction**

- `id: UUID`
- `cardId: UUID` (foreign key)
- `type: ENUM { SPEND, TOPUP }`
- `amount: BigDecimal`
- `createdAt: Timestamp`

---

### 🔌 API Endpoints

#### `POST /cards`

Creates a new virtual card.

```json
{
  "cardholderName": "Alice",
  "initialBalance": 100.00
}
```

#### `POST /cards/{id}/spend`

Spends from a card.

- Returns `400 Bad Request` if balance is insufficient.
- Must prevent double-spending via race condition handling.

```json
{
  "amount": 30.00
}
```

#### `POST /cards/{id}/topup`

Adds funds to an existing card.

```json
{
  "amount": 50.00
}
```

#### `GET /cards/{id}`

Retrieves card details including current balance.

#### `GET /cards/{id}/transactions`

Returns the full transaction history for a card.

---

## 🛡 Business Rules

- A card's balance **can never go below zero**
- Transactions must ensure **atomicity and consistency** (e.g., no double spend)
- Spending from **non-existent or deleted cards** is forbidden
- Transactions are blocked if the card is `BLOCKED`
- Cards must exist; otherwise, return `404 Not Found`
- A card can have a **maximum of 5 SPEND transactions per minute**
- Duplicate transactions are avoided by checking amount and timestamp within a configurable time window

---

## 🛠 Technical Requirements

- **Java 17**
- **Maven**

---

## ⚙ Implementations

- In-memory **H2 database** with versioning via **Flyway**

- **Spring Data JPA**

- In-memory **cache** using `@Cacheable` and `@CacheEvict`

- 100% **test coverage** (unit and integration) with **JUnit + Mockito**

- **Jacoco** test coverage report published via GitHub Pages:

  👉 [Test Coverage Report](https://rhribeiro25.github.io/virtual-card-platform)

- **Swagger UI** available for REST API exploration:

  👉 [Swagger Interface (localhost)](http://localhost:8080/swagger-ui.html)

- **Postman Collection** for manual testing:

  👉 [Access the file](https://github.com/rhribeiro25/virtual-card-platform/blob/main/src/main/resources/static/docs/virtual-card-platform.postman_collection.json)

- H2 database accessible during execution:

  👉 [H2 Console](http://localhost:8080/h2-console)
> JDBC URL: `jdbc:h2:mem:virtual_card_platform`\
> User: `sa` | Password: `123456`

- Transaction safety using `@Transactional` and **optimistic locking** via `@Version`

- Proper layering: `Controller → Service (UseCase) → Repository`

- Use of **DTOs**, **MapStruct-like mappers**, and REST best practices (HTTP 200, 201, 400, 404, 409, 500)

- Design patterns:

  - **Template Method** for transaction execution
  - **Facade** via `CardUsecase` to encapsulate logic
  - **Builder** for creating immutable entities

---

## 🌟 Bonus Implementations

- Pagination support in transaction history
- Card status (`ACTIVE`, `BLOCKED`) with enforcement
- Version field (`@Version`) to enable optimistic concurrency
- Rate limiting: max 5 `SPEND` transactions/minute/card
- Swagger API documentation
- Caching to avoid repeated queries
- CI pipeline with **GitHub Actions** (build, test, Jacoco publish)
- **Flyway** DB versioning for environment consistency

---

🧠 Technical Design Decisions

### `Transaction` linked directly to `Card` entity:

Using a rich domain model with full `Card` object instead of just `cardId` enables:

- Referential integrity and cascaded validations
- Easy access to card status and metadata
- Easier extension for rules based on card state

> This design improves expressiveness and consistency without violating business constraints.

---

## ⚖ Trade-offs

- Security (e.g., JWT) not implemented to focus on core logic
- H2 in-memory DB used for speed and ease of local testing

---

## 🚀 Future Improvements

- JWT authentication via Spring Security
- Redis cache for horizontal scalability
- PostgreSQL + Docker Compose setup
- Kafka for event-driven architecture
- API Gateway and circuit breakers
- Cloud deployment with monitoring and alerting
- Observability with structured logs and tracing support (ELK, OpenTelemetry, Grafana-ready)

---

## 📙 Learning Strategy

- Practical development with hands-on debugging
- Official documentation as a primary reference
- Courses and online resources for frameworks and architecture

---

> Developed by Renan Henrique Ribeiro\
> [LinkedIn](https://www.linkedin.com/in/rhribeiro25)

