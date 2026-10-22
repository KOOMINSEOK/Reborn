-- 검색이 데이터가 늘어도 느려지지 않게.
-- 사람별 게시물 좋아요 합 (그리고 프로필 글 목록) — 글쓴이로 바로 찾기. (memorial_id 를 지울 때 예전 색인도 같이 사라졌다)
create index posts_author_published_idx on posts (author_id, created_at desc) where status = 'published';

-- 이름 일부("지연")로 찾는 ilike '%..%' 는 보통 색인을 못 쓴다 → 글자 조각(trigram) 색인
create extension if not exists pg_trgm;
create index profiles_handle_trgm_idx on profiles using gin (handle gin_trgm_ops);
create index profiles_display_name_trgm_idx on profiles using gin (display_name gin_trgm_ops);
create index memorials_name_trgm_idx on memorials using gin (name gin_trgm_ops);
create index memorials_handle_trgm_idx on memorials using gin (handle gin_trgm_ops);
