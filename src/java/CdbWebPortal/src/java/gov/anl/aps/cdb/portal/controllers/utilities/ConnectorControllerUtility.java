/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ConnectorControllerUtility extends CdbEntityControllerUtility<Connector, ConnectorFacade> {
        
    private static final Logger LOGGER = LogManager.getLogger(ConnectorControllerUtility.class.getName());

    @Override
    public Connector createEntityInstance(UserInfo sessionUser) {
        return new Connector(); 
    }
    
    @Override
    protected void prepareEntityDestroy(Connector connector, UserInfo userInfo) throws CdbException {
        if (verifySafeRemovalOfConnector(connector) == false) {
            throw new CdbException("Cannot remove connector, it is shared by multiple items."); 
        }
        super.prepareEntityDestroy(connector, userInfo); 
    }
    
    public boolean verifySafeRemovalOfConnector(Connector connector) {
        
        // Get latest version from DB. 
        connector = findById(connector.getId());   
        
        List<ItemConnector> itemConnectorList = connector.getItemConnectorList();
        
        if (itemConnectorList.size() > 1) {
            // connector is inherited by other items
            return false;
            
        } else {
            // only a single ItemConnector uses this Connector, check that it is for a valid domain
            ItemConnector itemConnector = itemConnectorList.get(0); 
            Item item = itemConnector.getItem(); 
            String domainName = item.getDomain().getName();
            List<String> validDomainNames = new ArrayList<>();
            validDomainNames.add(ItemDomainName.catalog.getValue());
            validDomainNames.add(ItemDomainName.cableCatalog.getValue());
            if (!validDomainNames.contains(domainName)) {
                return false; 
            }           
        } 
        
        return true; 
    }  

    @Override
    protected ConnectorFacade getEntityDbFacade() {
        return ConnectorFacade.getInstance(); 
    }        

    @Override
    public String getEntityTypeName() {
        return "connector"; 
    }
    
}
