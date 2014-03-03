
create table plc
  (
    plc_id int not null primary key auto_increment,
    component_id int,
    plc_description text,
    plc_version_pv_name text
  ) type=InnoDb;


