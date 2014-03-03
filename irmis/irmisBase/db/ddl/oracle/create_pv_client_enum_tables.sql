-- Creates enum (lookup) tables for pv client schema. 
-- NOTE: as client crawlers are created, you must add
-- a unique row here to identify your client.

create table rec_client_type
  (
    rec_client_type_id int not null primary key,
    description varchar(100)
  );

insert into rec_client_type values (1, 'MEDM');
insert into rec_client_type values (2, 'Alarm Handler');
insert into rec_client_type values (3, 'Save/Restore');
insert into rec_client_type values (4, 'Sequencer');
insert into rec_client_type values (5, 'sddslogger');
insert into rec_client_type values (6, 'PEM');
insert into rec_client_type values (7, 'IOC Save/Restore');
insert into rec_client_type values (8, 'CA Security');

drop sequence seq_rec_client_type;
create sequence seq_rec_client_type start with 9;
