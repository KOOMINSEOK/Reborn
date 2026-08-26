-- profiles 를 Supabase 인증(auth.users)에 연결하고, 가입 시 프로필을 자동 생성한다.

alter table profiles
    add constraint profiles_id_fkey
    foreign key (id) references auth.users (id) on delete cascade;

-- 신규 가입 시 임시 프로필 생성. handle 은 uuid 기반이라 유니크가 보장된다.
-- ponytail: 임시 handle = user_<uuid>. 온보딩에서 사용자가 직접 바꾸게 한다.
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
    insert into public.profiles (id, handle, display_name)
    values (
        new.id,
        'user_' || replace(new.id::text, '-', ''),
        coalesce(
            new.raw_user_meta_data ->> 'name',
            new.raw_user_meta_data ->> 'full_name',
            new.email,
            'Reborn 사용자'
        )
    )
    on conflict (id) do nothing;
    return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
    after insert on auth.users
    for each row execute function public.handle_new_user();
