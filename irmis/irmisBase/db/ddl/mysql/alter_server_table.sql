
alter table server add index idx_server_id_component (component_id);
 
alter table server add foreign key fk_server_id_component (component_id) references component (component_id);
alter table server add foreign key fk_server_cog_id_person (cognizant_id) references person (person_id);
