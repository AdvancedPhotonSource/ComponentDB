/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: $
 *   $Date: $
 *   $Revision: $
 *   $Author: $
 */
package gov.anl.aps.cdb.common.utilities;

import java.util.List;
import java.util.ListIterator;
import javax.faces.model.SelectItem;

/**
 * Utility class for manipulating collections.
 */
public class CollectionUtility {

    /**
     * Prepare array of SelectItem objects for menus
     *
     * @param entities list of objects
     * @param selectOne true if resulting array should contain "Select" string
     * @return array of SelectItem objects
     */
    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "Select");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    /**
     * Prepare display string for a list of objects.
     *
     * @param list object list
     * @param beginDelimiter beginning delimiter
     * @param itemDelimiter item delimiter
     * @param endDelimiter ending delimiter
     * @return list display string
     */
    public static String displayItemList(List<?> list, String beginDelimiter, String itemDelimiter, String endDelimiter) {
        String result = beginDelimiter;
        boolean addItemDelimiter = false;
        if (list != null) {
            for (Object item : list) {
                if (!addItemDelimiter) {
                    addItemDelimiter = true;
                } else {
                    result += itemDelimiter;
                }
                result += item.toString();
            }
        }
        result += endDelimiter;
        return result;
    }

    /**
     * Prepare display string for a list of objects without outside delimiters.
     *
     * @param list object list
     * @param itemDelimiter item delimiter
     * @return list display string
     */
    public static String displayItemListWithoutOutsideDelimiters(List<?> list, String itemDelimiter) {
        String beginDelimiter = "";
        String endDelimiter = "";
        return displayItemList(list, beginDelimiter, itemDelimiter, endDelimiter);
    }

    /**
     * Prepare display string for a list of objects with spaces as delimiters.
     *
     * @param list object list
     * @return list display string
     */
    public static String displayItemListWithoutDelimiters(List<?> list) {
        String beginDelimiter = "";
        String itemDelimiter = "";
        String endDelimiter = "";
        return displayItemList(list, beginDelimiter, itemDelimiter, endDelimiter);
    }

    /**
     * Remove null references from list of objects.
     *
     * @param list object list
     */
    public static void removeNullReferencesFromList(List<?> list) {
        if (list == null) {
            return;
        }
        ListIterator iterator = list.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
    }
}
