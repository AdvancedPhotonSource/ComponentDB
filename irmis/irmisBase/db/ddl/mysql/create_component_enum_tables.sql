# Creates enum (lookup) tables for component schema.

create table component_rel_type
  (
    component_rel_type_id int not null primary key,
    rel_name varchar(30)
  ) type=InnoDb;

insert into component_rel_type values (1, 'control');
insert into component_rel_type values (2, 'housing');
insert into component_rel_type values (3, 'power');

create table port_pin_type
  (
    port_pin_type_id int not null primary key,
    port_pin_type varchar(12)
  ) type=InnoDb;

insert into port_pin_type values(1, 'IN');
insert into port_pin_type values(2, 'OUT');
insert into port_pin_type values(3, 'BIDIR');
insert into port_pin_type values(4, 'PWR');
insert into port_pin_type values(5, 'GND');
insert into port_pin_type values(6, 'COM');

create table chc_beamline_interest
  (
    chc_beamline_interest_id int not null primary key,
    interest varchar(30)
  ) type=InnoDb;

insert into chc_beamline_interest values (1, 'unknown');
insert into chc_beamline_interest values (2, 'supported');
insert into chc_beamline_interest values (3, 'preferred');
insert into chc_beamline_interest values (4, 'obsolete');

