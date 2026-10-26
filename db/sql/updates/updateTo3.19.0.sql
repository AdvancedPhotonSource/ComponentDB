--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--

-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < updateTo3.19.0.sql`

DROP VIEW IF EXISTS v_ioc_owner_info;

CREATE VIEW v_ioc_owner_info AS
SELECT DISTINCT
    i.name,
    owner.username AS owner_username,
    CONCAT(
        owner.last_name,
        ', ',
        owner.first_name,
        CASE
            WHEN NULLIF(TRIM(owner.middle_name), '') IS NOT NULL
            THEN CONCAT(' ', TRIM(owner.middle_name))
            ELSE ''
        END
    ) AS owner_name
FROM v_item_extras AS i
INNER JOIN item_entity_type AS iet
    ON iet.item_id = i.id
INNER JOIN item_element AS ie
    ON ie.id = i.self_element_id
INNER JOIN entity_info AS ei
    ON ei.id = ie.entity_info_id
INNER JOIN user_info AS owner
    ON owner.id = ei.owner_user_id
WHERE iet.entity_type_id = 11;
