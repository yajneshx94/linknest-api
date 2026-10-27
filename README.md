<div align="center">

  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&amp;size=30&amp;duration=2800&amp;pause=900&amp;color=58A6FF&amp;center=true&amp;vCenter=true&amp;width=980&amp;lines=LinkNest+%7C+Backend+REST+API%3BJava+%2B+Spring+Boot%3BJWT+%7C+Link+Management" alt="Animated title: LinkNest API" />

  <p><strong>The Java Spring Boot REST API for user accounts, link management, public link pages, and basic analytics.</strong></p>

  <p>
  <img src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&amp;logoColor=white&amp;style=for-the-badge" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring+Boot-3.5.x-6DB33F?logo=springboot&amp;logoColor=white&amp;style=for-the-badge" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/REST-API-005571?logo=fastapi&amp;logoColor=white&amp;style=for-the-badge" alt="REST API" />
  <img src="https://img.shields.io/badge/JWT-Authentication-8A2BE2?logo=jsonwebtokens&amp;logoColor=white&amp;style=for-the-badge" alt="JWT" />
  <img src="https://img.shields.io/badge/H2-local+default-1B6AC6?style=for-the-badge" alt="H2 local default" />
  <img src="https://img.shields.io/badge/PostgreSQL-support-4169E1?logo=postgresql&amp;logoColor=white&amp;style=for-the-badge" alt="PostgreSQL support" />
  </p>

  <p>
  <a href="https://github.com/yajneshx94/linknest-api/stargazers"><img src="https://img.shields.io/github/stars/yajneshx94/linknest-api?style=for-the-badge&amp;logo=github" alt="GitHub stars" /></a>
  <a href="https://github.com/yajneshx94/linknest-api/network/members"><img src="https://img.shields.io/github/forks/yajneshx94/linknest-api?style=for-the-badge&amp;logo=github" alt="GitHub forks" /></a>
  <a href="https://github.com/yajneshx94/linknest-api/commits/main/"><img src="https://img.shields.io/github/last-commit/yajneshx94/linknest-api?style=for-the-badge&amp;logo=git" alt="Last commit" /></a>
  </p>

  <a href="https://github.com/yajneshx94/linknest-api"><strong>View source repository →</strong></a>

</div>

<p align="center"><img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png" width="100%" alt="Decorative rainbow divider" /></p>

---

The Spring Boot backend for LinkNest, a full-stack link management application. It exposes REST endpoints for authentication, user profiles, link management, public link pages, click tracking, and administrator views.

**Related repository:** [LinkNest UI](https://github.com/yajneshx94/linknest-ui)

## 🧭 Contents

- [Features](#features)
- [Technology](#technology)
- [Architecture](#architecture)
- [Main endpoints](#main-endpoints)
- [JWT flow](#jwt-flow)
- [Database model](#database-model)
- [Run locally](#run-locally)
- [Tests](#tests)
- [Scope and limitations](#scope-and-limitations)

<a id="features"></a>
## Features
- User registration and login.
- JWT creation at login and JWT-based authentication for endpoints that require a signed-in user.
- Create, read, update, and delete links.
- Store profile information and public/private profile settings.
- Retrieve public link data by username.
- Track link clicks and expose basic link analytics.
- Provide administrator statistics and user-management endpoints.

<a id="technology"></a>
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

<a id="architecture"></a>
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

<a id="main-endpoints"></a>
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

<a id="jwt-flow"></a>
## JWT flow
1. A user submits username and password to `/api/auth/login`.
2. The backend authenticates the credentials and returns a signed JWT.
3. The frontend sends the token in the `Authorization: Bearer <token>` header on subsequent requests.
4. The backend JWT filter validates the token and establishes authentication for the request.

The token contains the username as its subject, an `isAdmin` claim, and issued/expiration timestamps. The token is signed with HS512. The payload is encoded, not encrypted.

<a id="database-model"></a>
## Database model
- `User` stores account and profile fields.
- `Link` stores link information and is associated with a user.
- One user can have multiple links; each link is associated with one user.

The reviewed local configuration uses a file-backed H2 database. PostgreSQL support is present, but set the correct datasource configuration for the environment where you run or deploy the application.

<a id="run-locally"></a>
## Run locally
Requirements: Java 17, Maven, and Node.js only if you also want to run the frontend.

From the directory containing `pom.xml`:

```bash
mvn spring-boot:run
```

The backend uses port `8080` by default unless configured otherwise. Check `src/main/resources/application.properties` for datasource, JWT, and service settings.

Set a strong, private JWT secret and appropriate expiration settings for your environment. Do not commit real secrets to source control. Use HTTPS for deployed environments.

<a id="tests"></a>
## Tests
Selected unit tests cover JWT token handling and user-details lookup. This is not a claim of exhaustive test coverage.

<a id="scope-and-limitations"></a>
## Scope and limitations
- The backend provides recommendation/analytics-style data, but it should not be described as a production-scale platform without additional evidence.
- Actual database behaviour depends on the datasource configuration used by the running environment.
- API access rules should be checked against the intended public-profile behaviour before deployment.

---

<div align="center">

  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png" width="100%" alt="Decorative rainbow divider" />

  <p><strong>Thanks for checking out this project!</strong></p>

  <p>
    <img src="https://visitor-badge.laobi.icu/badge?page_id=yajneshx94.linknest-api&amp;left_color=24292e&amp;right_color=58A6FF&amp;left_text=Repository%20visitors" alt="Repository visitor count" />
  </p>

</div>
