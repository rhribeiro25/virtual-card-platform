# 📝 Virtual Card Platform

## 🛡 Regras de Negócio

- O saldo do cartão nunca pode ser inferior a zero. Validações aplicadas no controller evitam valores negativos ou zero.
- Impedida qualquer transação de gasto com saldo insuficiente.
- Cada transação é validada quanto à duplicidade: por padrão, transações de mesmo valor em um período de 10 minutos para o mesmo cartão são bloqueadas. O tempo é configurável via `application.properties`.
- Transações são bloqueadas se o cartão estiver `BLOCKED`.
- Antes de qualquer operação, é verificado se o cartão existe, retornando 404 se não existir.
- Implementada regra de **rate limit**: no máximo 5 gastos por minuto por cartão.

## ⚙️ Requisitos Técnicos

- **Java 17** com **Spring Boot**
- **Banco em memória H2** com versionamento usando **Flyway**
- **Spring Data JPA**
- **Cache** em memória com `@Cacheable` e `@CacheEvict`
- **Cobertura de testes** unitários e de integração 100% com **JUnit + Mockito**
- **Jacoco Report publicado automaticamente via GitHub Pages**:

  👉 [`Acessar Cobertura de Testes`](https://rhribeiro25.github.io/virtual-card-platform)

- **Swagger UI disponível para exploração da API REST**:

  👉 [`Acessar Swagger com a aplicação rodando`](http://localhost:8080/swagger-ui.html)

- **Collection Postman para testes manuais:**

  👉 [`Acessar o arquivo`](https://github.com/rhribeiro25/virtual-card-platform/raw/main/src/main/resources/static/docs/virtual-card-platform.postman_collection.json)

- Capacidade de acessar o banco de dados H2 em memória para visualização e testes:

  👉 [`Acessar o h2-console com a aplicação rodando`](http://localhost:8080/h2-console)  
> JDBC URL: `jdbc:h2:mem:virtual_card_platform`  
> Usuário: `sa` | Senha: `123456`

- Segurança transacional com `@Transactional` e **concorrência otimista** via campo `@Version`
- Camadas bem definidas: `Controller → Service (UseCase) → Repository`
- Uso de **DTOs**, **MapStruct-like mappers**, e boas práticas REST (HTTP 200, 201, 400, 404, 409, 500)
- Design Patterns aplicados:
  - **Template Method** (para transações): padroniza o fluxo de processamento (validação → atualização → persistência → auditoria), permitindo personalização por tipo de transação (gasto ou recarga).
  - **Facade**: `CardUsecase` atua como fachada simplificando o uso de regras complexas por trás de uma interface coesa, escondendo detalhes internos dos controllers.
  - **Builder**: aplicado em `Card` e `Transaction` para criar objetos complexos de forma imutável e legível.

## 🌟 Bônus Implementados

- Paginação no histórico de transações
- Status do cartão (`ACTIVE`, `BLOCKED`) e regras aplicadas
- Campo de versão (`@Version`) no modelo de cartão
- Limite de até 5 gastos por minuto por cartão
- Integração com **Swagger** para documentação interativa da API
- **Cache** para otimização de consultas (évito de queries repetidas)
- Integração com **CI via GitHub Actions**, incluindo build, testes e publicação automática de relatório Jacoco
- Controle de versão do banco de dados com **Flyway**, garantindo consistência entre ambientes
- Observabilidade com **logs estruturados e suporte a traçamento distribuído**, prontos para integração com ferramentas como ELK, OpenTelemetry, Grafana, entre outras

## 🔍 Modelagem e Decisão de Projeto

### Uso de objeto `Card` nas transações:

A modelagem segue o padrão de **fortalecer o modelo de domínio**, usando relação direta entre `Transaction` e `Card`, em vez de apenas um `cardId`. Isso permite:

- Garantir integridade referencial
- Facilitar o uso de validações em tempo de execução com o objeto completo
- Melhor extensibilidade futura, com acesso a propriedades do cartão (ex: status, nome, versão) diretamente
- Aplicar lógicas de negócio baseadas no estado do objeto sem necessidade de buscar dados adicionais

> Esta decisão foi tomada para atender a requisitos técnicos e fortalecer a integridade e expressividade do domínio, **sem ferir nenhuma regra de negócio** estabelecida no enunciado ou na modelagem esperada.

## ⚖️ Trade-offs

- Não foi implementado autenticação e autorização (Spring Security)
- Banco em memória H2 foi usado para facilitar testes e execução local

## 🚀 Melhorias Futuras

- Autenticação JWT com Spring Security
- Integração com Redis para cache distribuído
- Uso de PostgreSQL e Docker Compose para dev local
- Kafka para comunicação entre microsserviços
- Gateway e Circuit Breaker
- Observabilidade com logs estruturados e traçamento distribuído (OpenTelemetry, ELK, etc.)
- Publicação em ambiente cloud (CD)

## 📙 Estratégia de Aprendizado

- **Cursos em plataformas de ensino** para aprofundar o conhecimento em Spring, testes, arquitetura limpa e boas práticas
- **Documentação oficial** como principal referência para uso correto e atualizado das tecnologias
- **Desenvolvimento prático com troubleshooting** para consolidar o aprendizado por meio da resolução de problemas reais, criando projetos completos e debugando erros em execução

---

> Desenvolvido por Renan Henrique Ribeiro\
> [GitHub](https://github.com/rhribeiro25) · [LinkedIn](https://www.linkedin.com/in/rhribeiro25)

