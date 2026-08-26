# Reborn Server

Reborn 앱의 API 서버. Kotlin + Ktor (JVM 17).

## 실행

```bash
./gradlew :server:run
```

→ http://localhost:8080/health

설정이 없어도 서버는 뜬다. 이 경우:
- `/health` → `{ "status": "ok", "db": "not_configured" }`
- `/me` → `401` (인증 비활성)

## 설정 (`server/.env`)

`.env` 는 gitignore 된다. 실제 환경변수가 있으면 그게 우선한다.

```bash
# 인증 — Supabase 액세스 토큰(ES256) 검증
SUPABASE_URL=https://xxxx.supabase.co

# DB — 있으면 부팅 시 Flyway 마이그레이션 자동 적용
DB_URL=jdbc:postgresql://<host>:<port>/postgres
DB_USER=postgres
DB_PASSWORD=<프로젝트 생성 시 설정한 비번>

# (나중) 관리자 호출·네이버 브릿지용
# SUPABASE_SECRET_KEY=sb_secret_...
```

| 변수 | 없을 때 |
|---|---|
| `SUPABASE_URL` | 인증 비활성, 보호 라우트는 401 |
| `DB_URL` | DB 없이 기동, `/health` 의 `db` 는 `not_configured` |
| `DB_USER` | 기본 `postgres` |
| `PORT` | 기본 `8080` |

## 엔드포인트

| 메서드 | 경로 | 인증 | 설명 |
|---|---|---|---|
| GET | `/health` | — | 상태 + DB 연결 여부 |
| GET | `/me` | Bearer | 토큰의 사용자 id / email |

`/me` 호출 예:

```bash
curl localhost:8080/me -H "Authorization: Bearer <앱에서 받은 Supabase 액세스 토큰>"
```

## 인증 동작

Supabase 가 발급한 JWT 를 `SUPABASE_URL/auth/v1/.well-known/jwks.json` 의 공개키로 검증한다
(ES256, 비대칭). 서버는 시크릿을 보관하지 않으며 키 회전에 자동 대응한다.

DB 마이그레이션 `V2__auth.sql` 는 `profiles.id` 를 `auth.users` 에 FK 로 연결하고,
가입 시 임시 프로필(`user_<uuid>`)을 자동 생성하는 트리거를 건다.

## 테스트

```bash
./gradlew :server:test
```
