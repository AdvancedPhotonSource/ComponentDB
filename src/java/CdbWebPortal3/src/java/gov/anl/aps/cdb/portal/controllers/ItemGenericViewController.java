/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named("itemGenericViewController")
@SessionScoped
public class ItemGenericViewController extends ItemController {
    
    public ItemGenericViewController() {
        super();
        displayNumberOfItemsPerPage = 25; 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public String getItemIdentifier1Title() {
        return "Item Identifier 1";
    }

    @Override
    public String getItemIdentifier2Title() {
        return "Item Identifier 2";
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Items derived from Item";
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "Derived from Item";
    }

    @Override
    public String getEntityTypeName() {
        return "item"; 
    }

    @Override
    public String getStyleName() {
        return "items"; 
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Item"; 
    }
    
}