/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.LocationHistoryObject;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author darek
 */
public class LocatableItemControllerUtility {
    
    private LocatableItemController locatableItemController; 

    public LocatableItemControllerUtility() {        
        ItemElementRelationshipFacade ierFacade = ItemElementRelationshipFacade.getInstance();
        ItemFacade itemFacade = ItemFacade.getInstance();
        RelationshipTypeFacade rtf = RelationshipTypeFacade.getInstance(); 
        locatableItemController = new LocatableItemController(ierFacade, itemFacade, rtf);
    }    
    
    // TODO move the functionality here.. 
    protected void updateItemLocation(LocatableItem item, UserInfo updateUser) throws InvalidRequest {
        locatableItemController.updateItemLocation(item, updateUser);
    }
    
    public void setItemLocationInfo(LocatableItem locatableItem) {
        locatableItemController.setItemLocationInfo(locatableItem);
    }
    
    public List<LocationHistoryObject> getLocationHistoryObjectList(LocatableItem locatableItem) {
        return locatableItemController.getLocationHistoryObjectList(locatableItem); 
    }
    
    public TreeNode getLocationTreeForLocationHistoryObject(LocationHistoryObject locationHistoryObject) {
        return locatableItemController.getLocationTreeForLocationHistoryObject(locationHistoryObject); 
    }
}
