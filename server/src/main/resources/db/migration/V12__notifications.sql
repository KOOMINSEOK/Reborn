-- 앱 안 알림 (좋아요·댓글·팔로우·방명록).
-- 알림은 DB 트리거가 만든다 → 좋아요·댓글 등을 넣는 경로가 몇 개든 빠짐없이, 한 트랜잭션 안에서.
-- 좋아요 취소·언팔로우·댓글 삭제 때는 그 알림도 지운다 (눌렀다 취소한 좋아요가 남지 않게).
-- 자기 글에 자기가 한 행동은 알리지 않는다.

create table notifications (
    id           uuid        primary key default gen_random_uuid(),
    recipient_id uuid        not null references profiles (id) on delete cascade,
    actor_id     uuid        not null references profiles (id) on delete cascade,
    type         text        not null check (type in
                     ('post_like', 'post_comment', 'history_like', 'history_comment', 'follow', 'guestbook')),
    post_id      uuid        references posts (id) on delete cascade,
    history_id   uuid        references memorial_history (id) on delete cascade,
    memorial_id  uuid        references memorials (id) on delete cascade,
    -- 댓글·방명록 id (지워지면 알림도 지운다). FK 는 종류마다 표가 달라 걸지 않고 트리거로 지운다.
    source_id    uuid,
    -- 댓글·방명록 앞부분 (목록에 바로 보이게)
    preview      text,
    created_at   timestamptz not null default now(),
    read_at      timestamptz
);
create index notifications_recipient_idx on notifications (recipient_id, created_at desc, id desc);
create index notifications_unread_idx on notifications (recipient_id) where read_at is null;
create index notifications_source_idx on notifications (source_id) where source_id is not null;

-- ---------- 게시글 좋아요 ----------
create or replace function public.notify_post_like()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        insert into public.notifications (recipient_id, actor_id, type, post_id)
        select p.author_id, new.user_id, 'post_like', p.id
        from public.posts p where p.id = new.post_id and p.author_id <> new.user_id;
    else
        delete from public.notifications
        where type = 'post_like' and post_id = old.post_id and actor_id = old.user_id;
    end if;
    return null;
end;
$$;
create trigger post_likes_notify after insert or delete on post_likes
    for each row execute function public.notify_post_like();

-- ---------- 게시글 댓글 ----------
create or replace function public.notify_post_comment()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        insert into public.notifications (recipient_id, actor_id, type, post_id, source_id, preview)
        select p.author_id, new.author_id, 'post_comment', p.id, new.id, left(new.body, 100)
        from public.posts p where p.id = new.post_id and p.author_id <> new.author_id;
    else
        delete from public.notifications where source_id = old.id;
    end if;
    return null;
end;
$$;
create trigger post_comments_notify after insert or delete on post_comments
    for each row execute function public.notify_post_comment();

-- ---------- 추억 글 좋아요 ----------
create or replace function public.notify_history_like()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        insert into public.notifications (recipient_id, actor_id, type, history_id, memorial_id)
        select h.author_id, new.user_id, 'history_like', h.id, h.memorial_id
        from public.memorial_history h where h.id = new.history_id and h.author_id <> new.user_id;
    else
        delete from public.notifications
        where type = 'history_like' and history_id = old.history_id and actor_id = old.user_id;
    end if;
    return null;
end;
$$;
create trigger history_likes_notify after insert or delete on history_likes
    for each row execute function public.notify_history_like();

-- ---------- 추억 글 댓글 ----------
create or replace function public.notify_history_comment()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        insert into public.notifications (recipient_id, actor_id, type, history_id, memorial_id, source_id, preview)
        select h.author_id, new.author_id, 'history_comment', h.id, h.memorial_id, new.id, left(new.body, 100)
        from public.memorial_history h where h.id = new.history_id and h.author_id <> new.author_id;
    else
        delete from public.notifications where source_id = old.id;
    end if;
    return null;
end;
$$;
create trigger history_comments_notify after insert or delete on history_comments
    for each row execute function public.notify_history_comment();

-- ---------- 팔로우 ----------
create or replace function public.notify_follow()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        insert into public.notifications (recipient_id, actor_id, type)
        values (new.followee_id, new.follower_id, 'follow');
    else
        delete from public.notifications
        where type = 'follow' and recipient_id = old.followee_id and actor_id = old.follower_id;
    end if;
    return null;
end;
$$;
create trigger follows_notify after insert or delete on follows
    for each row execute function public.notify_follow();

-- ---------- 방명록 (추모 페이지 개설자에게) ----------
create or replace function public.notify_guestbook()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
    if tg_op = 'INSERT' then
        insert into public.notifications (recipient_id, actor_id, type, memorial_id, source_id, preview)
        select m.creator_id, new.author_id, 'guestbook', m.id, new.id, left(new.message, 100)
        from public.memorials m where m.id = new.memorial_id and m.creator_id <> new.author_id;
    else
        delete from public.notifications where source_id = old.id;
    end if;
    return null;
end;
$$;
create trigger guestbook_entries_notify after insert or delete on guestbook_entries
    for each row execute function public.notify_guestbook();

-- ---------- 이미 있는 좋아요·댓글·팔로우·방명록도 알림으로 (모두 읽음 처리) ----------
insert into notifications (recipient_id, actor_id, type, post_id, created_at, read_at)
select p.author_id, l.user_id, 'post_like', p.id, l.created_at, now()
from post_likes l join posts p on p.id = l.post_id where p.author_id <> l.user_id;

insert into notifications (recipient_id, actor_id, type, post_id, source_id, preview, created_at, read_at)
select p.author_id, c.author_id, 'post_comment', p.id, c.id, left(c.body, 100), c.created_at, now()
from post_comments c join posts p on p.id = c.post_id where p.author_id <> c.author_id;

insert into notifications (recipient_id, actor_id, type, history_id, memorial_id, created_at, read_at)
select h.author_id, l.user_id, 'history_like', h.id, h.memorial_id, l.created_at, now()
from history_likes l join memorial_history h on h.id = l.history_id where h.author_id <> l.user_id;

insert into notifications (recipient_id, actor_id, type, history_id, memorial_id, source_id, preview, created_at, read_at)
select h.author_id, c.author_id, 'history_comment', h.id, h.memorial_id, c.id, left(c.body, 100), c.created_at, now()
from history_comments c join memorial_history h on h.id = c.history_id where h.author_id <> c.author_id;

insert into notifications (recipient_id, actor_id, type, created_at, read_at)
select f.followee_id, f.follower_id, 'follow', f.created_at, now() from follows f;

insert into notifications (recipient_id, actor_id, type, memorial_id, source_id, preview, created_at, read_at)
select m.creator_id, g.author_id, 'guestbook', m.id, g.id, left(g.message, 100), g.created_at, now()
from guestbook_entries g join memorials m on m.id = g.memorial_id where m.creator_id <> g.author_id;
