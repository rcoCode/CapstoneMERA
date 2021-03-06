# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contact (
  id                        bigserial not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  email                     varchar(255),
  phone                     varchar(255),
  constraint uq_contact_email unique (email),
  constraint pk_contact primary key (id))
;

create table containers (
  id                        bigserial not null,
  device_id                 bigint,
  empty                     boolean,
  owner_id                  bigint,
  pill_count                bigint,
  container                 bigint,
  constraint uq_containers_container unique (container),
  constraint pk_containers primary key (id))
;

create table dispensor (
  id                        bigserial not null,
  dispenser                 bigint,
  owner_id                  bigint,
  start_time                timestamp,
  end_time                  timestamp,
  constraint uq_dispensor_owner_id unique (owner_id),
  constraint pk_dispensor primary key (id))
;

create table log (
  id                        bigserial not null,
  schedule_time             timestamp,
  logged_time               timestamp,
  status_type               varchar(255),
  message                   varchar(255),
  regards_id                bigint,
  own_id                    bigint,
  constraint pk_log primary key (id))
;

create table meds (
  id                        bigserial not null,
  name                      varchar(255),
  dose                      bigint,
  schedule                  timestamp,
  daily_time                timestamp,
  frequency                 bigint,
  updated                   boolean,
  stored_in_id              bigint,
  constraint uq_meds_stored_in_id unique (stored_in_id),
  constraint pk_meds primary key (id))
;

create table Users (
  id                        bigserial not null,
  username                  varchar(255),
  password_hash             varchar(255),
  fname                     varchar(255),
  lname                     varchar(255),
  constraint uq_Users_username unique (username),
  constraint pk_Users primary key (id))
;


create table contact_Users (
  contact_id                     bigint not null,
  Users_id                       bigint not null,
  constraint pk_contact_Users primary key (contact_id, Users_id))
;
alter table containers add constraint fk_containers_device_1 foreign key (device_id) references dispensor (id);
create index ix_containers_device_1 on containers (device_id);
alter table containers add constraint fk_containers_owner_2 foreign key (owner_id) references Users (id);
create index ix_containers_owner_2 on containers (owner_id);
alter table dispensor add constraint fk_dispensor_owner_3 foreign key (owner_id) references Users (id);
create index ix_dispensor_owner_3 on dispensor (owner_id);
alter table log add constraint fk_log_regards_4 foreign key (regards_id) references containers (id);
create index ix_log_regards_4 on log (regards_id);
alter table log add constraint fk_log_own_5 foreign key (own_id) references Users (id);
create index ix_log_own_5 on log (own_id);
alter table meds add constraint fk_meds_storedIn_6 foreign key (stored_in_id) references containers (id);
create index ix_meds_storedIn_6 on meds (stored_in_id);



alter table contact_Users add constraint fk_contact_Users_contact_01 foreign key (contact_id) references contact (id);

alter table contact_Users add constraint fk_contact_Users_Users_02 foreign key (Users_id) references Users (id);

# --- !Downs

drop table if exists contact cascade;

drop table if exists contact_Users cascade;

drop table if exists containers cascade;

drop table if exists dispensor cascade;

drop table if exists log cascade;

drop table if exists meds cascade;

drop table if exists Users cascade;

