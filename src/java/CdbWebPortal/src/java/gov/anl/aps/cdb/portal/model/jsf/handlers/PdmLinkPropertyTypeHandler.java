/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/model/jsf/handlers/PdmLinkPropertyTypeHandler.java $
 *   $Date: 2015-12-21 11:34:12 -0600 (Mon, 21 Dec 2015) $
 *   $Revision: 884 $
 *   $Author: djarosz $
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 * PDMLink property type handler.
 *
 * This handler uses CDB web service.
 */
public class PdmLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "PDMLink";
    
    private static final String INFO_ACTION_COMMAND = "updatePdmLinkDrawingPropertyInfoDialog();";

    public PdmLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.INFO_ACTION);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue){
        propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory){
        propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
}
