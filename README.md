# Brainics Backend — Phase 1 (Foundation)

Spring Boot 3.5 · Java 21 · PostgreSQL (Neon) · Flyway · Spring Security

## What Phase 1 includes

- Spring Boot project with `dev` / `prod` profiles
- Neon PostgreSQL connection, with credentials only in environment variables
- Flyway migrations (`src/main/resources/db/migration`)
- One response format for every endpoint (`ApiResponse`) and one global error handler
- `GET /api/health` that checks the app **and** the Neon connection
- Spring Security locked down: only the health check is public (login comes in Phase 2)

## Before you start: reset your Neon password

The old database password was pasted into a chat, so treat it as leaked.
Neon dashboard → your `brainics` project → **Roles** → reset the password for `neondb_owner`.

## Run it (IntelliJ)

1. Install **JDK 21** (IntelliJ: File → Project Structure → SDK → Download JDK → 21).
2. Open this folder in IntelliJ (open `pom.xml` → "Open as Project"). Wait for Maven to finish importing.
3. Copy `.env.example` to `.env` in the same folder as `pom.xml` and fill in:
   - `DB_URL`: Neon dashboard → **Connect**. Choose the **direct** connection (host without `-pooler`) and write it as
     `jdbc:postgresql://<host>/neondb?sslmode=require`
   - `DB_USERNAME` and the **new** `DB_PASSWORD`
4. Run `BrainicsBackendApplication`.
5. Open http://localhost:8080/api/health

Expected result:

```json
{
  "success": true,
  "message": "Brainics API is running",
  "data": {
    "status": "UP",
    "application": "brainics-backend",
    "profile": "dev",
    "database": "UP",
    "schemaVersion": "1",
    "time": "2026-10-17T..."
  }
}
```

`"schemaVersion": "1"` means Flyway ran `V1__baseline.sql` on your Neon database.

Any other URL, e.g. http://localhost:8080/api/anything, should return `401` with a JSON error.
That is correct for now.

## Run the tests

```
mvn test
```

(In IntelliJ: right-click `src/test` → Run Tests.) These tests need no database.

## Troubleshooting

| Problem | Fix |
| --- | --- |
| `Could not resolve placeholder 'DB_URL'` | `.env` is missing or not in the same folder as `pom.xml`. |
| `password authentication failed` | Wrong password. Use the new one from Neon. |
| `SSL` / `channel_binding` error | Use the `?sslmode=require` URL only. Remove `channel_binding=require`. |
| Slow first start (10–30 s) | Neon free databases sleep when idle. The first connection wakes them. |
| Flyway lock or timeout errors | You used the `-pooler` host. Switch to the direct host. |

## Next: Phase 2 — authentication and roles

`V2` migration (`users`, `roles`, `user_roles`), BCrypt passwords, username/password login,
ADMIN / TEACHER / STUDENT permissions, and a secure first-admin setup.
