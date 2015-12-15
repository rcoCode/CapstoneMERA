# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contact (
  id                        bigserial not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  email                     varchar(255),
  phone                     varchar(255),
  constraint pk_contact primary key (id))
;

create table containers (
  id                        bigserial not null,
  device_id                 bigint,
  empty                     boolean,
  medication_id             bigint,
  pill_count                bigint,
  constraint uq_containers_medication_id unique (medication_id),
  constraint pk_containers primary key (id))
;

create table dispensor (
  id                        bigserial not null,
  start_time                timestamp,
  end_time                  timestamp,
  owner_id                  bigint,
  constraint uq_dispensor_owner_id unique (owner_id),
  constraint pk_dispensor primary key (id))
;

create table meds (
  id                        bigserial not null,
  name                      varchar(255),
  dose                      bigint,
  schedule                  timestamp,
  per_wk                    bigint,
  per_mnth                  bigint,
  per_day                   bigint,
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
  device_id                 bigint,
  constraint uq_Users_username unique (username),
  constraint uq_Users_device_id unique (device_id),
  constraint pk_Users primary key (id))
;


create table Users_contact (
  Users_id                       bigint not null,
  contact_id                     bigint not null,
  constraint pk_Users_contact primary key (Users_id, contact_id))
;
alter table containers add constraint fk_containers_device_1 foreign key (device_id) references dispensor (id);
create index ix_containers_device_1 on containers (device_id);
alter table containers add constraint fk_containers_medication_2 foreign key (medication_id) references meds (id);
create index ix_containers_medication_2 on containers (medication_id);
alter table dispensor add constraint fk_dispensor_owner_3 foreign key (owner_id) references Users (id);
create index ix_dispensor_owner_3 on dispensor (owner_id);
alter table meds add constraint fk_meds_storedIn_4 foreign key (stored_in_id) references containers (id);
create index ix_meds_storedIn_4 on meds (stored_in_id);
alter table Users add constraint fk_Users_device_5 foreign key (device_id) references dispensor (id);
create index ix_Users_device_5 on Users (device_id);



alter table Users_contact add constraint fk_Users_contact_Users_01 foreign key (Users_id) references Users (id);

alter table Users_contact add constraint fk_Users_contact_contact_02 foreign key (contact_id) references contact (id);

# --- !Downs

drop table if exists contact cascade;

drop table if exists Users_contact cascade;

drop table if exists containers cascade;

drop table if exists dispensor cascade;

drop table if exists meds cascade;

drop table if exists Users cascade;

