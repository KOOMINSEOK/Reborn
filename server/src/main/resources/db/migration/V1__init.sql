-- 앱 사용자 프로필.
-- Supabase 사용 시 id 는 auth.users.id 와 동일 값을 사용한다
-- (auth 스키마 FK 연결은 인증 단계 마이그레이션에서 추가).
create table if not exists profiles (
    id           uuid        primary key,
    handle       text        unique not null,
    display_name text        not null,
    created_at   timestamptz not null default now()
);
