/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

/**
 *
 * @author djarosz
 */
@Stateless
public class ConnectorFacade extends CdbEntityFacade<Connector> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConnectorFacade() {
        super(Connector.class);
    }
    
    public List<Connector> getAvailableConnectorsForInventoryItem(Item item, ConnectorType connectorType, Boolean is_male) {
        try {
           StoredProcedureQuery query = em.createNamedStoredProcedureQuery("connector.available_connectors_for_inventory_item"); 
           query.setParameter("item_id", item.getId()); 
           
           Integer connectorTypeId = null; 
           
           if (connectorType != null){
               connectorTypeId = connectorType.getId(); 
           }
           
           query.setParameter("connector_type_id", connectorTypeId);
           query.setParameter("is_male", is_male);
           
           return query.getResultList(); 
        } catch (NoResultException ex) {
        }
        return null;
    }
    
}
