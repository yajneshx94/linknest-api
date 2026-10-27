# LinkNest API

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen) ![JPA](https://img.shields.io/badge/JPA%2FHibernate-orange) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue) ![JWT](https://img.shields.io/badge/Security-JWT-purple)


The Spring Boot backend for LinkNest, a full-stack link management application. It exposes REST endpoints for authentication, user profiles, link management, public link pages, click tracking, and administrator views.

## Features

- User registration and login.
- JWT creation at login and JWT-based authentication for endpoints that require a signed-in user.
- Create, read, update, and delete links.
- Store profile information and public/private profile settings.
- Retrieve public link data by username.
- Track link clicks and expose basic link analytics.
- Provide administrator statistics and user-management endpoints.

## Technology

- Java 17
- Spring Boot 3.5.x
- Spring Web
- Spring Security
- Spring Data JPA
- H2 for the reviewed local default configuration
- PostgreSQL driver/support for configurations that use PostgreSQL
- JWT library
- BCrypt password hashing
- Lombok
- JUnit and Mockito for selected tests

## Architecture

```text
React frontend
      ↓ HTTP / REST
Spring Boot controllers
      ↓
Application logic + JWT authentication
      ↓
Spring Data repositories
      ↓
H2 (local default) or configured relational database
```

## Main endpoints

### Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/auth/register` | Register a user |
| `POST` | `/api/auth/login` | Authenticate credentials and return a JWT |

### Links

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/links` | Retrieve the authenticated user's links |
| `POST` | `/api/links` | Create a link |
| `PUT` | `/api/links/{id}` | Update a link |
| `DELETE` | `/api/links/{id}` | Delete a link |
| `POST` | `/api/links/{id}/click` | Record a link click |
| `GET` | `/api/links/analytics` | Retrieve link analytics |
| `GET` | `/api/links/public/{username}` | Retrieve public link data |

### Profile and administration

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/profile` | Retrieve the signed-in user's profile |
| `PUT` | `/api/profile` | Update the signed-in user's profile |
| `GET` | `/api/profile/public/{username}` | Public-profile data endpoint in the controller |
| `GET` | `/api/admin/stats` | Administrator statistics |
| `GET` | `/api/admin/users` | Administrator user listing |
| `GET` | `/api/admin/users/recent` | Recently registered users |
| `GET` | `/api/admin/growth` | User growth information |
| `POST` | `/api/admin/users/{userId}/toggle-admin` | Toggle a user's administrator flag |

**Authorization note:** the reviewed security configuration explicitly permits `/api/auth/**` and `/api/links/public/**`, then requires authentication for other requests. Verify the authorization rule for `/api/profile/public/{username}` before describing that endpoint as accessible to unauthenticated visitors.

## JWT flow

1. A user submits username and password to `/api/auth/login`.
2. The backend authenticates the credentials and returns a signed JWT.
3. The frontend sends the token in the `Authorization: Bearer <token>` header on subsequent requests.
4. The backend JWT filter validates the token and establishes authentication for the request.

The token contains the username as its subject, an `isAdmin` claim, and issued/expiration timestamps. The token is signed with HS512. The payload is encoded, not encrypted.

## Database model

- `User` stores account and profile fields.
- `Link` stores link information and is associated with a user.
- One user can have multiple links; each link is associated with one user.

The reviewed local configuration uses a file-backed H2 database. PostgreSQL support is present, but set the correct datasource configuration for the environment where you run or deploy the application.

## Run locally

Requirements: Java 17, Maven, and Node.js only if you also want to run the frontend.

From the directory containing `pom.xml`:

```bash
mvn spring-boot:run
```

The backend uses port `8080` by default unless configured otherwise. Check `src/main/resources/application.properties` for datasource, JWT, and service settings.

Set a strong, private JWT secret and appropriate expiration settings for your environment. Do not commit real secrets to source control. Use HTTPS for deployed environments.

## Tests

Selected unit tests cover JWT token handling and user-details lookup. This is not a claim of exhaustive test coverage.

## Scope and limitations

- The backend provides recommendation/analytics-style data, but it should not be described as a production-scale platform without additional evidence.
- Actual database behaviour depends on the datasource configuration used by the running environment.
- API access rules should be checked against the intended public-profile behaviour before deployment.
