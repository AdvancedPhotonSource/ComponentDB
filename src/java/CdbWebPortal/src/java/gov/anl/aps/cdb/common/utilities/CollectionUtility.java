package gov.anl.aps.cdb.common.utilities;

import java.util.List;
import java.util.ListIterator;
import javax.faces.model.SelectItem;

public class CollectionUtility
{

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

    public static String displayItemList(List<?> list, String beginDelimiter, String itemDelimiter, String endDelimiter) {
        String result = beginDelimiter;
        boolean addItemDelimiter = false;
        if (list != null) {
            for (Object item : list) {
                if (!addItemDelimiter) {
                    addItemDelimiter = true;
                }
                else {
                    result += itemDelimiter;
                }
                result += item.toString();
            }
        }
        result += endDelimiter;
        return result;
    }

    public static String displayItemListWithoutOutsideDelimiters(List<?> list, String itemDelimiter) {
        String beginDelimiter = "";
        String endDelimiter = "";
        return displayItemList(list, beginDelimiter, itemDelimiter, endDelimiter);
    }
    
    public static String displayItemListWithoutDelimiters(List<?> list) {
        String beginDelimiter = "";
        String itemDelimiter = "";
        String endDelimiter = "";
        return displayItemList(list, beginDelimiter, itemDelimiter, endDelimiter);
    }

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
