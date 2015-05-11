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

import gov.anl.aps.cdb.portal.model.db.entities.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * DB utility class for locations.
 */
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
    
    public static TreeNode createLocationRoot(List<Location> locationsWithoutParents) {
        TreeNode locationRoot = new DefaultTreeNode(new Location(), null);
        for (Location location : locationsWithoutParents) {
            TreeNode locationNode = new DefaultTreeNode(location, locationRoot);
            populateLocationNode(locationNode, location);
        }
        return locationRoot;
    }

    private static void populateLocationNode(TreeNode locationNode, Location location) {
        for (Location childLocation : location.getChildLocationList()) {
            TreeNode childLocationNode = new DefaultTreeNode(childLocation, locationNode);
            populateLocationNode(childLocationNode, childLocation);
        }
    }    
}
