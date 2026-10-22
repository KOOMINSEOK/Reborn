-- 저장(스크랩): 내가 저장한 게시물·추억 글. 원글이 지워지거나 못 보게 되면 목록에서 빠진다 (조회 때 걸러서).
create table saves (
    user_id    uuid        not null references profiles (id) on delete cascade,
    kind       text        not null check (kind in ('post', 'history')),
    target_id  uuid        not null,
    created_at timestamptz not null default now(),
    primary key (user_id, kind, target_id)
);
create index saves_user_idx on saves (user_id, kind, created_at desc);

-- 보관: 내 게시물을 지우지 않고 숨긴다. 보관한 글은 나만 보관함에서 보고, 피드·프로필·검색에서 빠진다.
alter table posts add column archived_at timestamptz;
