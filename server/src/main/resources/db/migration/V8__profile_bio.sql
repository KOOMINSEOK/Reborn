-- 프로필 한 줄 소개. (handle / display_name / avatar_url / is_private 는 이미 있음)
alter table profiles add column bio text;
