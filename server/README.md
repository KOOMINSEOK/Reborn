# Reborn Server

Reborn 앱의 API 서버. Kotlin + Ktor (JVM 17).

## 실행

```bash
./gradlew :server:run
```

→ http://localhost:8080/health

DB 환경변수가 없으면 **DB 없이 기동**한다. `/health` 응답:

```json
{ "status": "ok", "db": "not_configured" }
```

## 환경변수

| 변수 | 필수 | 예시 |
|---|---|---|
| `PORT` | 아니오 (기본 8080) | `8080` |
| `DB_URL` | 아니오 (없으면 DB 스킵) | `jdbc:postgresql://db.xxxx.supabase.co:5432/postgres` |
| `DB_USER` | 아니오 (기본 `postgres`) | `postgres` |
| `DB_PASSWORD` | 아니오 | `••••••` |

`DB_URL` 을 넣으면 부팅 시 Flyway 마이그레이션(`src/main/resources/db/migration`)이 적용되고
`/health` 의 `db` 가 `connected` 로 바뀐다.

로컬에서 값을 넣고 돌리려면:

```bash
DB_URL="jdbc:postgresql://localhost:5432/reborn" DB_USER=postgres DB_PASSWORD=postgres ./gradlew :server:run
```

## 테스트

```bash
./gradlew :server:test
```

## 다음 단계

인증(Supabase JWT 검증 + `GET /me`)은 `feature/server-auth` 브랜치에서.
