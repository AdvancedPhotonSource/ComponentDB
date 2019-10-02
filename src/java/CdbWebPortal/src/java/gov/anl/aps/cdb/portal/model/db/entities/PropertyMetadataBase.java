/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

/**
 *
 * @author djarosz
 */
public abstract class PropertyMetadataBase extends CdbEntity {
    
    public abstract String getMetadataKey(); 
    public abstract String getMetadataValue();
    
    
}
