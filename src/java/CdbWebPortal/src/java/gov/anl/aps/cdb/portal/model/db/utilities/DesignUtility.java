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

import gov.anl.aps.cdb.portal.model.db.entities.Design;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DB utility class for designs.
 */
public class DesignUtility {

    public static List<Design> filterDesign(String query, List<Design> candidateDesignList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Design> filteredDesignList = new ArrayList<>();
        for (Design design : candidateDesignList) {
            boolean nameContainsQuery = searchPattern.matcher(design.getName()).find();
            if (nameContainsQuery) {
                filteredDesignList.add(design);
            }
        }
        return filteredDesignList;
    }

}
