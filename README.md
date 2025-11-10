# VOP Framework

> **VOP Framework** is a modular Java framework designed to eliminate boilerplate across API microservices.  
> It standardizes authentication, logging, data access, and platform behaviors — allowing applications to focus purely on business logic.

---

## 🌱 Overview

The VOP framework provides a set of Spring Boot–compatible starters and core libraries that can be combined to quickly build secure, observable, and production-grade APIs.

| Layer | Module | Purpose |
|-------|---------|----------|
| **Base** | `vop-core` | Common types (API envelopes, errors, paging, masking, correlation IDs) |
| **Autoconfig** | `vop-autoconfigure` | Core auto-configuration and base properties |
| **Platform** | `vop-starter-platform` | Standardized filters, global exception handling, and JSON (Jackson) setup |
| **Security** | `vop-starter-security` | JWT (Okta) + API-key auth, unified principal, outbound token propagation |
| **Data** | `vop-starter-data` | JDBC + Liquibase + Redis with optional embedded Redis for local |
| **JPA** | `vop-data-jpa` | Optional JPA/Auditing/Specification helpers |
| **Logging** | `vop-logging-config` | Structured JSON logging + masking patterns |
| **Search** | `vop-search-core` | Vendor-neutral SPI for OpenSearch/Elasticsearch integrations |
| **Parent & BOM** | `vop-parent`, `vop-bom` | Manage dependency versions and plugin consistency |
| **Demo Service** | `vop-demo-service` *(coming next)* | Example Spring Boot API showing full framework integration |

---

## 🧩 Key Features

### ✅ Core & Platform
- Unified `ApiResponse<T>` structure for all APIs  
- `GlobalExceptionHandler` auto-wraps errors in a consistent JSON envelope  
- `CorrelationIdFilter` adds `X-Correlation-Id` to every request and MDC  
- Jackson autoconfig with ISO date/time and `FAIL_ON_UNKNOWN_PROPERTIES` disabled  

### 🔐 Security
- Inbound JWT (Okta) authentication  
- Optional API-key fallback via `X-API-Key` header  
- Outbound token propagation for `WebClient` and `RestTemplate`  
- Exposes unified `VopPrincipal` across controllers and services  

### 💾 Data & JPA
- Liquibase-based DB migrations  
- Redis integration (with optional embedded Redis for `local` profile)  
- JDBC helper auto-configured (`JdbcTemplate` bean)  
- Auditing base entity + `AuditorAware` with `networkId` propagation  
- Pageable mappers for Spring Data compatibility  

### 📜 Logging
- JSON log layout (Logback + Logstash encoder)  
- Automatic masking for SSNs, credit cards, tokens, etc.  
- Correlation ID and MDC propagation  

### 🔍 Search
- Vendor-agnostic SPI (`SearchClient`) for OpenSearch/Elasticsearch  
- Query DSL and bulk/index helpers  

---

## ⚙️ Project Structure

```
vop-api-framework/
├── vop-bom/                  # Dependency management
├── vop-parent/               # Common build/plugins configuration
├── vop-core/                 # Common API + utility classes
├── vop-autoconfigure/        # Base auto-configs
├── vop-starter-platform/     # Web platform defaults
├── vop-starter-security/     # JWT/API-key security
├── vop-starter-data/         # Liquibase + Redis (with embedded)
├── vop-data-jpa/             # JPA/Audit utilities
├── vop-logging-config/       # JSON logging formatters
├── vop-search-core/          # Search SPI
└── vop-demo-service/         # Example microservice (to be added)
```

---

## 🚀 Build & Run

### Build All Modules
```bash
mvn clean install -DskipTests
```

### Build a Specific Module
```bash
mvn clean package -pl vop-starter-platform -am -DskipTests
```

### Verify Auto-Configurations
After install, check your local Maven repository:
```
~/.m2/repository/com/tnl/vop/
```
You should see each module published with version `0.1.0-SNAPSHOT`.

---

## 🧪 Local Development

A **demo-service** module will show typical usage.  
Example local configuration (`application-local.yml`):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
    username: app
    password: app
  liquibase:
    change-log: classpath:/db/changelog/db.changelog-master.yaml

  data:
    redis:
      host: localhost
      port: 6379

vop:
  data:
    redis:
      embedded:
        enabled: true
        port: 6379
        only-when-local-profile: true

logging:
  level:
    root: INFO
```

---

## 🧠 Design Principles

- **Convention over configuration:** opinionated defaults, override-friendly.  
- **Framework-agnostic layers:** no module depends on Web or Security unless it must.  
- **Composition over inheritance:** use small auto-configs instead of deep hierarchies.  
- **Provider-neutral:** Hibernate, OpenSearch, etc. are optional adapters.  
- **Profile-aware:** local vs. prod behavior handled via configuration, not code changes.  

---

## 🛡️ Security Model

| Auth Type | Source | Purpose |
|------------|---------|----------|
| JWT | Okta / OAuth2 Resource Server | Standard bearer token validation |
| API Key | Config/env header | For service-to-service calls or testing |
| Token Propagation | WebClient / RestTemplate | Forward inbound token to downstream APIs |

`VopPrincipal` captures:
```java
applicationName, appToken, firstName, lastName, networkId, roles, scopes
```

---

## 🧰 Technology Stack

- **Java:** 21  
- **Spring Boot:** 3.2.x  
- **Build:** Maven multi-module  
- **DB Migration:** Liquibase  
- **Caching:** Redis  
- **Auth:** Okta JWT / API Key  
- **Metrics:** Micrometer + Actuator  
- **Logging:** Logback (JSON encoder)  
- **Search (optional):** OpenSearch  

---

## 📦 Creating a New Application

To create a new microservice using VOP Framework:

1. Add dependency in your app’s `pom.xml`:
   ```xml
   <parent>
       <groupId>com.tnl.vop</groupId>
       <artifactId>vop-parent</artifactId>
       <version>0.1.0-SNAPSHOT</version>
   </parent>

   <dependencies>
       <dependency>
           <groupId>com.tnl.vop</groupId>
           <artifactId>vop-starter-platform</artifactId>
       </dependency>
       <dependency>
           <groupId>com.tnl.vop</groupId>
           <artifactId>vop-starter-security</artifactId>
       </dependency>
       <dependency>
           <groupId>com.tnl.vop</groupId>
           <artifactId>vop-starter-data</artifactId>
       </dependency>
   </dependencies>
   ```

2. Add your REST controllers and Liquibase changelogs.
3. Configure your DB, Redis, and Okta endpoints.
4. Run with:
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=local
   ```

---

## 🧭 Next Steps

- ✅ Phase 1 – Core, Platform, Security, Data, JPA, Logging  
- 🚧 Phase 2 – Testing framework (`vop-test`)  
- 🗓️ Phase 3 – Integrations (OpenSearch adapter, Kafka, email/SMS support)  
- 🧩 Phase 4 – Demo Service showcasing end-to-end usage  

---

## 👥 Contributors

- **Lead Developer:** Mohan Kandar  
- **Framework Vision:** Simplify and unify enterprise API development across projects.  
