/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainLocationControllerUtility extends ItemControllerUtility<ItemDomainLocation, ItemDomainLocationFacade> {   
    
    private static final Logger logger = LogManager.getLogger(ItemDomainLocationControllerUtility.class.getName());

    @Override
    public boolean isEntityHasQrId() {
        return true; 
    }

    @Override
    public boolean isEntityHasName() {
        return true;
    }

    @Override
    public boolean isEntityHasProject() {
        return false;
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.location.getValue(); 
    }

    @Override
    protected ItemDomainLocationFacade getItemFacadeInstance() {
        return ItemDomainLocationFacade.getInstance(); 
    }
    
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public String getEntityTypeName() {
        return "location";
    }
    
    @Override
    protected ItemDomainLocation instenciateNewItemDomainEntity() {
        return new ItemDomainLocation();
    }
    
    public void updateParentForItem(ItemDomainLocation item, Item newParentItem, UserInfo userInfo) throws CdbException {
        if (newParentItem instanceof ItemDomainLocation == false) {
            return;
        }
        ItemDomainLocation newParent = (ItemDomainLocation) newParentItem;

        ItemDomainLocation ittrParentItem = newParent;
        while (ittrParentItem != null) {            
            if (item.equals(ittrParentItem)) {
                String message = "Cannot set location of item as itself or its child.";
                logger.error(message);
                throw new CdbException(message);                 
            }
            
            ittrParentItem = ittrParentItem.getParentItem();
        }

        ItemElement member = item.getParentItemElement();
        List<ItemElement> itemElementMemberList = item.getItemElementMemberList();                   

        if (member != null) {            
            String elementName = generateUniqueElementNameForItem(newParent);

            member.setName(elementName);
            member.setParentItem(newParent);
        } else if (itemElementMemberList.isEmpty()) {
            ItemElement createItemElement = null;            
            
            createItemElement = createItemElement(newParent, userInfo);             
            createItemElement.setContainedItem(item);
            itemElementMemberList.add(createItemElement); 
        } else {
            String message = "Cannot update parent, item does not have one membership."; 
            logger.error(message);
            throw new CdbException(message); 
        }
    }
    
    /**
     * Used by import framework.  Looks up entity by path.
     */
    @Override
    public ItemDomainLocation findByPath(String path) throws CdbException {
        
        if (path.charAt(0) != '/') {
            // first character expected to be forward slash
            throw new CdbException("invalid path format, first character expected to be forward slash");
        }
        
        // tokenize the path string, escaping any embedded delimiters
        String delim = "/";
        String esc = "\\";
        String regex = "(?<!" + Pattern.quote(esc) + ")" + Pattern.quote(delim);
        List<String> pathTokens = Arrays.asList(path.split(regex));
        
        if (pathTokens.isEmpty()) {
            return null;
        }
        
        // get item name and list of parent item names from path
        String itemName = pathTokens.get(pathTokens.size() - 1);
        List<String> pathParentNames = new ArrayList<>();
        if (pathTokens.size() > 1) {
            // here we skip the first element since it is expected to be empty string
            // as the first character is slash, and the last element which is the item name
            pathParentNames = pathTokens.subList(1, pathTokens.size() - 1);
            Collections.reverse(pathParentNames);
        }
        
        // retrieve list of candidate items matching name
        List<ItemDomainLocation> candidateItems = getItemFacadeInstance().findByName(itemName);
        
        // check path against parents for each candidate
        for (ItemDomainLocation candidateItem : candidateItems) {
            
            // create parent path list for candidate item and compare to specified path
            List<String> itemParentNames = new ArrayList<>();
            ItemDomainLocation candidateParent = candidateItem.getParentItem();
            while (candidateParent != null) {
                // replace occurrences of '/' with "\\/" to match syntax in specified path
                itemParentNames.add(candidateParent.getName().replace("/", "\\/"));
                candidateParent = candidateParent.getParentItem();
            }
            if (itemParentNames.equals(pathParentNames)) {
                // candidate item parent path matches specified path
                return candidateItem;
            }
        }
                    
        // no match
        return null;
    }
        
}
