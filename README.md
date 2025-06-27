# 🎫 Virtual Card Platform - Backend API

<details>
  <summary><strong><span style="font-size: 1.1em;">
    💼 Problem Overview
  </span></strong></summary>

  <br>

  You are tasked with building the backend API for a **Virtual Card Platform**. Users should be able to:
  
  - Create virtual cards
  - Add funds (top-up)
  - Spend funds from the cards
  
  The system must guarantee **data consistency**, **prevent overspending**, and remain **robust under concurrent usage**.
  
  ---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    🧱 Core Requirements
  </span></strong></summary>

  ##### 🏛️ Entity

  - 💳 Card 

    The Card entity represents a virtual card created by a user within the platform. It holds key information required for performing financial transactions, such as the available balance and operational status.
    
    - `id: UUID`
    - `cardholderName: String`
    - `balance: BigDecimal`
    - `createdAt: Timestamp`
  
  - 💸 Transaction

    The Transaction entity represents a financial operation executed on a virtual card. It stores information about the transaction type, amount, and the moment it occurred. Every transaction is linked to a specific card.
    
    - `id: UUID`
    - `cardId: UUID` (foreign key)
    - `type: ENUM { SPEND, TOPUP }`
    - `amount: BigDecimal`
    - `createdAt: Timestamp`

  ##### 🔌 API Endpoints
  
  - `POST /cards`
  
    - Creates a new virtual card.
    
    ```json
    {
      "cardholderName": "Alice",
      "initialBalance": 100.00
    }
    ```
  
  - `POST /cards/{id}/spend`
   
    - Returns `400 Bad Request` if balance is insufficient.
    - Must prevent double-spending via race condition handling.
    
    ```json
    {
      "amount": 30.00,
      "requestId": "UUID"
    }
    ```
  
  - `POST /cards/{id}/topup`
  
    - Adds funds to an existing card.
    
    ```json
    {
      "amount": 50.00,
      "requestId": "UUID"
    }
    ```
  
  - `GET /cards/{id}`
  
    - Retrieves card details including current balance.
    
  - `GET /cards/{id}/transactions`
    
    - Returns the full transaction history for a card.
    
  ---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    📝 Business Rules
  </span></strong></summary>

  <br>

  - A card's balance **can never go below zero**
  - Transactions must ensure **atomicity and consistency** (e.g., no double spend)
  - Spending from **non-existent or deleted cards** is forbidden
  - Transactions are blocked if the card is `BLOCKED`
  - Cards must exist; otherwise, return `404 Not Found`
  - A card can have a **maximum of 5 SPEND transactions per minute**
  - Duplicate transactions are avoided by checking amount and timestamp within a configurable time window
  
  ---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    🛠️ Setup Essentials
  </span></strong></summary>

  <br>

  - **Java 17** – Required language version
  - **Maven 3.8** – Dependency management and build tool
  - **Default port: 8080**

---

</details>


<details>
  <summary><strong><span style="font-size: 1.1em;">
    ▶️ How to Run
  </span></strong></summary>

  <br>

  ```bash
    mvn spring-boot:run
  ```

  > 📌 That's it! No additional configuration is needed. All dependencies are resolved via Maven.
  > 
  > 🚀 The application runs with:
  > - In-memory H2 database initialized via Flyway
  > - In-memory cache for improved performance and reduced database load

