-- 사용자 차단. 차단하면 상대 글이 피드에서 사라지고 (양방향), 팔로우 관계도 끊긴다.

create table blocks (
    blocker_id uuid        not null references profiles (id) on delete cascade,
    blocked_id uuid        not null references profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    primary key (blocker_id, blocked_id),
    check (blocker_id <> blocked_id)
);
create index blocks_blocked_idx on blocks (blocked_id);
