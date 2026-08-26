-- 소셜 그래프(memorials, follows) + 게시물(posts) + 피드.

create table memorials (
    id                uuid        primary key default gen_random_uuid(),
    owner_id          uuid        not null references profiles (id) on delete cascade,
    name              text        not null,
    handle            text        unique not null,
    bio               text,
    is_posthumous     boolean     not null default false,   -- 고인 본인 계정 여부
    visibility        text        not null default 'public'
                                  check (visibility in ('public', 'followers', 'private')),
    profile_image_url text,
    follower_count    int         not null default 0,
    created_at        timestamptz not null default now()
);
create index memorials_owner_idx on memorials (owner_id);

create table follows (
    follower_id uuid        not null references profiles (id) on delete cascade,
    memorial_id uuid        not null references memorials (id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (follower_id, memorial_id)
);
create index follows_memorial_idx on follows (memorial_id);

create table posts (
    id            uuid        primary key default gen_random_uuid(),
    memorial_id   uuid        not null references memorials (id) on delete cascade,
    author_id     uuid        not null references profiles (id) on delete cascade,
    caption       text        not null default '',
    image_url     text,
    is_posthumous boolean     not null default false,   -- 고인 생전 예약 게시물 여부
    status        text        not null default 'published'
                              check (status in ('published', 'scheduled')),
    publish_at    timestamptz,
    like_count    int         not null default 0,
    comment_count int         not null default 0,
    created_at    timestamptz not null default now()
);
create index posts_memorial_idx on posts (memorial_id, created_at desc);
create index posts_published_idx on posts (created_at desc) where status = 'published';

-- 팔로우 증감 시 memorials.follower_count 동기화.
create or replace function public.sync_follower_count()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    if tg_op = 'INSERT' then
        update public.memorials set follower_count = follower_count + 1 where id = new.memorial_id;
    elsif tg_op = 'DELETE' then
        update public.memorials set follower_count = greatest(follower_count - 1, 0) where id = old.memorial_id;
    end if;
    return null;
end;
$$;

drop trigger if exists follows_count_sync on follows;
create trigger follows_count_sync
    after insert or delete on follows
    for each row execute function public.sync_follower_count();
