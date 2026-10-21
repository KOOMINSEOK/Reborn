-- 가입 그만두기(빈 계정 삭제)와 가입 마무리가 동시에 일어나도 둘 중 하나만 되게.
-- 그만두기는 먼저 deleting_at 을 찍고(가입을 마치지 않은 계정만, 한 문장), 마무리는 deleting_at 이 없을 때만 된다.
alter table profiles add column deleting_at timestamptz;
