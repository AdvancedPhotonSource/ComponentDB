-- Alters schema for PV client, adding indexes and fk constraints.

-- Indexes 

create index idx_rec_nm_rc on rec_client (rec_nm);
create index idx_vuri_id_rc on rec_client (vuri_id);

create index idx_uri_id_ur on vuri (uri_id);

create index idx_parent_vuri_id_vur on vuri_rel (parent_vuri_id);
create index idx_child_vuri_id_vur on vuri_rel (child_vuri_id);

-- Foreign key constraints
alter table rec_client add constraint fk_vuri_id_rc foreign key (vuri_id) references vuri (vuri_id);

alter table vuri add constraint fk_uri_id_vur foreign key (uri_id) references uri (uri_id);

alter table vuri_rel add constraint fk_parent_vuri_id_vur foreign key (parent_vuri_id) references vuri (vuri_id);
alter table vuri_rel add constraint fk_child_vuri_id_vur foreign key (child_vuri_id) references vuri (vuri_id);


