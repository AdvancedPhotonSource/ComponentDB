# Alters shared schema, adding indexes and foreign key constraints.

# Indexes 
alter table role add index idx_person_id_r (person_id);
alter table role add index idx_role_name_id_r (role_name_id);

alter table person_group add index idx_person_id_p (person_id);
alter table person_group add index idx_group_name_id_pg (group_name_id);

alter table audit_action add index idx_audit_action_type_id_aa (audit_action_type_id);
alter table audit_action add index idx_person_id_aa (person_id);

# Foreign key constraints
alter table role add foreign key fk_person_id_r (person_id) references person (person_id);
alter table role add foreign key fk_role_name_id_r (role_name_id) references role_name (role_name_id);

alter table person_group add foreign key fk_person_id_pg (person_id) references person (person_id);
alter table person_group add foreign key fk_group_name_id_pg (group_name_id) references group_name (group_name_id);

alter table audit_action add foreign key fk_audit_action_type_id_aa (audit_action_type_id) references audit_action_type (audit_action_type_id);
alter table audit_action add foreign key fk_person_id_aa (person_id) references person (person_id);

