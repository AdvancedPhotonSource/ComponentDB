--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.15.5.sql`

ALTER TABLE item_type ADD COLUMN `sort_order` float(10,2) unsigned DEFAULT NULL AFTER domain_id;
ALTER TABLE item_category ADD COLUMN `sort_order` float(10,2) unsigned DEFAULT NULL AFTER domain_id;

# Set default sort order for item_type
SET @row_number = 0;
CREATE TEMPORARY TABLE sorted_type_ids AS
(
SELECT id FROM item_type ORDER BY name
);
CREATE TEMPORARY TABLE type_row_num AS
(
SELECT *, (@row_number:=@row_number + 1) as row_num from sorted_type_ids
);
update item_type it inner join type_row_num trn on trn.id = it.id set sort_order = row_num;

# Set default sort order for item_category
SET @row_number = 0;
CREATE TEMPORARY TABLE sorted_category_ids AS
(
SELECT id FROM item_category ORDER BY name
);
CREATE TEMPORARY TABLE category_row_num AS
(
SELECT *, (@row_number:=@row_number + 1) as row_num from sorted_category_ids
);
update item_category ic inner join category_row_num crn on crn.id = ic.id set sort_order = row_num;



