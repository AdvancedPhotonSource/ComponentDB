-- Alters shared schema, adding indexes and foreign key constraints.

-- Indexes 
create index idx_person_id_r on role (person_id);
create index idx_role_name_id_r on role (role_name_id);

create index idx_person_id_p on person_group (person_id);
create index idx_group_name_id_pg on person_group(group_name_id);

create index idx_audit_action_type_id_aa on audit_action (audit_action_type_id);
create index idx_person_id_aa on audit_action (person_id);

-- Foreign key constraints
alter table role add constraint fk_person_id_r foreign key (person_id) references person (person_id);
alter table role add constraint fk_role_name_id_r foreign key (role_name_id) references role_name (role_name_id);

alter table person_group add constraint fk_person_id_pg foreign key (person_id) references person (person_id);
alter table person_group add constraint fk_group_name_id_pg foreign key (group_name_id) references group_name (group_name_id);

alter table audit_action add constraint fk_audit_action_type_id_aa foreign key (audit_action_type_id) references audit_action_type (audit_action_type_id);
alter table audit_action add constraint fk_person_id_aa foreign key (person_id) references person (person_id);

