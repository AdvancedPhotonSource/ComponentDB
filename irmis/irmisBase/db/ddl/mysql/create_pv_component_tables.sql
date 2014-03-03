
create table dev_sup_component_type
  (
    dev_sup_component_type_id int not null primary key auto_increment,
    dev_sup_dset_id int,
    component_type_id int,
    modified_date timestamp,
    modified_by varchar(10)
  ) type=InnoDb;

create table dev_sup_dset
  (
    dev_sup_dset_id int not null primary key auto_increment,
    dev_sup_dset_name varchar(30),
    modified_date timestamp,
    modified_by varchar(10)
  ) type=InnoDb;

create table dev_sup_link_rule
  (
    dev_sup_link_rule_id int not null primary key auto_increment,
    dev_sup_dset_id int,
    link_rule_token1_type_id int,
    link_rule_token2_type_id int,
    link_rule_token3_type_id int,
    link_rule_token4_type_id int,
    link_rule_param_type_id int
  ) type=InnoDb;

create table link_rule_token_type
  (
    link_rule_token_type_id int not null primary key,
    link_rule_token_type varchar(30)
  ) type=InnoDb;

insert into link_rule_token_type values (1, 'Card Number');
insert into link_rule_token_type values (2, 'Signal Number');

create table rec_pin
  (
    rec_pin_id int not null primary key auto_increment,
    rec_id int,
    port_pin_id int
  ) type=InnoDb;

create table rec_component_type
  (
    rec_component_type_id int not null primary key auto_increment,
    rec_id int,
    component_type_id int,
    dtyp_str varchar(30),
    link_str varchar(30)
  ) type=InnoDb;

