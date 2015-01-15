package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Design;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DesignUtility {

    public static List<Design> filterDesign(String query, List<Design> candidateDesignList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Design> filteredDesignList = new ArrayList<>();
        for (Design location : candidateDesignList) {
            boolean nameContainsQuery = searchPattern.matcher(location.getName()).find();
            if (nameContainsQuery) {
                filteredDesignList.add(location);
            }
        }
        return filteredDesignList;
    }
}
