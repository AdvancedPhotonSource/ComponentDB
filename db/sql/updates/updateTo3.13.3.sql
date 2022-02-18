--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME --host=127.0.0.1 --user=cdb -p < updateTo3.13.3.sql`

INSERT INTO setting_type values 
(21019,'ItemDomainMachineDesign.List.Filter.FilteredMachineIds', 'Comma seperated list of top level ids that are filtered by default', ''), 
(21020,'ItemDomainMachineDesign.List.FilterBy.Name','Filter for machine by name.',NULL);