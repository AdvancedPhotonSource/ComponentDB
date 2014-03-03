drop table if exists server;
create table server
  (
    server_id int not null primary key auto_increment,
    component_id int,
    server_description varchar(255),
    cognizant_id int,
    operating_system varchar(255)
  ) type=InnoDb;


