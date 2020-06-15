create table "users"
(
    id serial not null,
    username varchar not null,
    password varchar not null,
    enabled boolean default true not null
);

create unique index user_id_uindex
    on "users" (id);

create unique index users_username_uindex
    on "users" (username);

alter table "users"
    add constraint user_pk
        primary key (id);







