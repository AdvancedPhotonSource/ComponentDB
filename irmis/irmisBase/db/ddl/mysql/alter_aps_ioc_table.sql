
alter table aps_ioc add index idx_ioc_id_ai (ioc_id);
 
alter table aps_ioc add foreign key fk_ioc_id_ai (ioc_id) references ioc (ioc_id);
alter table aps_ioc add foreign key fk_cog_developer_id_ai (cog_developer_id) references person (person_id);
alter table aps_ioc add foreign key fk_cog_technician_id_ai (cog_technician_id) references person (person_id);

