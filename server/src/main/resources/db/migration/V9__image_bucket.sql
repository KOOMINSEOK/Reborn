-- 사용자 업로드 이미지 버킷. 공개 읽기, 쓰기는 서버가 발급한 서명 URL 로만.
-- 크기·형식 제한을 버킷에 걸어 둔다 → 서명 URL 로 올려도 Storage 가 직접 거부한다.
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('images', 'images', true, 10485760, array['image/jpeg', 'image/png', 'image/webp'])
on conflict (id) do nothing;
