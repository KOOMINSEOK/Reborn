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
| **내 프로필 글 (posts)** | | | 인스타식. 생전/생후(`isPosthumous`) 분류 |
| POST | `/posts` | Bearer | 글 작성 (`publishAt` 지정 시 사후 발행 예약) |
| GET | `/posts/{id}` | Bearer | 글 상세 (`liked` 포함) |
| GET | `/feed?offset=&limit=` | Bearer | 팔로우한 사람 ∪ 추천 (3:1 인터리브) |
| POST/DELETE | `/posts/{id}/like` | Bearer | 좋아요 → `{liked, likeCount}` |
| GET/POST | `/posts/{id}/comments` | Bearer | 댓글 목록 / 작성 |
| DELETE | `/post-comments/{id}` | Bearer | 본인 댓글 삭제 |
| **팔로우 (사람↔사람)** | | | |
| POST/DELETE | `/users/{id}/follow` | Bearer | 팔로우 / 언팔로우 |
| **추모 페이지 (memorials)** | | | 타인이 개설. 히스토리·방명록이 붙음 |
| POST | `/memorials` | Bearer | 추모 페이지 개설 |
| GET | `/memorials/{id}` | Bearer | 조회 |
| POST/DELETE | `/memorials/{id}/follow` | Bearer | 추모 페이지 팔로우 |
| POST/GET | `/memorials/{id}/history` | Bearer | 히스토리(추억) 글 작성 / 목록 |
| GET | `/history/{id}` | Bearer | 히스토리 글 상세 |
| POST/DELETE | `/history/{id}/like` | Bearer | 히스토리 좋아요 |
| GET/POST | `/history/{id}/comments` | Bearer | 히스토리 댓글 |
| DELETE | `/history-comments/{id}` | Bearer | 본인 히스토리 댓글 삭제 |

`/me` 호출 예:

```bash
curl localhost:8080/me -H "Authorization: Bearer <앱에서 받은 Supabase 액세스 토큰>"
```

이미지는 서버를 거치지 않는다: 앱이 Supabase Storage 에 직접 업로드하고
그 URL 을 `imageUrl` 로 넘긴다.

## 인증 동작

Supabase 가 발급한 JWT 를 `SUPABASE_URL/auth/v1/.well-known/jwks.json` 의 공개키로 검증한다
(ES256, 비대칭). 서버는 시크릿을 보관하지 않으며 키 회전에 자동 대응한다.

DB 마이그레이션 `V2__auth.sql` 는 `profiles.id` 를 `auth.users` 에 FK 로 연결하고,
가입 시 임시 프로필(`user_<uuid>`)을 자동 생성하는 트리거를 건다.

## 도메인 모델

- **posts** — 내 프로필에 쓰는 내 글. 생전/생후 분류. **홈 피드**가 이걸 보여준다.
- **memorials** — 타인이 개설한 특정 고인의 추모 페이지.
- **memorial_history** — 추모 페이지에 친구·가족이 올리는 추억 글.
- **follows** — 사람 ↔ 사람 (피드 개인화). **memorial_follows** — 사람 → 추모 페이지.

`GET /feed` = 팔로우한 사람의 최신글 ∪ 추천글(비팔로우·비공개아님·시간감쇠 랭킹)을 3:1 로 섞음.
추천 랭킹은 결정적(`ln(1+likes+2*comments) - age/45000`)이라 offset 페이지네이션이 일관된다.

좋아요/댓글 로직은 posts·memorial_history 가 동일 구조라 `InteractionRepo` 한 클래스로 재사용한다.
모든 카운트(`like_count`·`comment_count`·`follower_count`)는 DB 트리거가 관리 — 코드가 직접 안 건드림.

마이그레이션: V1 profiles · V2 auth · V3 feed(초안) · V4 post 상호작용 · V5 모델 정리(posts↔프로필, 사람 팔로우, 히스토리 분리).

## 테스트

```bash
./gradlew :server:test
```
