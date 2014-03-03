#
# Schema for component history
# 

create table component_instance
  (
    component_instance_id int not null primary key auto_increment,
    component_id int,
    component_type_id int not null,
    serial_number varchar(60),
    version int
  ) type=InnoDb;

create table component_instance_state
  (
    component_instance_state_id int not null primary key auto_increment,
    component_instance_id int not null,
    person_id int not null,
    entered_date timestamp not null,
    component_state_id int not null,
    comment varchar(255),
    reference_data_1 varchar(255),
    reference_data_2 timestamp null,
    version int
  ) type=InnoDb;

create table component_state
  (
    component_state_id int not null primary key auto_increment,
    component_state_category_id int not null,
    state varchar(60) not null
  ) type=InnoDb;

create table component_state_category
  (
    component_state_category_id int not null primary key auto_increment,
    category varchar(60) not null
  ) type=InnoDb;


