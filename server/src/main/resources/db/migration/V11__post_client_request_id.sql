-- 글 작성 멱등성: 앱이 작성 플로우마다 만든 UUID. 응답 유실 후 재시도해도 같은 글을 돌려준다.
alter table posts add column client_request_id uuid;
create unique index posts_author_client_request_uniq
    on posts (author_id, client_request_id)
    where client_request_id is not null;
