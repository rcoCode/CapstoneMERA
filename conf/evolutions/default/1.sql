# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Medication (
  id                        bigserial not null,
  name                      varchar(255),
  per_wk                    bigint,
  per_mnth                  bigint,
  per_day                   bigint,
  constraint pk_Medication primary key (id))
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




# --- !Downs

drop table if exists Medication cascade;

drop table if exists Users cascade;

