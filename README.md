# Notification-Service

## Overview

Notification-Service is the microservice in the APA-CRM platform responsible for delivering real-time notifications to users and organizations. Built with **Java** and **Spring Boot**, it leverages WebSockets to provide immediate updates and messaging capabilities across the platform. The service supports authorization and authentication via integration with the Auth microservice and ensures secure and reliable notification delivery.

## Features

- Real-time notifications via WebSockets (STOMP protocol)
- Supports targeted notifications to individual users or entire organizations
- Integration with APA-CRM Auth service for secure messaging
- Feign client support for service-to-service communication
- Easy configuration for local or containerized deployment

## Technology Stack

- **Java**
- **Spring Boot** (microservices framework)
- **Spring WebSocket** (real-time communication)
- **Spring Cloud OpenFeign** (inter-service calls)
- **Docker** (container runtime)
- **Maven**

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker (optional)
- Git

## Build & Run Instructions

### 1. Clone the repository

```sh
git clone https://github.com/APA-CRM/Notification-Service.git
cd Notification-Service
```

### 2. Build with Maven

```sh
./mvnw clean install
```

### 3. Run the service

```sh
./mvnw spring-boot:run
```

Service will run on the default Spring Boot port (usually `8080`). You can customize the port and other settings in `src/main/resources/application.properties`.

### 4. Run with Docker (optional)

```sh
docker build -t notification-service .
docker run -p 8080:8080 notification-service
```

### 5. WebSocket Endpoints

- WebSocket root: `/ws-notifications`
- Subscribe to topics: `/topic/notifications`, `/topic/organizations/{organizationId}/notifications`
- User-specific notifications: `/user/{userId}/queue/notifications`

### 6. REST Testing Endpoints

- Send message to user: `POST /api/test/notifications/users/{userId}`
- Send message to organization: `POST /api/test/notifications/organizations/{organizationId}`
- Get all connected users: `GET /api/test/notifications/users`

## Configuration

- Auth service integration: requires `auth-service` URL configuration for secure WebSocket authorization.
- WebSocket settings are defined in `WebSocketsConfig.java` and can be tuned for production needs.

## Contribution Guidelines

Contributions are welcome! Please follow these steps:

### Development

- Fork this repository and clone your fork.
- Create a new feature or bugfix branch:
  ```sh
  git checkout -b feature/my-feature
  ```
- Follow standard Java coding practices and project conventions.
- Commit changes with clear messages.

### Pull Requests

- Open a pull request against the `develop` branch.
- Complete the PR template, describing your changes and rationale.
- Make sure your code passes all CI tests and checks.
- Address reviews promptly.

### Issues

- Use GitHub Issues for bug reports and feature requests.
- Include details to help reproducing bugs (logs, configuration).

### Code of Conduct

- Be respectful and collaborative.
- Follow the [Contributor Covenant](https://www.contributor-covenant.org/).

## License

Licensed under the [Apache License 2.0](./LICENSE).

## Contact & Support

If you have questions, please open an issue or contact a maintainer via GitHub.

---

_For more details, refer to the code documentation or reach out to the APA-CRM team._
