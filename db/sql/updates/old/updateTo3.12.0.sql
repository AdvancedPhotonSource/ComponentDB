--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.12.0.sql`


insert into user_group (name, description) value ('CDB_MAINTAINER', 'System Maintainer Group');
insert into user_group (name, description) value ('CDB_ADVANCED', 'System Advanced Group');

ALTER TABLE item_element ADD COLUMN is_housed bool  NULL DEFAULT 1 AFTER is_required;
ALTER TABLE item_element_history ADD COLUMN is_housed bool  NULL DEFAULT 1 AFTER is_required;
