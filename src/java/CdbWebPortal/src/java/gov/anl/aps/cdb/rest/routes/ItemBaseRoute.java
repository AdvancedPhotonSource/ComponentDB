/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import javax.ejb.EJB;
import javax.ws.rs.PathParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author djarosz
 */
public abstract class ItemBaseRoute extends BaseRoute {
    
    @EJB
    ItemFacade itemFacade;    
    
    @EJB
    ItemProjectFacade itemProjectFacade;

    @EJB
    ItemCategoryFacade itemCategoryFacade;
    
    private static final Logger LOGGER = LogManager.getLogger(ItemBaseRoute.class.getName());        
    
    public Item getItemByIdBase(@PathParam("id") int id) throws ObjectNotFound {        
        Item findById = itemFacade.findById(id);
        if (findById == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with id: " + id);
            LOGGER.error(ex);
            throw ex; 
        }
        return findById;
    }      
    
    public ItemProject getItemProjectById(int id) throws ObjectNotFound {
        ItemProject project = itemProjectFacade.find(id);
        if (project == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item project with id: " + id);
            LOGGER.error(ex);
            throw ex; 
        }
        return project; 
    }
    
    public ItemCategory getItemCategoryById(int id) throws ObjectNotFound {
        ItemCategory itemCategory = itemCategoryFacade.find(id); 
        if (itemCategory == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item category with id: " + id);
            LOGGER.error(ex);
            throw ex;             
        }
        
        return itemCategory; 
    }
 }
