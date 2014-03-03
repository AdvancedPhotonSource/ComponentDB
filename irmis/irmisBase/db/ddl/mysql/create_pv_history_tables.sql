# Schema for holding inventory of historical pv's in ioc's
     

create table rec_history
  (
    rec_history_id int not null primary key auto_increment,
    ioc_boot_id int,
    rec_nm varchar(128) binary,
    rec_type_history_id int,
    rec_criticality int
  ) type=InnoDb;      

create table fld_history
  (
    fld_history_id int not null primary key auto_increment,
    rec_history_id int,
    fld_type_history_id int,
    fld_val varchar(128),
    ioc_resource_history_id int 
  ) type=InnoDb;      

create table ioc_resource_history
  (
    ioc_resource_history_id int not null primary key auto_increment,
    ioc_boot_id int,
    text_line varchar(255),
    load_order int,
    uri_history_id int,
    unreachable tinyint(1),
    subst_str varchar(255),
    ioc_resource_type_id int
  ) type=InnoDb; 
  
create table rec_type_history
  (
    rec_type_history_id int not null primary key auto_increment,
    ioc_boot_id int,
    rec_type varchar(24),
    ioc_resource_history_id int
  ) type=InnoDb;      

create table fld_type_history
  (
    fld_type_history_id int not null primary key auto_increment,
    rec_type_history_id int,
    fld_type varchar(24),
    dbd_type varchar(24),
    def_fld_val varchar(128)
  ) type=InnoDb;      
  
create table uri_history
  (
    uri_history_id int not null primary key auto_increment,
    uri varchar(255),
    uri_modified_date timestamp,
    modified_date timestamp,
    modified_by varchar(60)
  ) type=InnoDb;
  
create table rec_type_dev_sup_history
    (
      rec_type_dev_sup_history_id int not null primary key auto_increment,
      rec_type_history_id int,
      dtyp_str varchar(50),
      dev_sup_dset varchar(50),
      dev_sup_io_type varchar(50)
    ) type=InnoDb;
