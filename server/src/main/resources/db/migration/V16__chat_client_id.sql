-- 보내기를 다시 시도해도 한 번만 저장되게: 앱이 메시지마다 만든 id. 같은 사람이 같은 id 로 또 보내면 처음 것을 돌려준다.
alter table messages add column client_id uuid;
alter table messages add constraint messages_sender_client_unique unique (sender_id, client_id);
