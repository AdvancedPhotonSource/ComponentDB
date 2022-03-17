--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.10.1.sql`

INSERT INTO setting_type VALUE (21013,'ItemDomainMachineDesign.List.Display.QrId','Display column for qrid in machine design list.','false');
