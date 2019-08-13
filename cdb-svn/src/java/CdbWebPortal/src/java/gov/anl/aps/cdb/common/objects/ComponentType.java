/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/objects/ComponentType.java $
 *   $Date: 2015-08-11 09:53:36 -0500 (Tue, 11 Aug 2015) $
 *   $Revision: 693 $
 *   $Author: djarosz $
 */
package gov.anl.aps.cdb.common.objects;

/**
 * Component Type object.
 */
public class ComponentType extends CdbObject {
    
    private int componentTypeCategoryId; 
    private ComponentTypeCategory componentTypeCategory;
    
    public ComponentType() {
        
    }

    public ComponentTypeCategory getComponentTypeCategory() {
        return componentTypeCategory;
    }

    public void setComponentTypeCategory(ComponentTypeCategory componentTypeCategory) {
        this.componentTypeCategory = componentTypeCategory;
    } 

    public int getComponentTypeCategoryId() {
        return componentTypeCategoryId;
    }

    public void setComponentTypeCategoryId(int componentTypeCategoryId) {
        this.componentTypeCategoryId = componentTypeCategoryId;
    }
   
}
