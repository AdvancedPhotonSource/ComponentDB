/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

/**
 *
 * @author craig
 */
public class CustomOutputHandler extends RefOutputHandler {
    
    public CustomOutputHandler(
            String columnName, String description, String domainGetterMethod, String domainTransferGetterMethod) {
        super(columnName, description, domainGetterMethod, domainTransferGetterMethod);
    }
    
}
