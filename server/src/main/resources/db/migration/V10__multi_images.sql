-- 게시글·추억 글 이미지 여러 장. image_url(단일) → image_urls(순서 있는 배열, 최대 10장).
alter table posts add column image_urls text[] not null default '{}';
update posts set image_urls = array[image_url] where image_url is not null;
alter table posts drop column image_url;
alter table posts add constraint posts_image_urls_max check (cardinality(image_urls) <= 10);

alter table memorial_history add column image_urls text[] not null default '{}';
update memorial_history set image_urls = array[image_url] where image_url is not null;
alter table memorial_history drop column image_url;
alter table memorial_history add constraint memorial_history_image_urls_max check (cardinality(image_urls) <= 10);
