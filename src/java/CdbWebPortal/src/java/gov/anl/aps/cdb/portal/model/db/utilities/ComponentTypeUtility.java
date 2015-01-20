package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ComponentTypeUtility {

    public static List<ComponentType> filterComponentType(String query, List<ComponentType> candidateComponentTypeList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<ComponentType> filteredComponentTypeList = new ArrayList<>();
        for (ComponentType componentType : candidateComponentTypeList) {
            boolean nameContainsQuery = searchPattern.matcher(componentType.getName()).find();
            if (nameContainsQuery) {
                filteredComponentTypeList.add(componentType);
            }
        }
        return filteredComponentTypeList;
    }
}
