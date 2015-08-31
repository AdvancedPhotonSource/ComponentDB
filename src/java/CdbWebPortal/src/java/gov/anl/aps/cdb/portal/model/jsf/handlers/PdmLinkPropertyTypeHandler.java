/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.api.PdmLinkApi;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import org.apache.log4j.Logger;

/**
 * PDMLink property type handler.
 *
 * This handler uses CDB web service.
 */
public class PdmLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "PDMLink";
    
    private static final String INFO_ACTION_COMMAND = "PF('pdmLinkDrawingPropertyInfoDialogWidget').show();";

    private static final Logger logger = Logger.getLogger(PdmLinkPropertyTypeHandler.class.getName());
    
    private PdmLinkApi pdmLinkApi;

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