---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    📬 API Usage via Postman
  </span></strong></summary>

  <br>

  This project includes a complete [Postman collection](https://github.com/rhribeiro25/virtual-card-platform/blob/main/src/main/resources/static/docs/virtual-card-platform.postman_collection.json) to help test and explore the API.
  
  1. Import the collection into Postman  
  2. Run the application using:
  3. Execute the requests in the following order:

  ###### 💳 `POST /cards` – Create a Virtual Card
  
  ![Create Card Screenshot](src/main/resources/static/docs/images/create-card.png)

  ###### 💸 `POST /cards/{id}/topup` – Add Funds to a Card
  
  ![Top-Up Screenshot](src/main/resources/static/docs/images/topup-card.png)

  ###### 💸  `POST /cards/{id}/spend` – Spend from the Card
  
  ![Spend Screenshot](src/main/resources/static/docs/images/spend-card.png)
  
  ###### 🔍 `GET /cards/{id}` – Retrieve Card Details
  
  ![Get Card Screenshot](src/main/resources/static/docs/images/get-card-details.png)
  
  ###### 📜 `GET /cards/{id}/transactions` – List Transactions
  
  ![Transaction History Screenshot](src/main/resources/static/docs/images/get-transactions-page.png)

---

</details> 

<details>
  <summary><strong><span style="font-size: 1.1em;">
    ⚙ Implementations
  </span></strong></summary>

  - In-memory **H2 database** with versioning via **Flyway**

  - **Spring Data JPA**

  - In-memory **cache** using `@Cacheable` and `@CacheEvict`

  - 100% **test coverage** (unit and integration) with **JUnit + Mockito**

  - Transaction safety using `@Transactional` and **optimistic locking** via `@Version`

  - Proper layering: `Controller → Service (UseCase) → Repository`

  - Use of **DTOs**, **MapStruct-like mappers**, and REST best practices (HTTP 200, 201, 400, 404, 409, 500)

  - Design patterns:
      - **Template Method** for transaction execution
      - **Facade** via `CardUsecase` to encapsulate logic
      - **Builder** for creating immutable entities

  - **Jacoco** test coverage report published via GitHub Pages:

    👉 [Test Coverage Report](https://rhribeiro25.github.io/virtual-card-platform)

  - **Swagger UI** available for REST API exploration:

    👉 [Swagger Interface (localhost)](http://localhost:8080/swagger-ui.html)

  - **Postman Collection** for manual testing:

    👉 [Access the file](https://github.com/rhribeiro25/virtual-card-platform/blob/main/src/main/resources/static/docs/virtual-card-platform.postman_collection.json)

  - H2 database accessible during execution:

    👉 [H2 Console](http://localhost:8080/h2-console)
  
  <br>

  > JDBC URL: `jdbc:h2:mem:virtual_card_platform`\
  > User: `sa` | Password: `123456`

---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    🌟 Bonus Implementations
  </span></strong></summary>

  <br>

  - Pagination support in transaction history
  - Card status (`ACTIVE`, `BLOCKED`) with enforcement
  - Version field (`@Version`) to enable optimistic concurrency
  - Rate limiting: max 5 `SPEND` transactions/minute/card
  - Swagger API documentation
  - Caching to avoid repeated queries
  - CI pipeline with **GitHub Actions** (build, test, Jacoco publish)
  - **Flyway** DB versioning for environment consistency
  - Request ID Validation – validation using requestId in transactions to make sure the same transaction isn't processed more than once, even in case of network issues or retries.
  - Cache First Strategy – Now the system checks the cache first, and only goes to the database if the data isn’t there. That helps improve performance and reduce unnecessary DB hits.
  - Global Exception Handler – organizing everything through BusinessException to keep things clean and centralized.
  - Transactional Rollback – @Transactional(rollbackFor = BusinessException.class) to ensure that if anything goes wrong in a business rule, all operations inside the process are rolled back, even those inside a Template Method flow.
  - Custom Validation per Transaction Type – validations customizable using a supports() method, so each one is only applied to the right type of transaction. It makes the system more flexible and easier to maintain.

---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    🧠 Technical Design Decisions
  </span></strong></summary>

  Using a rich domain model with full `Card` object instead of just `cardId` enables:

  - Referential integrity and cascaded validations
  - Easy access to card status and metadata
  - Easier extension for rules based on card state
  
  - <br>

  > This design improves expressiveness and consistency without violating business constraints.

---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    ⚖ Trade-offs
  </span></strong></summary>

1. Simplified Domain Models (Card & Transaction only)

    **Trade-off:**  
    Limited modeling to only two main entities (`Card` and `Transaction`) to keep the codebase small and testable.
    
    **Impact:**  
    ✅ Keeps business logic focused and isolated  
    ❌ May require refactoring when introducing related domains (e.g., User, Limits, Notifications)

2. Synchronous REST-only Communication

    **Trade-off:**  
    Used only REST APIs for card operations.
    
    **Impact:**  
    ✅ Easy to implement and test  
    ❌ Not scalable for high-throughput or event-driven scenarios (e.g., Kafka-based processing)

3. Optimistic Locking Instead of Distributed Locking

    **Trade-off:**  
    Used `@Version` field for concurrency handling instead of distributed locks (e.g., Redis-based).
    
    **Impact:**  
    ✅ Simple and safe within a single DB instance  
    ❌ May not prevent race conditions in distributed, high-concurrency environments

4. In-memory Cache Instead of Redis

    **Trade-off:**  
    Used `@Cacheable` with in-memory cache to reduce DB hits.
    
    **Impact:**  
    ✅ Zero setup; improves performance locally  
    ❌ Not suitable for horizontal scaling or shared cache between instances

5. Flyway for Versioning, No Liquibase or Schema Generation

    **Trade-off:**  
    Chose Flyway for database migrations and disabled Spring’s auto DDL generation.
    
    **Impact:**  
    ✅ Full control over schema changes, predictable  
    ❌ Requires manual script writing; no visual diffing or rollback tools built-in

6. Rate Limiting by Business Rule, Not Infrastructure

    **Trade-off:**  
    Implemented rate limiting (5 SPEND/min) in business logic instead of using an API Gateway or filter-based limiter.
    
    **Impact:**  
    ✅ Business-specific control  
    ❌ No automatic protection against DoS or broader abuse patterns

7. No Integration with External Services

    **Trade-off:**  
    The project is self-contained and doesn't simulate real external systems (e.g., card providers, fraud detection, etc.).
    
    **Impact:**  
    ✅ Simpler test scope  
    ❌ Less realistic for real-world systems with integration complexity

8. CI/CD with GitHub Actions but No Deployment Step

    **Trade-off:**  
    Configured automated tests and coverage reports, but deployment was not included.
    
    **Impact:**  
    ✅ Validates code quality early  
    ❌ Does not demonstrate production readiness (e.g., Docker, cloud deploy)

9. No Logging Framework Configured (e.g., SLF4J + Logback)

    **Trade-off:**  
    Relied on Spring Boot default logging without structuring log outputs.
    
    **Impact:**  
    ✅ Sufficient for local dev  
    ❌ Not prepared for observability or log analysis in production

---

</details>


<details>
  <summary><strong><span style="font-size: 1.1em;">
    🚀 Future Improvements
  </span></strong></summary>

1. <strong>Observability & Monitoring</strong>
    - Centralized logging with ELK stack (Elasticsearch, Logstash, Kibana)
    - Tracing with OpenTelemetry or Jaeger
    - Health checks via Spring Boot Actuator
    - Real-time metrics with Prometheus + Grafana
    - Alerting system (e.g., with Grafana Alerts or Google Cloud Monitoring)

2. <strong>Security</strong>
    - OAuth2 + JWT token support with Spring Security
    - JWE (JSON Web Encryption) for sensitive payload protection
    - Rate limiting via API Gateway or Bucket4j
    - CORS and CSRF protection setup
    - Secrets management with Vault or environment-level encryption

3. <strong>Persistence & Infrastructure</strong>
    - Replace H2 with PostgreSQL in Docker
    - Full Docker Compose environment (App + DB + Cache + Monitoring)
    - Migration scripts validation with Flyway dry-run
    - Testcontainers for integration tests with real databases

4. <strong>Testing & Quality</strong>
    - Integration tests using Testcontainers
    - Contract testing with Spring Cloud Contract
    - Load testing with k6 or Gatling
    - Mutation testing with Pitest
    - Code smells and static analysis with SonarQube

5. <strong>Performance & Scalability</strong>
    - Use Redis or Caffeine for distributed caching
    - Enable async processing with @Async or Spring Batch
    - Introduce rate limiting using external services (e.g., Kong, Envoy)
    - Adopt Circuit Breaker pattern (e.g., with Resilience4j)

6. <strong>Architecture & Organization</strong>
    - Modularization using Spring Modules / Multi-Module Maven
    - Use Clean Architecture + CQRS for command/query separation
    - Extract audit logic to a dedicated auditing module

7. <strong>Developer Experience</strong>
    - Swagger UI enhancements (tag groups, examples, auth header prefill)
    - Include Makefile or CLI script for common dev tasks
    - GitHub Actions with code coverage badge and changelog generator
    - Automatic changelog generation using Conventional Commits + Release Drafter

---

</details>

<details>
  <summary><strong><span style="font-size: 1.1em;">
    📙 Learning Strategy
  </span></strong></summary>

  <br>

  - Practical development with hands-on debugging
  - Official documentation as a primary reference
  - Courses and online resources for frameworks and architecture

---

</details>

#

> Developed by Renan Henrique Ribeiro\
> [LinkedIn](https://www.linkedin.com/in/rhribeiro25)

