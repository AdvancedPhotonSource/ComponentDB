-- Creates enum (lookup) tables for component schema.

create table component_rel_type
  (
    component_rel_type_id int not null primary key,
    rel_name varchar(30)
  );

insert into component_rel_type values (1, 'control');
insert into component_rel_type values (2, 'housing');
insert into component_rel_type values (3, 'power');

drop sequence seq_component_rel_type;
create sequence seq_component_rel_type start with 4;

create table port_pin_type
  (
    port_pin_type_id int not null primary key,
    port_pin_type varchar(12)
  );

insert into port_pin_type values(1, 'IN');
insert into port_pin_type values(2, 'OUT');
insert into port_pin_type values(3, 'BIDIR');
insert into port_pin_type values(4, 'PWR');
insert into port_pin_type values(5, 'GND');
insert into port_pin_type values(6, 'COM');

drop sequence seq_port_pin_type;
create sequence seq_port_pin_type start with 7;
