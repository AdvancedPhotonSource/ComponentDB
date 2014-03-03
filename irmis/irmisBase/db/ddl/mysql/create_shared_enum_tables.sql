# Creates enum (lookup) tables for shared schema. 
# Note that most of these are not populated here. The actual
# population of many enum tables is site-specific, and so
# must be done locally, not in irmisBase distribution.

create table role_name
  (
    role_name_id int not null primary key,
    role_name varchar(60),
    unique(role_name)
  ) type=InnoDb;
 
insert into role_name values (1, "irmis:admin");
insert into role_name values (2, "irmis:ioc-editor");
insert into role_name values (3, "irmis:component-type-editor");
insert into role_name values (4, "irmis:component-editor");
insert into role_name values (5, "irmis:cable-editor");
insert into role_name values (6, "irmis:component-type-port-editor");
insert into role_name values (50, "cognitive person");

create table group_name
  (
    group_name_id int not null primary key auto_increment,
    group_name varchar(100),
    unique(group_name)
  ) type=InnoDb;

insert into group_name values(1, "Controls");
insert into group_name values(2, "No Group");
insert into group_name values(3, "Unknown");

create table technical_system
  (
    technical_system_id int not null primary key auto_increment,
    technical_system varchar(60),
    description varchar(255),
    unique(technical_system)
  ) type=InnoDb;

create table machine
  (
    machine_id int not null primary key auto_increment,
    machine varchar(60),
    description varchar(255),
    unique(machine)
  ) type=InnoDb;

create table audit_action_type
  (
    audit_action_type_id int not null primary key,
    action_name varchar(60),
    unique(action_name)
  ) type=InnoDb;

insert into audit_action_type values (1, 'add component to hierarchy');
insert into audit_action_type values (2, 'modify component logical order');
insert into audit_action_type values (3, 'modify component logical description');
insert into audit_action_type values (4, 'delete component from hierarchy');
insert into audit_action_type values (5, 'add component type');
insert into audit_action_type values (6, 'edit component type');
insert into audit_action_type values (7, 'login successful');
insert into audit_action_type values (8, 'login failed');
insert into audit_action_type values (9, 'logout');
insert into audit_action_type values (10, 'add cable');
insert into audit_action_type values (11, 'remove cable');
insert into audit_action_type values (12, 'edit cable');
insert into audit_action_type values (13, 'modify component name');
insert into audit_action_type values (14, 'modify component parent');
insert into audit_action_type values (15, 'modify component type');
insert into audit_action_type values (16, 'modify component serial number');
insert into audit_action_type values (17, 'modify component group ownership');
insert into audit_action_type values (18, 'modify component verified flag');
insert into audit_action_type values (19, 'modify component image url');

