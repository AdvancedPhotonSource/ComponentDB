/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DB utility class for items.
 */
public class ItemUtility {

    public static List<Item> filterItem(String query, List<Item> candidateItemList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Item> filteredItemList = new ArrayList<>();
        for (Item item : candidateItemList) {
            boolean nameContainsQuery = searchPattern.matcher(item.getName()).find();
            if (nameContainsQuery) {
                filteredItemList.add(item);
            }
        }
        return filteredItemList;
    }
    
}
