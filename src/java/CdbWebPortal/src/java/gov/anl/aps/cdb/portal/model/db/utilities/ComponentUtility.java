package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ComponentUtility {

    public static List<Component> filterComponent(String query, List<Component> candidateComponentList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Component> filteredComponentList = new ArrayList<>();
        for (Component location : candidateComponentList) {
            boolean nameContainsQuery = searchPattern.matcher(location.getName()).find();
            if (nameContainsQuery) {
                filteredComponentList.add(location);
            }
        }
        return filteredComponentList;
    }
}
