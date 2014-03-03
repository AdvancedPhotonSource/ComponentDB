-- Schema for system components and cables.
-- 

create table component_semaphore
  (
    component_semaphore_id int not null,
    semaphore int not null,
    userid varchar(30),
    modified_date int
  );
insert into component_semaphore values (1, 1, 'initial', 0);

create table component
  (
    component_id int not null primary key,
    component_type_id int not null,
    component_instance_name varchar(60),
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component;
create sequence seq_component start with 10000;

-- APS-specific table here for now. May be factored out of general application later.
create table aps_component
  (
    aps_component_id int not null primary key,
    component_id int not null,
    serial_number varchar(60),
    group_name_id int,
    verified smallint, 
    mark_for_delete smallint,
    version int
  );

drop sequence seq_aps_component;
create sequence seq_aps_component start with 10000;

create table component_rel
  (
    component_rel_id int not null primary key,
    parent_component_id int,
    child_component_id int,
    logical_order int,
    logical_desc varchar(60),
    component_rel_type_id int,
    verified_person_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_rel;
create sequence seq_component_rel start with 10000;

create table component_type
  (
    component_type_id int not null primary key,
    component_type_name varchar(60),
    description varchar(100),
    form_factor_id int,
    mfg_id int,
    mark_for_delete smallint,
    version int,
    unique (component_type_name, mark_for_delete)
  );

drop sequence seq_component_type;
create sequence seq_component_type start with 10000;

create table base_component_type
  (
    base_component_type_id int not null primary key,
    component_type_name varchar(60),
    unique (component_type_name)
  );    

drop sequence seq_base_component_type;
create sequence seq_base_component_type start with 10000;

create table mfg
  (
    mfg_id int not null primary key,
    mfg_name varchar(100),
    version int,
    mark_for_delete smallint,
    unique (mfg_name, mark_for_delete)
  );

drop sequence seq_mfg;
create sequence seq_mfg start with 10000;

create table form_factor
  (
    form_factor_id int not null primary key,
    form_factor varchar(100),
    mark_for_delete smallint,
    version int,
    unique (form_factor, mark_for_delete)
  );

drop sequence seq_form_factor;
create sequence seq_form_factor start with 10000;

create table component_type_person
  (
    component_type_person_id int not null primary key,
    component_type_id int,
    person_id int,
    role_name_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_type_person;
create sequence seq_component_type_person start with 10000;

create table component_type_document
  (
    component_type_document_id int not null primary key,
    component_type_id int,
    document_type varchar(100),
    uri_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_type_document;
create sequence seq_component_type_document start with 10000;

create table component_type_status
  (
    component_type_status_id int not null primary key,
    component_type_id int,
    spare_qty int,
    stock_qty int,
    spare_loc varchar(100),
    instantiated smallint,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_type_status;
create sequence seq_component_type_status start with 10000;

create table component_type_function
  (
    component_type_function_id int not null primary key,
    component_type_id int,
    function_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_type_function;
create sequence seq_component_type_function start with 10000;

create table function
  (
    function_id int not null primary key,
    function varchar(100),
    version int,
    mark_for_delete smallint,
    unique (function, mark_for_delete)
  );

drop sequence seq_function;
create sequence seq_function start with 10000;

create table component_type_if
  (
    component_type_if_id int not null primary key,
    component_type_id int,
    required smallint,
    presented smallint,
    max_children int,
    component_rel_type_id int,
    component_type_if_type_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_type_if;
create sequence seq_component_type_if start with 10000;

create table component_type_if_type
  (
    component_type_if_type_id int not null primary key,
    component_rel_type_id int,
    if_type varchar(100),
    mark_for_delete smallint,
    version int,
    unique (if_type, component_rel_type_id, mark_for_delete)
  );

drop sequence seq_component_type_if_type;
create sequence seq_component_type_if_type start with 10000;

create table component_port
  (
    component_port_id int not null primary key,
    component_port_type_id int,
    component_id int,
    component_port_name varchar(40),
    component_port_order int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_port;
create sequence seq_component_port start with 10000;

create table component_port_type
  (
    component_port_type_id int not null primary key,
    component_port_type varchar(60),
    component_port_group varchar(60),
    component_port_pin_count int,
    modified_date timestamp,
    modified_by varchar(60),
    mark_for_delete smallint,
    version int,
    unique (component_port_type)
  );

drop sequence seq_component_port_type;
create sequence seq_component_port_type start with 10000;

create table port_pin
  (
    port_pin_id int not null primary key,
    port_pin_type_id int,
    port_pin_usage varchar(60),
    signal_name varchar(60),
    component_port_id int,
    port_pin_designator_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_port_pin;
create sequence seq_port_pin start with 10000;

create table port_pin_designator
  (
    port_pin_designator_id int not null primary key,
    component_port_type_id int,
    designator_order int,
    designator varchar(60),
    mark_for_delete smallint,
    version int
  );

drop sequence seq_port_pin_designator;
create sequence seq_port_pin_designator start with 10000;

create table conductor
  (
    conductor_id int not null primary key,
    cable_id int,
    port_pin_a_id int,
    port_pin_b_id int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_conductor;
create sequence seq_conductor start with 10000;

create table cable
  (
    cable_id int not null primary key,
    color varchar(40),
    label varchar(40),
    component_port_a_id int,
    component_port_b_id int,
    pin_detail smallint,
    virtual smallint,
    dest_desc varchar(60),
    mark_for_delete smallint,
    version int
  );

drop sequence seq_cable;
create sequence seq_cable start with 10000;

-- component type template tables

create table component_port_template
  (
    component_port_template_id int not null primary key,
    component_type_id int,
    component_port_type_id int,
    component_port_name varchar(40), 
    component_port_order int,
    mark_for_delete smallint,
    version int
  );

drop sequence seq_component_port_template;
create sequence seq_component_port_template start with 10000;

create table port_pin_template
  (
    port_pin_template_id int not null primary key,
    component_port_template_id int,
    port_pin_type_id int,
    port_pin_designator_id int,
    port_pin_usage varchar(60),
    mark_for_delete smallint,
    version int
  );

drop sequence seq_port_pin_template;
create sequence seq_port_pin_template start with 10000;






