# Schema for holding inventory of PV alias names
     

create table rec_alias
  (
    rec_alias_id int not null primary key auto_increment,
    rec_id int,
    alias_nm varchar(128),
    ioc_resource_id int
  ) type=InnoDb;      

create table rec_alias_history
  (
    rec_alias_history_id int not null primary key auto_increment,
    rec_history_id int,
    alias_nm varchar(128),
    ioc_resource_history_id int
  ) type=InnoDb;      

