# Transaction Management System

A robust and scalable transaction management system built with Spring Boot, providing RESTful APIs for managing financial transactions.

## Project Overview

This system provides a comprehensive solution for managing financial transactions with features including:
- Transaction creation, retrieval, update, and deletion
- Pagination and sorting support
- Input validation
- Idempotency support
- Caching
- Comprehensive error handling
- API documentation with Swagger/OpenAPI
- Prometheus monitoring

## Requirements Met

Based on the project requirements, this system fulfills the following criteria:

- Uses Java 21 and Spring Boot
- Stores data in memory using H2 Database
- Provides RESTful APIs for transaction management
- Implements comprehensive testing (unit, integration with TestContainers, validation, idempotency, stress/load consideration)
- Uses Maven for build management
- Documents APIs with Swagger/OpenAPI
- Implements idempotency for POST requests
- Implements caching for transaction list retrieval
- Uses Log4j2 for logging
- Integrates Prometheus for application monitoring
- Implements robust error handling
- Provides a clear README file

## Technology Stack

- Java 21
- Spring Boot 3.2.5
- Spring Data JPA
- H2 Database (in-memory)
- Log4j2 for logging
- Swagger/OpenAPI for API documentation
- Lombok for reducing boilerplate code
- JUnit 5 and TestContainers for testing
- **Spring Boot Actuator** and **Micrometer Prometheus Registry** for monitoring

## API Endpoints

### Transaction Management

#### GET /api/transactions
- Retrieves a paginated list of transactions
- Query Parameters:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 10)
  - `sort`: Sort field and direction (format: field,direction, default: id,desc)

#### GET /api/transactions/{id}
- Retrieves a specific transaction by ID
- Path Parameter:
  - `id`: Transaction ID

#### POST /api/transactions
- Creates a new transaction
- Headers:
  - `Idempotency-Key`: Required for idempotent operations
- Request Body:
  ```json
  {
    "description": "string",
    "amount": "decimal",
    "type": "INCOME|EXPENSE"
  }
  ```

#### PUT /api/transactions/{id}
- Updates an existing transaction
- Path Parameter:
  - `id`: Transaction ID
- Request Body:
  ```json
  {
    "description": "string",
    "amount": "decimal",
    "type": "INCOME|EXPENSE"
  }
  ```

#### DELETE /api/transactions/{id}
- Deletes a transaction
- Path Parameter:
  - `id`: Transaction ID

## Data Models

### Transaction Entity
```java
public class Transaction {
    private Long id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}
```

### Transaction Types
- INCOME
- EXPENSE

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

### Access Swagger Documentation
Once the application is running, access the API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## Features

### Input Validation
- Description: Not blank, max 255 characters
- Amount: Not null, must be positive
- Type: Not null, must be valid transaction type

### Idempotency
- All POST requests require an Idempotency-Key header
- Prevents duplicate transaction creation

### Caching
- Transaction list is cached for better performance
- Cache is automatically invalidated on updates

### Error Handling
- Comprehensive error handling with meaningful messages
- Business exceptions for common scenarios
- Validation errors with detailed feedback

## Prometheus Monitoring

This project integrates Prometheus for collecting application metrics. Spring Boot Actuator and Micrometer are used to expose metrics in a format that Prometheus can scrape.

### Configuration

