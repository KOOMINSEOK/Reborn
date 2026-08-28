-- 추모 페이지 방명록. 히스토리(추억 글)와 달리 이미지/좋아요/댓글 없이 짧은 메시지만.

create table guestbook_entries (
    id          uuid        primary key default gen_random_uuid(),
    memorial_id uuid        not null references memorials (id) on delete cascade,
    author_id   uuid        not null references profiles (id) on delete cascade,
    message     text        not null,
    created_at  timestamptz not null default now()
);
create index guestbook_memorial_idx on guestbook_entries (memorial_id, created_at desc);
