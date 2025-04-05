# Devsu Technical Assessment

A microservices-based banking application that provides customer management and financial transaction capabilities. The system is built using Spring Boot, Spring Cloud, and follows a microservices architecture pattern.

## Project Overview

This project implements a banking system with the following features:
- Customer identity management
- Account management
- Financial transaction processing
- Service discovery and API gateway

The application is containerized using Docker and can be easily deployed using Docker Compose.

## Project Structure

The project consists of the following modules:

### api-ms-customer-identity

Microservice responsible for customer management operations:
- Customer registration and profile management
- Customer authentication
- Customer status management

Structure:
- `src/main/java`: Java source code
- `src/main/resources`: Configuration files
- `docs/postman`: Postman collection for testing the API

### api-ms-financial-movement

Microservice responsible for financial operations:
- Account management
- Transaction processing
- Balance inquiries
- Transaction reporting

Structure:
- `src/main/java`: Java source code
- `src/main/resources`: Configuration files
- `docs/postman`: Postman collection for testing the API

### api-ms-discovery-server

Eureka server for service discovery:
- Service registration
- Service discovery
- Load balancing

Structure:
- `src/main/java`: Java source code
- `src/main/resources`: Configuration files

### api-ms-entry-gateway

API Gateway that routes requests to appropriate microservices:
- Request routing
- Load balancing
- API composition

Structure:
- `src/main/java`: Java source code
- `src/main/resources`: Configuration files

### lib-ms-devsu-utils

Shared library containing common utilities and models:
- Entity models
- DTOs
- Common utilities
- Exception handling

Structure:
- `src/main/java`: Java source code

## Database

The project uses MySQL as the database. The database initialization script is located in:
- `database/initialization-script-001.sql`

This script creates the necessary databases and tables for the application, and populates them with initial data.

## Postman Collections

The project includes Postman collections for testing the APIs:
- `postman-collection/devsu-technical-challenge.postman_collection.json`

This collection contains requests for testing all the endpoints of the microservices.

## Docker and Docker Compose

The project is containerized using Docker. Each microservice has its own Dockerfile located in the `resource-dockerfiles` directory.

The Docker Compose file (`resource-dockerfiles/docker-compose.yml`) defines the following services:
- RabbitMQ for messaging
- SonarQube for code quality analysis
- MySQL for database
- phpMyAdmin for database management
- All microservices (discovery server, gateway, customer identity, financial movement)

## Getting Started

### Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

### Running with Docker Compose

1. Clone the repository:
   ```
   git clone <repository-url>
   cd devsu-technical-assessment
   ```

2. Build the project with Maven:
   ```
   mvn clean package
   ```

3. Start the application using Docker Compose:
   ```
   cd resource-dockerfiles
   docker-compose up -d
   ```

4. The services will be available at:
   - Eureka Server: http://localhost:8761
   - API Gateway: http://localhost:9090
   - Customer Identity Service: http://localhost:8081 (via Gateway)
   - Financial Movement Service: http://localhost:8082 (via Gateway)
   - phpMyAdmin: http://localhost:8090
   - RabbitMQ Management: http://localhost:15672
   - SonarQube: http://localhost:9001

### Running Locally

1. Start the MySQL database:
   ```
   cd resource-dockerfiles
   docker-compose up -d mysql rabbitmq
   ```

2. Run each microservice in the following order:
   - Discovery Server
   - API Gateway
   - Customer Identity Service
   - Financial Movement Service

   ```
   cd api-ms-discovery-server
   mvn spring-boot:run
   
   # In a new terminal
   cd api-ms-entry-gateway
   mvn spring-boot:run
   
   # In a new terminal
   cd api-ms-customer-identity
   mvn spring-boot:run
   
   # In a new terminal
   cd api-ms-financial-movement
   mvn spring-boot:run
   ```

## Testing

Import the Postman collection from `postman-collection/devsu-technical-challenge.postman_collection.json` to test the APIs.

## License

This project is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.