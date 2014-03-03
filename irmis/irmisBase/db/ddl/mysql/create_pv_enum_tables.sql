# Creates enum (lookup) tables for pv schema. Script is MySQL-specific.
# NOTE: this need only be done once. These records should not change much.

create table ioc_resource_type
  (
    ioc_resource_type_id int not null primary key,
    ioc_resource_type varchar(40)
  ) type=InnoDb;

insert into ioc_resource_type values (1, 'db');
insert into ioc_resource_type values (2, 'template');
insert into ioc_resource_type values (3, 'dbd');

create table ioc_error_message
  (
    ioc_error_num int not null primary key ,
    ioc_error_message varchar(250)
  ) type=InnoDb;

insert into ioc_error_message values (1, 'dbd unreachable');
insert into ioc_error_message values (2, 'general startup command parse error');
insert into ioc_error_message values (3, 'possible ioc boot problem');

create table dev_sup_io_type
  (
    dev_sup_io_type_id int not null primary key,
    io_type varchar(20) 
  ) type=InnoDb;

insert into dev_sup_io_type values (1,'BITBUS_IO');
insert into dev_sup_io_type values (2,'VME_IO');

