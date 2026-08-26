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
| POST | `/memorials` | Bearer | 추모 프로필 생성 |
| GET | `/memorials/{id}` | Bearer | 추모 프로필 조회 |
| POST | `/memorials/{id}/follow` | Bearer | 팔로우 |
| DELETE | `/memorials/{id}/follow` | Bearer | 언팔로우 |
| POST | `/posts` | Bearer | 게시물 작성 (`publishAt` 지정 시 예약) |
| GET | `/feed?offset=&limit=` | Bearer | 팔로우 ∪ 추천 게시물 (3:1 인터리브) |

`/me` 호출 예:

```bash
curl localhost:8080/me -H "Authorization: Bearer <앱에서 받은 Supabase 액세스 토큰>"
```

이미지는 서버를 거치지 않는다: 앱이 Supabase Storage 에 직접 업로드하고
그 URL 을 `POST /posts` 의 `imageUrl` 로 넘긴다.

## 인증 동작

Supabase 가 발급한 JWT 를 `SUPABASE_URL/auth/v1/.well-known/jwks.json` 의 공개키로 검증한다
(ES256, 비대칭). 서버는 시크릿을 보관하지 않으며 키 회전에 자동 대응한다.

DB 마이그레이션 `V2__auth.sql` 는 `profiles.id` 를 `auth.users` 에 FK 로 연결하고,
가입 시 임시 프로필(`user_<uuid>`)을 자동 생성하는 트리거를 건다.

## 피드

`V3__feed.sql` 이 `memorials` · `follows` · `posts` 를 만든다.
`GET /feed` 는 팔로우한 추모의 최신글과 추천글(비팔로우·공개·시간감쇠 랭킹)을 3:1 로 섞는다.
추천 랭킹은 결정적(`ln(1+likes+2*comments) - age/45000`)이라 offset 페이지네이션이 일관된다.

## 테스트

```bash
./gradlew :server:test
```
