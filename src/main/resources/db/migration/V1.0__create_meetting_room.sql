create table "meeting_room"
(
    id serial not null,
    name varchar not null
);

create unique index meeting_room_id_uindex
    on "meeting_room" (id);

alter table "meeting_room"
    add constraint meeting_room_pk
        primary key (id);