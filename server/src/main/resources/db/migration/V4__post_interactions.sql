-- 게시물 좋아요 / 댓글 + posts 카운트 동기화.

create table post_likes (
    post_id    uuid        not null references posts (id) on delete cascade,
    user_id    uuid        not null references profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    primary key (post_id, user_id)
);
create index post_likes_user_idx on post_likes (user_id);

create table post_comments (
    id         uuid        primary key default gen_random_uuid(),
    post_id    uuid        not null references posts (id) on delete cascade,
    author_id  uuid        not null references profiles (id) on delete cascade,
    body       text        not null,
    created_at timestamptz not null default now()
);
create index post_comments_post_idx on post_comments (post_id, created_at);

create or replace function public.sync_post_like_count()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    if tg_op = 'INSERT' then
        update public.posts set like_count = like_count + 1 where id = new.post_id;
    elsif tg_op = 'DELETE' then
        update public.posts set like_count = greatest(like_count - 1, 0) where id = old.post_id;
    end if;
    return null;
end;
$$;

create or replace function public.sync_post_comment_count()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    if tg_op = 'INSERT' then
        update public.posts set comment_count = comment_count + 1 where id = new.post_id;
    elsif tg_op = 'DELETE' then
        update public.posts set comment_count = greatest(comment_count - 1, 0) where id = old.post_id;
    end if;
    return null;
end;
$$;

drop trigger if exists post_likes_count_sync on post_likes;
create trigger post_likes_count_sync
    after insert or delete on post_likes
    for each row execute function public.sync_post_like_count();

drop trigger if exists post_comments_count_sync on post_comments;
create trigger post_comments_count_sync
    after insert or delete on post_comments
    for each row execute function public.sync_post_comment_count();
