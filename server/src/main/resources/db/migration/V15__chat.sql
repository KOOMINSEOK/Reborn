-- 1:1 메시지. 두 사람 사이 대화방은 하나 (user_a < user_b 로 순서를 정해 중복 없이).
-- 사람마다: 어디까지 읽었나(*_read_at), 대화방을 지웠나(*_hidden_before — 그 시각까지의 메시지는 그 사람에게 안 보임,
-- 새 메시지가 오면 목록에 다시 나타난다).

create table conversations (
    id               uuid        primary key default gen_random_uuid(),
    user_a           uuid        not null references profiles (id) on delete cascade,
    user_b           uuid        not null references profiles (id) on delete cascade,
    created_at       timestamptz not null default now(),
    last_message_at  timestamptz,
    a_read_at        timestamptz,
    b_read_at        timestamptz,
    a_hidden_before  timestamptz,
    b_hidden_before  timestamptz,
    unique (user_a, user_b),
    check (user_a < user_b)
);
create index conversations_user_b_idx on conversations (user_b);

create table messages (
    id              uuid        primary key default gen_random_uuid(),
    conversation_id uuid        not null references conversations (id) on delete cascade,
    sender_id       uuid        not null references profiles (id) on delete cascade,
    body            text        not null check (char_length(body) between 1 and 2000),
    created_at      timestamptz not null default now()
);
create index messages_conversation_idx on messages (conversation_id, created_at desc, id desc);
