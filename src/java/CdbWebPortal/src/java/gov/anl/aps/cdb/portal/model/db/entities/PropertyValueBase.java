/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class PropertyValueBase extends CdbEntity {
    
    public abstract List<PropertyMetadataBase> getPropertyMetadataBaseList(); 
    
}
