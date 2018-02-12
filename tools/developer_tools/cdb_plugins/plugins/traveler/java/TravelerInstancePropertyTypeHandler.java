/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;

/**
 *
 * @author djarosz
 */
public class TravelerInstancePropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Traveler Instance";   

    public TravelerInstancePropertyTypeHandler() {
        super(HANDLER_NAME);
    }
    
    @Override
    public boolean isPropertyCloneable() {
        return false;
    }
   
}
