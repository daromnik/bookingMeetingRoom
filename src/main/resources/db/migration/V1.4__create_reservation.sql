create table reservation
(
    id serial not null,
    user_id int not null
        constraint reservation_user_id_fk
        references users
        on delete cascade,
    meeting_room_id int not null
        constraint reservation_meeting_room_id_fk
        references meeting_room
        on delete cascade,
    descriptions varchar,
    date_start timestamp not null,
    date_finish timestamp not null
);

create unique index reservation_id_uindex
    on reservation (id);

alter table reservation
    add constraint reservation_pk
        primary key (id);

