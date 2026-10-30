--
-- Copyright (c) UChicago Argonne, LLC. All rights reserved.
-- See LICENSE file.
--
-- Review dangling representation references before clearing them.
-- Execute by running `mysql CDB_DB_NAME -h 127.0.0.1 -u cdb -p < cleanup-dangling-item-element-representations.sql`

SELECT
    representing.id,
    representing.parent_item_id,
    representing.represents_item_element_id
FROM item_element AS representing
LEFT JOIN item_element AS represented
    ON represented.id = representing.represents_item_element_id
WHERE representing.represents_item_element_id IS NOT NULL
  AND represented.id IS NULL;

UPDATE item_element AS representing
LEFT JOIN item_element AS represented
    ON represented.id = representing.represents_item_element_id
SET representing.represents_item_element_id = NULL
WHERE representing.represents_item_element_id IS NOT NULL
  AND represented.id IS NULL;
