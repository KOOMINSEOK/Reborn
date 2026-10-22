-- 검색 정렬용 조회수 · 조화.
-- 조회수: 다른 사람이 프로필·추모 페이지를 열면 +1. 같은 사람은 하루에 한 번만 센다 (새로고침으로 부풀리지 않게).
alter table profiles add column view_count bigint not null default 0;
alter table memorials add column view_count bigint not null default 0;

create table page_views (
    viewer_id   uuid not null references profiles (id) on delete cascade,
    target_kind text not null check (target_kind in ('profile', 'memorial')),
    target_id   uuid not null,
    day         date not null default current_date,
    primary key (viewer_id, target_kind, target_id, day)
);

-- 조화(근조화환). 구매·결제가 붙으면 여기에 쌓인다 — 지금은 표만 (검색의 조화순이 이 수를 센다).
create table wreaths (
    id          uuid        primary key default gen_random_uuid(),
    memorial_id uuid        not null references memorials (id) on delete cascade,
    sender_id   uuid        references profiles (id) on delete set null,
    sender_name text        not null,
    message     text        not null default '',
    created_at  timestamptz not null default now()
);
create index wreaths_memorial_idx on wreaths (memorial_id);
