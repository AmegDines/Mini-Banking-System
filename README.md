# MiniBank

A small banking management app with a Spring Boot REST API, MySQL persistence, and a React/Vite kiosk interface.

## Run with Docker Compose

From the project root:

```sh
docker compose up --build
```

Open [http://localhost:8080](http://localhost:8080). Compose starts MySQL and builds the React assets into the Spring Boot application. Set `DB_PASSWORD` in `docker-compose.yml` before using this setup outside local development.

## Run locally

Start the database with `docker compose up -d db`, then start the backend from the project root:

```sh
mvn spring-boot:run
```

In another terminal, start Vite:

```sh
cd frontend
npm ci
npm run dev
```

Open the URL printed by Vite. Its `/api` requests are proxied to Spring Boot on port 8080.

## API

- `POST /api/accounts` — create an account (`name`, `email`, `accountType`, `initialBalance`)
- `GET /api/accounts/{accountNumber}` — fetch an account
- `GET /api/accounts` — list accounts for the kiosk
- `POST /api/transactions/deposit` — `{ "accountNumber": "MB…", "amount": 25.00 }`
- `POST /api/transactions/withdraw` — `{ "accountNumber": "MB…", "amount": 10.00 }`
- `POST /api/transactions/transfer` — `{ "senderAccNo": "MB…", "receiverAccNo": "MB…", "amount": 5.00 }`
- `GET /api/transactions/account/{accountNumber}` — transaction history

Transactions use `BigDecimal` amounts. Transfer and withdrawal operations are transactional. API errors use `{ "timestamp", "message", "status" }`.

## Build

```sh
mvn -B package
cd frontend
npm ci
npm run build
npm audit
```

The Maven package runs the JUnit suite. GitHub Actions builds the frontend and packages/tests the backend on pushes and pull requests to `main`.
