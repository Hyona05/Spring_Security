# Spring Security REST API

A Spring Boot REST API application for managing Trainers, Trainees and Trainings with JWT-based authentication and authorization.

## Technologies

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate
* H2 Database
* Maven
* Lombok
* Jakarta Validation
* JWT (JSON Web Token)
* BCrypt Password Encoder
* OpenAPI / Swagger
* JUnit 5
* Mockito

---

## Security Features

### Authentication

* Username/Password authentication
* Spring Security integration
* BCrypt password hashing with salt
* JWT-based login

### Authorization

* Bearer Token authentication
* Protected endpoints require valid JWT token
* Registration endpoints are publicly accessible

### Brute Force Protection

* User is blocked after 3 failed login attempts
* Account remains locked for 5 minutes

### Logout

* JWT token invalidation using blacklist mechanism
* Secure logout endpoint

### CORS

* Configured through Spring Security

---

## API Features

### Authentication

* Login
* Logout
* Change Password

### Trainee Management

* Register Trainee
* Get Trainee Profile
* Update Trainee Profile
* Delete Trainee Profile
* Activate/Deactivate Trainee
* Get Trainee Trainings
* Update Trainee Trainer List
* Get Not Assigned Active Trainers

### Trainer Management

* Register Trainer
* Get Trainer Profile
* Update Trainer Profile
* Activate/Deactivate Trainer
* Get Trainer Trainings

### Training Management

* Add Training
* Get Training Types

---

## Security Rules

Public Endpoints:

* POST /api/trainees/register
* POST /api/trainers/register
* POST /api/auth/login

Protected Endpoints:

* All remaining endpoints require authentication using JWT Bearer Token.

---

## Database Schema

### Tables

* users
* trainers
* trainees
* trainings
* training_types
* trainee_trainer

### Relationships

| Entity                  | Relationship |
| ----------------------- | ------------ |
| User → Trainer          | One-to-One   |
| User → Trainee          | One-to-One   |
| Trainee ↔ Trainer       | Many-to-Many |
| Training → Trainee      | Many-to-One  |
| Training → Trainer      | Many-to-One  |
| Training → TrainingType | Many-to-One  |

---

## Business Rules

* Username and password are generated automatically during registration.
* Passwords are stored using BCrypt hashing.
* Username cannot be changed.
* User cannot be both Trainer and Trainee.
* Training Types are predefined and read-only.
* JWT token is required for protected resources.
* Brute force protection prevents repeated failed login attempts.

---

## Logging

Implemented according to task requirements:

### Transaction Logging

Each request receives a unique transactionId.

### REST Logging

Logged information:

* Endpoint
* HTTP Method
* Response Status
* Error Information

Sensitive information such as passwords and JWT tokens are never logged.

---

## Exception Handling

Implemented using `@RestControllerAdvice`.

Handled exceptions:

* AuthenticationException
* ResourceNotFoundException
* ValidationException
* MethodArgumentNotValidException
* RuntimeException

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## H2 Console

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:rest_task
```

Username:

```text
sa
```

Password:

```text
root123
```

---

## Build

```bash
mvn clean install
```

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

---

## Architecture

Controller Layer
↓
Service Layer
↓
Repository Layer
↓
Database Layer

The project follows REST principles, layered architecture, SOLID, DRY and KISS principles.
