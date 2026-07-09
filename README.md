# Student Automation System

A Spring Boot application for automating student management systems.

## Project Structure

The project has the following directory structure:

```text
student-automation
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.studentautomation
│   │   │       │
│   │   │       ├── StudentAutomationApplication.java
│   │   │       │
│   │   │       ├── config/           # Configuration classes
│   │   │       ├── controller/       # REST API Controllers
│   │   │       ├── dto/              # Data Transfer Objects
│   │   │       │   ├── request/      # Incoming Request DTOs
│   │   │       │   └── response/     # Outgoing Response DTOs
│   │   │       │
│   │   │       ├── entity/           # JPA Entities
│   │   │       ├── enums/            # Enumerated types
│   │   │       ├── exception/        # Custom exceptions & global handlers
│   │   │       ├── repository/       # Data Access/Repositories
│   │   │       ├── service/          # Business logic services
│   │   │       │   └── impl/         # Service implementations
│   │   │       │
│   │   │       ├── security/         # Security configuration & filters
│   │   │       ├── mapper/           # MapStruct/DTO mappers
│   │   │       ├── util/             # Utility classes
│   │   │       └── validation/       # Custom validations
│   │   │
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db
│   │           └── migration/        # Database migration files (e.g. Flyway)
│   │
│   └── test
│       └── java
│           └── com.studentautomation
│               └── StudentAutomationApplicationTests.java
│
├── pom.xml
└── README.md
```

## Running the Application

To run the application, use:

```bash
mvnw spring-boot:run
```
