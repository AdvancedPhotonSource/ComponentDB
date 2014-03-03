-- Creates enum (lookup) tables for pv schema.
-- NOTE: this need only be done once. These records should not change much.

create table ioc_resource_type
  (
    ioc_resource_type_id int not null primary key,
    ioc_resource_type varchar(40)
  );

insert into ioc_resource_type values (1, 'db');
insert into ioc_resource_type values (2, 'template');
insert into ioc_resource_type values (3, 'dbd');

drop sequence seq_ioc_resource_type;
create sequence seq_ioc_resource_type start with 4;

create table ioc_error_message
  (
    ioc_error_num int not null primary key ,
    ioc_error_message varchar(250)
  );

insert into ioc_error_message values (1, 'dbd unreachable');
insert into ioc_error_message values (2, 'general startup command parse error');
insert into ioc_error_message values (3, 'possible ioc boot problem');

drop sequence seq_ioc_error_message;
create sequence seq_ioc_error_message start with 4;
