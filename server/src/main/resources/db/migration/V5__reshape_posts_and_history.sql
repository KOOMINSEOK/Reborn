-- 게시물/팔로우 모델 정리 + 메모리얼 히스토리 분리.
--
-- posts        = 내 프로필에 쓰는 내 글 (인스타식). 생전/생후(is_posthumous) 분류. 홈 피드.
-- memorials    = 타인이 개설한 특정 고인의 추모 페이지.
-- memorial_history = 추모 페이지에 친구·가족이 올리는 추억 글.
-- follows          = 사람 ↔ 사람 (피드 개인화).
-- memorial_follows = 사람 → 추모 페이지 (팔로워 수).

-- 1. posts: 프로필 글이므로 memorial_id 제거.
alter table posts drop column memorial_id cascade;

-- 2. profiles: 아바타 + 비공개 계정 플래그.
alter table profiles add column avatar_url text;
alter table profiles add column is_private boolean not null default false;

-- 3. follows: 메모리얼 팔로우 → 사람 팔로우로 재구성 (데이터 없음).
drop table follows;
create table follows (
    follower_id uuid        not null references profiles (id) on delete cascade,
    followee_id uuid        not null references profiles (id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (follower_id, followee_id),
    check (follower_id <> followee_id)
);
create index follows_followee_idx on follows (followee_id);

-- 4. 메모리얼 팔로우는 별도 테이블. V3 의 sync_follower_count 함수를 그대로 재사용.
create table memorial_follows (
    follower_id uuid        not null references profiles (id) on delete cascade,
    memorial_id uuid        not null references memorials (id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (follower_id, memorial_id)
);
create index memorial_follows_memorial_idx on memorial_follows (memorial_id);

create trigger memorial_follows_count_sync
    after insert or delete on memorial_follows
    for each row execute function public.sync_follower_count();

-- 5. memorials: 개설자 명확화, 계정 아님.
alter table memorials rename column owner_id to creator_id;
alter table memorials drop column is_posthumous;

-- 6. 메모리얼 히스토리(추억) 글.
create table memorial_history (
    id            uuid        primary key default gen_random_uuid(),
    memorial_id   uuid        not null references memorials (id) on delete cascade,
    author_id     uuid        not null references profiles (id) on delete cascade,
    caption       text        not null default '',
    image_url     text,
    like_count    int         not null default 0,
    comment_count int         not null default 0,
    created_at    timestamptz not null default now()
);
create index memorial_history_memorial_idx on memorial_history (memorial_id, created_at desc);

create table history_likes (
    history_id uuid        not null references memorial_history (id) on delete cascade,
    user_id    uuid        not null references profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    primary key (history_id, user_id)
);
create index history_likes_user_idx on history_likes (user_id);

create table history_comments (
    id         uuid        primary key default gen_random_uuid(),
    history_id uuid        not null references memorial_history (id) on delete cascade,
    author_id  uuid        not null references profiles (id) on delete cascade,
    body       text        not null,
    created_at timestamptz not null default now()
);
create index history_comments_history_idx on history_comments (history_id, created_at);

-- 7. 히스토리 카운트 동기화 트리거.
create or replace function public.sync_history_like_count()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        update public.memorial_history set like_count = like_count + 1 where id = new.history_id;
    elsif tg_op = 'DELETE' then
        update public.memorial_history set like_count = greatest(like_count - 1, 0) where id = old.history_id;
    end if;
    return null;
end;
$$;

create or replace function public.sync_history_comment_count()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        update public.memorial_history set comment_count = comment_count + 1 where id = new.history_id;
    elsif tg_op = 'DELETE' then
        update public.memorial_history set comment_count = greatest(comment_count - 1, 0) where id = old.history_id;
    end if;
    return null;
end;
$$;

create trigger history_likes_count_sync
    after insert or delete on history_likes
    for each row execute function public.sync_history_like_count();

create trigger history_comments_count_sync
    after insert or delete on history_comments
    for each row execute function public.sync_history_comment_count();
