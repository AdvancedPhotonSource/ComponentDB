# Alters schema for PV client, adding indexes and fk constraints.
# WARNING: this script is MySQL specific

# Indexes 

alter table rec_client add index idx_rec_nm (rec_nm);
alter table rec_client add index idx_vuri_id (vuri_id);

alter table vuri add index idx_uri_id (uri_id);

alter table vuri_rel add index idx_parent_vuri_id (parent_vuri_id);
alter table vuri_rel add index idx_child_vuri_id (child_vuri_id);

# Foreign key constraints
alter table rec_client add foreign key fk_vuri_id (vuri_id) references vuri (vuri_id) on delete restrict;

alter table vuri add foreign key fk_uri_id (uri_id) references uri (uri_id) on delete restrict;

alter table vuri_rel add foreign key fk_parent_vuri_id (parent_vuri_id) references vuri (vuri_id) on delete restrict;
alter table vuri_rel add foreign key fk_child_vuri_id (child_vuri_id) references vuri (vuri_id) on delete restrict;


