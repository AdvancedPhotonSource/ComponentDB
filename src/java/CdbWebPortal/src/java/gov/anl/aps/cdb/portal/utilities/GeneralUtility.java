/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

/**
 *
 * @author craig
 */
public class GeneralUtility {
    
    public static String generatePaddedUnitName(int itemNumber) {
        return String.format("Unit: %09d", itemNumber);
    }
    
}
