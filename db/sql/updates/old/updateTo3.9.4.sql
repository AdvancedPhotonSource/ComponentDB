--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.9.4.sql`

INSERT INTO setting_type VALUE (8038,'ItemElement.List.Display.SimpleViewPartName','Display part name (element name) on the simple view.','false');
