

drop table if exists cable_type;
create table cable_type
  (
    cable_type_id int not null primary key auto_increment,
    cable_type varchar(50),
    cable_type_description varchar(255),
    cable_diameter float(9,3)
  ) type=InnoDb;
