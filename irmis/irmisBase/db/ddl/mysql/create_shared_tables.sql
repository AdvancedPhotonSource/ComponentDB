# Tables used by more than one sub-schema defined here.

# Uri table stores references to files on the file system.
#
# Columns:
#   uri - contains file system path encoded as uri
#           (ie. file://host/path/to/file.txt )
#   uri_modified_date - the mtime attribute of the file
#   modified_date - time of row insert
#   modified_by - username or app name who performed insert

create table uri
  (
    uri_id int not null primary key auto_increment,
    uri varchar(255),
    uri_modified_date timestamp,
    modified_date timestamp,
    modified_by varchar(60)
  ) type=InnoDb;

create table person
  (
    person_id int not null primary key auto_increment,
    first_nm varchar(30),
    middle_nm varchar(30),
    last_nm varchar(30),
    userid varchar(30)
  ) type=InnoDb;

create table role
  (
    role_id int not null primary key auto_increment,
    person_id int,
    role_name_id int
  ) type=InnoDb;

create table person_group
  (
    person_group_id int not null primary key auto_increment,
    person_id int,
    group_name_id int
  ) type=InnoDb;

create table audit_action
  (
    audit_action_id int not null primary key auto_increment,
    audit_action_type_id int not null,
    action_key int,
    action_desc varchar(100),
    person_id int,
    action_date timestamp
  ) type=InnoDb;




