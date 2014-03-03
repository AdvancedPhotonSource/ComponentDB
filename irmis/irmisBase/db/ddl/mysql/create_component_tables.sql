#
# Schema for components
# 

create table component_semaphore
  (
    component_semaphore_id int not null,
    semaphore int not null,
    userid varchar(30),
    modified_date bigint
  ) type=InnoDb;
insert into component_semaphore values (1, 1, 'initial', 0);

create table component
  (
    component_id int not null primary key auto_increment,
    component_type_id int not null,
    component_instance_name varchar(60),
    image_uri varchar(255),
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;

# APS-specific table here for now. May be factored out of general application later.
create table aps_component
  (
    aps_component_id int not null primary key auto_increment,
    component_id int not null,
    serial_number varchar(60),
    group_name_id int,
    verified tinyint(1), 
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;

create table component_rel
  (
    component_rel_id int not null primary key auto_increment,
    parent_component_id int,
    child_component_id int,
    logical_order int,
    logical_desc varchar(60),
    component_rel_type_id int,
    verified_person_id int, 
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;

create table component_type
  (
    component_type_id int not null primary key auto_increment,
    component_type_name varchar(60),
    description varchar(100),
    vdescription text,
    chc_beamline_interest_id int,
    chc_contact_id int,
    form_factor_id int,
    mfg_id int,
    mark_for_delete tinyint(1),
    version int,
    unique (component_type_name, mark_for_delete)
  ) type=InnoDb;


create table base_component_type
  (
    base_component_type_id int not null primary key auto_increment,
    component_type_name varchar(60),
    unique (component_type_name)
  ) type=InnoDb;    

create table mfg
  (
    mfg_id int not null primary key auto_increment,
    mfg_name varchar(100),
    version int,
    mark_for_delete tinyint(1),
    unique (mfg_name, mark_for_delete)
  ) type=InnoDb;


create table form_factor
  (
    form_factor_id int not null primary key auto_increment,
    form_factor varchar(100),
    mark_for_delete tinyint(1),
    version int,
    unique (form_factor, mark_for_delete)
  ) type=InnoDb;


create table component_type_person
  (
    component_type_person_id int not null primary key auto_increment,
    component_type_id int,
    person_id int,
    role_name_id int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table component_type_document
  (
    component_type_document_id int not null primary key auto_increment,
    component_type_id int,
    document_type varchar(100),
    uri_id int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table component_type_status
  (
    component_type_status_id int not null primary key auto_increment,
    component_type_id int,
    spare_qty int,
    stock_qty int,
    spare_loc varchar(100),
    instantiated tinyint(1),
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table component_type_function
  (
    component_type_function_id int not null primary key auto_increment,
    component_type_id int,
    function_id int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table function
  (
    function_id int not null primary key auto_increment,
    function varchar(100),
    version int,
    mark_for_delete tinyint(1),
    unique (function, mark_for_delete)
  ) type=InnoDb;


create table component_type_if
  (
    component_type_if_id int not null primary key auto_increment,
    component_type_id int,
    required tinyint(1),
    presented tinyint(1),
    max_children int,
    component_rel_type_id int,
    component_type_if_type_id int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table component_type_if_type
  (
    component_type_if_type_id int not null primary key auto_increment,
    component_rel_type_id int,
    if_type varchar(100),
    mark_for_delete tinyint(1),
    version int,
    unique (if_type, component_rel_type_id, mark_for_delete)
  ) type=InnoDb;


create table component_port
  (
    component_port_id int not null primary key auto_increment,
    component_port_type_id int,
    component_id int,
    component_port_name varchar(40),
    component_port_order int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table component_port_type
  (
    component_port_type_id int not null primary key auto_increment,
    component_port_type varchar(60),
    component_port_group varchar(60),
    component_port_pin_count int,
    modified_date timestamp,
    modified_by varchar(60),
    mark_for_delete tinyint(1),
    version int,
    unique (component_port_type)
  ) type=InnoDb;


create table port_pin
  (
    port_pin_id int not null primary key auto_increment,
    port_pin_type_id int,
    port_pin_usage varchar(60),
    signal_name varchar(60),
    component_port_id int,
    port_pin_designator_id int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table port_pin_designator
  (
    port_pin_designator_id int not null primary key auto_increment,
    component_port_type_id int,
    designator_order int,
    designator varchar(60),
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table conductor
  (
    conductor_id int not null primary key auto_increment,
    cable_id int,
    port_pin_a_id int,
    port_pin_b_id int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table cable
  (
    cable_id int not null primary key auto_increment,
    color varchar(60),
    label varchar(60),
    component_port_a_id int,
    component_port_b_id int,
    pin_detail tinyint(1),
    virtual tinyint(1),
    dest_desc varchar(60),
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


# component type template tables

create table component_port_template
  (
    component_port_template_id int not null primary key auto_increment,
    component_type_id int,
    component_port_type_id int,
    component_port_name varchar(40), 
    component_port_order int,
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;


create table port_pin_template
  (
    port_pin_template_id int not null primary key auto_increment,
    component_port_template_id int,
    port_pin_type_id int,
    port_pin_designator_id int,
    port_pin_usage varchar(60),
    mark_for_delete tinyint(1),
    version int
  ) type=InnoDb;





