-- 회원가입 단계 (약관 → 본인인증 → 아이디 → 목적). 가입 도중 앱을 꺼도 다음에 이어서 하게 서버가 진행 상태를 갖는다.
-- 본인인증은 실명 확인 업체(PASS·NICE 등)가 정해지기 전까진 개발용 대역(IDENTITY_DEV)으로만 돈다.
-- 주민번호 뒷자리·생년월일 원문은 저장하지 않는다. 업체가 주는 연계정보(CI) 같은 값만 — 개발용은 해시.

alter table profiles
    add column terms_agreed_at      timestamptz,
    add column marketing_opt_in     boolean not null default false,
    add column identity_verified_at timestamptz,
    -- 한 사람당 계정 하나: 같은 사람이면 같은 값 (실서비스는 본인확인 업체의 CI)
    add column identity_ci          text unique,
    add column identity_name        text,
    add column signup_purpose       text check (signup_purpose in ('remember', 'record', 'browse')),
    add column onboarded_at         timestamptz;

-- 이미 있는 계정은 가입을 마친 것으로 (본인인증 없이 만들어진 테스트·시드 계정)
update profiles set onboarded_at = created_at where onboarded_at is null;

-- 본인인증 요청 (인증번호). 번호는 해시로만, 3분 유효, 5번 틀리면 끝.
create table identity_requests (
    id          uuid        primary key default gen_random_uuid(),
    user_id     uuid        not null references profiles (id) on delete cascade,
    ci          text        not null,
    name        text        not null,
    code_hash   text        not null,
    attempts    int         not null default 0,
    expires_at  timestamptz not null,
    verified_at timestamptz,
    created_at  timestamptz not null default now()
);
create index identity_requests_user_idx on identity_requests (user_id, created_at desc);
