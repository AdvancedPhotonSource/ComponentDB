# Alters component history schema, adding indexes and foreign key constraints.

# Indexes 
alter table component_instance add index idx_component_id_ci (component_id);
alter table component_instance add index idx_component_type_id_ci (component_type_id);
alter table component_instance add index idx_serial_number_ci (serial_number);

alter table component_instance_state add index idx_component_instance_id_cis (component_instance_id);
alter table component_instance_state add index idx_component_state_id_cis (component_state_id);

alter table component_state add index idx_component_state_category_id_cs (component_state_category_id);


# Foreign key constraints
alter table component_instance add foreign key fk_component_id_ci (component_id) references component (component_id);
alter table component_instance add foreign key fk_component_type_id_ci (component_type_id) references component_type (component_type_id);

alter table component_instance_state add foreign key fk_component_instance_id_cis (component_instance_id) references component_instance (component_instance_id);
alter table component_instance_state add foreign key fk_component_state_id_cis (component_state_id) references component_state (component_state_id);

alter table component_state add foreign key fk_component_state_category_id_cs (component_state_category_id) references component_state_category (component_state_category_id);

