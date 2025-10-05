# LinkNest API - A Multi-Tenant Link-in-Bio Service

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen) ![JPA](https://img.shields.io/badge/JPA%2FHibernate-orange) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue) ![JWT](https://img.shields.io/badge/Security-JWT-purple)

This repository contains the backend REST API for **LinkNest**, a full-stack, multi-tenant "link-in-bio" application. This service allows users to register for an account, manage a personal list of links through a secure dashboard, and share them via a single, public profile page.

The core of this project is its **multi-tenant architecture**, designed to serve multiple users (tenants) from a single running instance of the application while ensuring strict data isolation and security.

**Live Frontend Demo:** [Link to your deployed Vercel URL will go here]
**Frontend Repository:** [Link to your `linknest-ui` GitHub repository]

---

## Key Features & Architectural Concepts

This API demonstrates several key concepts crucial for modern backend development:

* **Multi-Tenant Architecture:** Built from the ground up to support multiple users securely. The application uses a **Shared Database, Shared Schema** approach, where a `user_id` foreign key on the `links` table ensures that every piece of data is tied to a specific tenant. All database queries are scoped to the authenticated user, guaranteeing strict data isolation.

* **Secure Authentication:** User authentication is handled via **JSON Web Tokens (JWT)**. The API provides endpoints for user registration and login. Upon successful login, a signed JWT is issued to the client, which is then used to authorize access to protected resources.

* **Full CRUD Functionality:** The API provides a complete set of RESTful endpoints for managing links, including creating, reading, updating, and deleting (CRUD) operations for the authenticated user.

* **Public & Protected Endpoints:** The application correctly separates public-facing endpoints (like viewing a user's profile) from protected endpoints (like the user's private dashboard for managing links), which are secured using the JWT authentication filter.

---

## Tech Stack

This API is built with a modern, robust Java-based stack:

* **Framework:** Spring Boot 3
* **Security:** Spring Security 6, JSON Web Tokens (JWT)
* **Database:** Spring Data JPA / Hibernate
* **Database Driver:** PostgreSQL (for production), H2 (for local development)
* **Build Tool:** Maven
* **Language:** Java 17

---

## API Endpoints

All endpoints are prefixed with `/api`.

### Authentication (`/auth`)

| Method | Endpoint         | Access | Description                             |
| :----- | :--------------- | :----- | :-------------------------------------- |
| `POST` | `/auth/register` | Public | Registers a new user (tenant).          |
| `POST` | `/auth/login`    | Public | Authenticates a user and returns a JWT. |

### Links (`/links`)

| Method   | Endpoint                   | Access    | Description                                     |
| :------- | :------------------------- | :-------- | :------------------------------------------------ |
| `GET`    | `/links`                   | Protected | Retrieves all links for the authenticated user.   |
| `POST`   | `/links`                   | Protected | Creates a new link for the authenticated user.    |
| `PUT`    | `/links/{id}`              | Protected | Updates an existing link owned by the user.       |
| `DELETE` | `/links/{id}`              | Protected | Deletes a link owned by the user.                 |
| `GET`    | `/links/public/{username}` | Public    | Retrieves the public list of links for a user.    |

---

## Setup and Run Locally

To run this project on your local machine, follow these steps:

1.  **Prerequisites:**
    * Java 17 (or later)
    * Maven

2.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/linknest-api.git](https://github.com/your-username/linknest-api.git)
    cd linknest-api
    ```

3.  **Run the application:**
    The project is configured to use an in-memory H2 database by default for easy setup.
    ```bash
    ./mvnw spring-boot:run
    ```
    The API will be available at `http://localhost:8080`.

4.  **Access the H2 Console (Optional):**
    While the application is running, you can view the in-memory database at `http://localhost:8080/h2-console`. Use the JDBC URL `jdbc:h2:mem:linknestdb`.