1.  **Dependencies**: Ensure the following dependencies are included in `pom.xml`:
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    ```

2.  **Application Configuration**: The `application.yml` file is configured to expose the Prometheus endpoint:
    ```yaml
    management:
      endpoints:
        web:
          exposure:
            include: health,info,prometheus,metrics
      prometheus:
        metrics:
          export:
            enabled: true
    ```

3.  **Prometheus Configuration (Kubernetes)**: The `k8s/prometheus-config.yaml` file defines how Prometheus scrapes metrics from the application:
    ```yaml
    scrape_configs:
      - job_name: 'transaction-management'
        metrics_path: '/actuator/prometheus'
        static_configs:
          - targets: ['transaction-management-service:8080']
        scheme: http
    ```
    _Note: Replace `transaction-management-service:8080` with the actual service name and port of your application in your Kubernetes cluster._

### Accessing Metrics

-   **Application Endpoint**: Metrics are available at the `/actuator/prometheus` endpoint of your application:
    ```
    http://<your-app-ip>:8080/actuator/prometheus
    ```

-   **Prometheus UI**: Once deployed, you can access the Prometheus web UI to query and visualize metrics. The default port is 9090.
    ```
    http://<prometheus-service-ip>:9090
    ```

### Deployment

Use the provided Kubernetes manifests in the `k8s` directory to deploy Prometheus to your cluster:

```bash
kubectl apply -f k8s/prometheus-config.yaml
kubectl apply -f k8s/prometheus-deployment.yaml
```

## Testing

The project includes comprehensive test coverage:
- Unit tests for services and controllers
- Integration tests with TestContainers
- Validation tests for request/response handling

Run tests with:
```bash
mvn test
```

## Logging

The application uses Log4j2 for logging with the following features:
- Structured logging
- Different log levels (INFO, WARN, ERROR)
- Log rotation and file management

## Security Considerations

- Input validation on all endpoints
- Idempotency checks for POST requests
- Proper error handling and logging
- No sensitive data exposure in responses

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Error Codes

### Overview
The system uses a standardized error code system to provide clear and consistent error responses. Error codes are organized into different ranges based on their category.

### Error Code Ranges
| Range | Category | Description |
|-------|----------|-------------|
| 0 | Success | Successful operation |
| 1000-1999 | Parameter Validation | Errors related to input parameter validation |
| 2000-2999 | Transaction | Errors related to transaction processing |
| 3000-3999 | Idempotency | Errors related to request idempotency |
| 5000-5999 | System | Internal system errors |

### Common Error Codes
| Code | Message | HTTP Status | Description |
|------|---------|-------------|-------------|
| 0 | success | 200 OK | Operation completed successfully |
| 1001 | Parameter validation failed | 400 Bad Request | Input parameters failed validation |
| 2001 | Transaction not found | 404 Not Found | Requested transaction does not exist |
| 3001 | Idempotency-Key header is required | 400 Bad Request | Missing required idempotency key |
| 3002 | Repeated request | 409 Conflict | Duplicate request detected |
| 5000 | Internal server error | 500 Internal Server Error | Unexpected system error |

## Exception Handling

### Business Exceptions
The system uses `BusinessException` for handling business-specific errors. Each exception includes:
- Error code
- Error message
- HTTP status code
- Optional additional data

### Exception Hierarchy
```
RuntimeException
└── BusinessException
    ├── TransactionNotFoundException
    ├── ValidationException
    ├── IdempotencyException
    └── SystemException
```

### Exception Usage
```java
// Example of throwing a business exception
throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);

// Example with custom message
throw new BusinessException(ErrorCode.PARAM_VALIDATION_FAILED, "Invalid amount format");

// Example with additional data
throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE, "Insufficient funds", balanceData);
```

## Development Guide

### Adding New Error Codes
When adding new error codes:
1. Follow the established error code ranges
2. Add appropriate JavaDoc documentation
3. Include HTTP status code
4. Provide clear error message
5. Update this documentation

### Error Code Format
- Use numeric codes for better compatibility
- Follow the range guidelines
- Keep messages concise and clear
- Include appropriate HTTP status codes

### Best Practices
1. Always use predefined error codes when possible
2. Add new error codes only when necessary
3. Document all error codes and their usage
4. Include error codes in API documentation
5. Test error scenarios thoroughly

## Appendix

### Error Code Reference
For a complete list of error codes, see `ErrorCode.java` in the source code.

### HTTP Status Codes
The system uses standard HTTP status codes:
- 200: Success
- 400: Bad Request
- 404: Not Found
- 409: Conflict
- 500: Internal Server Error

### Related Documentation
- [API Documentation](docs/api.md)
- [Error Handling Guide](docs/error-handling.md)
- [Development Guidelines](docs/development.md)

## Table of Contents
- [Overview](#overview)
- [Requirements Met](#requirements-met)
- [Technology Stack](#technology-stack)
- [API Endpoints](#api-endpoints)
- [Data Models](#data-models)
- [Building and Running](#building-and-running)
- [Features](#features)
- [Prometheus Monitoring](#prometheus-monitoring)
- [Testing](#testing)
- [Logging](#logging)
- [Security Considerations](#security-considerations)
- [Contributing](#contributing)
- [Error Codes](#error-codes)
- [Exception Handling](#exception-handling)
- [Development Guide](#development-guide)
- [Appendix](#appendix)
