
create index idx_ioc_id_ai on aps_ioc (ioc_id);

alter table aps_ioc add constraint fk_ioc_id_ai foreign key (ioc_id) references ioc (ioc_id);
alter table aps_ioc add constraint fk_cog_developer_id_ai foreign key (cog_developer_id) references person (person_id);
alter table aps_ioc add constraint fk_cog_technician_id_ai foreign key (cog_technician_id) references person (person_id);

