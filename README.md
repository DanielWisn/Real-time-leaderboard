# Real-Time Leaderboard

A backend system for a real-time competitive leaderboard service built with Spring Boot. Users can register, log in, submit scores for various games, and view live rankings. The system uses Redis sorted sets for efficient real-time leaderboard queries and PostgreSQL for persistent score history.

## Features

- **User Authentication** — register and log in via Spring Security session-based form login
- **Score Submission** — submit scores for any game; all submissions are persisted to PostgreSQL
- **Per-Game Leaderboard** — view the top players for a specific game, ranked by their personal best score
- **Global Leaderboard** — view the top players across all games combined, ranked by cumulative best scores
- **User Rankings** — query a specific user's rank within a game or globally
- **Top Players Report** — generate a report of the highest single scores submitted within a given date range across all games

## Tech Stack

- **Java 17**
- **Spring Boot 4.0.4**
- **Spring Security** — session-based authentication with form login
- **Spring Session + Redis** — Redis-backed HTTP session storage
- **Spring Data JPA / Hibernate** — ORM for PostgreSQL
- **Spring Data Redis / Lettuce** — Redis client for leaderboard sorted sets
- **PostgreSQL** — persistent storage for users, games, and full score history
- **Redis** — sorted sets (`ZSET`) for real-time leaderboard rankings
- **Docker / Docker Compose** — containerised infrastructure

## Architecture

### Session-Based Identity

After logging in, Spring Security stores the authenticated user's identity in the HTTP session, which is persisted to Redis via Spring Session. On every subsequent request, the session cookie sent by the client is resolved back to the stored security context — meaning the server always knows who is making the request without the client needing to send a user ID explicitly.

Authenticated endpoints like `POST /scores/save` and `GET /scores/userScore` resolve the current user directly from the session:

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName(); // resolved from session, no userId needed in the request body
```

This means score submission only requires the game ID and the score value — the user is identified automatically from the active session. The session itself is stored in Redis as a hash under `spring:session:sessions:{sessionId}`, keeping it available across potential multiple instances of the application.

### Score Storage

Score submissions trigger two writes:

- **PostgreSQL** receives every submission with a timestamp, forming a complete history used for time-range reports.
- **Redis** maintains one sorted set per game (`leaderboard:game:{gameId}`) holding each user's personal best score only, and a second global sorted set (`leaderboard:global`) holding each user's cumulative best scores across all games. Redis is updated incrementally on each submission — only when a new personal best is achieved.

This separation means leaderboard reads (`ZREVRANGE`, `ZREVRANK`) are served entirely from Redis with no database involvement, while historical queries (Top Players Report) go directly to PostgreSQL.

## Prerequisites

- Docker and Docker Compose
- Java 17+
- Maven

## Getting Started

**1. Start PostgreSQL and Redis**

```bash
docker compose up -d
```

**2. Run the application**

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. PostgreSQL is exposed on port `5332` and Redis on `6379`.

**3. Reset all data (wipe volumes)**

```bash
docker compose down -v
docker compose up -d
```

## API Endpoints

### Authentication

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/users/register` | Public | Register a new user |
| POST | `/login` | Public | Log in (form-encoded: `username`, `password`) |
| POST | `/logout` | Public | Log out |

### Games

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/games` | Public | List all games |
| POST | `/games` | Public | Create a new game |
| DELETE | `/games/{id}` | Public | Delete a game |

### Scores & Leaderboard

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/scores/save` | Authenticated | Submit a score for a game |
| GET | `/scores/leaderboard/{gameId}` | Public | Top N players for a specific game |
| GET | `/scores/leaderboard/global` | Public | Top N players across all games |
| GET | `/scores/userScore` | Authenticated | Logged-in user's rank and best scores per game |
| GET | `/scores/report` | Public | Top scores within a date range across all games |

### Example Requests

**Register**
```http
POST http://localhost:8080/users/register
Content-Type: application/json

{
  "username": "daniel",
  "password": "password123"
}
```

**Login**
```http
POST http://localhost:8080/login
Content-Type: application/x-www-form-urlencoded

username=daniel&password=password123
```

**Submit a score** — user is resolved from the active session, no userId needed
```http
POST http://localhost:8080/scores/save
Content-Type: application/json

{
  "gameId": 1,
  "score": 1500
}
```

**Get top 10 for a game**
```http
GET http://localhost:8080/scores/leaderboard/1?top=10
```

**Get top players report**
```http
GET http://localhost:8080/scores/report?from=2024-01-01&to=2024-12-31
```

## Data Model

**User** — `id`, `username`, `password` (BCrypt hashed)

**Game** — `id`, `title`

**Score** — `id`, `user`, `game`, `score`, `date` — every submission stored with timestamp

## Redis Key Structure

```
leaderboard:game:{gameId}   — sorted set, member = userId, score = personal best
leaderboard:global          — sorted set, member = userId, score = sum of personal bests across all games
spring:session:sessions:*   — Spring Session data (managed automatically)
```