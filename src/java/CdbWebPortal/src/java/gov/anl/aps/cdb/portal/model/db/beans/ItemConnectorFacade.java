/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemConnectorFacade extends CdbEntityFacade<ItemConnector> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemConnectorFacade() {
        super(ItemConnector.class);
    }
    
    public static ItemConnectorFacade getInstance() {
        return (ItemConnectorFacade) SessionUtility.findFacade(ItemConnectorFacade.class.getSimpleName()); 
    }

    public void create(ItemConnector entity) {        
        ConnectorFacade.getInstance().create(entity.getConnector());
        super.create(entity);
    }

}
