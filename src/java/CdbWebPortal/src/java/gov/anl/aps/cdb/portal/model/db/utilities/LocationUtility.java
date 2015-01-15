package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LocationUtility {

    public static List<Location> filterLocation(String query, List<Location> candidateLocationList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Location> filteredLocationList = new ArrayList<>();
        for (Location location : candidateLocationList) {
            boolean nameContainsQuery = searchPattern.matcher(location.getName()).find();
            if (nameContainsQuery) {
                filteredLocationList.add(location);
            }
        }
        return filteredLocationList;
    }
}
