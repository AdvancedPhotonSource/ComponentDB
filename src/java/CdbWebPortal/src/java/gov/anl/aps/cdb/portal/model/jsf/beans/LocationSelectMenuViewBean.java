/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.beans.LocationDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;

@Named("locationSelectMenuViewBean")
@RequestScoped
public class LocationSelectMenuViewBean implements Serializable {

    @EJB
    LocationDbFacade locationFacade;

    private MenuModel locationMenu;

    private List<Location> skipLocationList;

    private TreeNode selectedNode;

    @PostConstruct
    public void init() {
        locationMenu = createLocationMenu();
    }

    public MenuModel getLocationMenu() {
        return locationMenu;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public Location getSelectedLocation() {
        if (selectedNode != null) {
            return (Location) selectedNode.getData();
        }
        return null;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public List<Location> getSkipLocationList() {
        return skipLocationList;
    }

    public void setSkipLocationList(List<Location> skipLocationList) {
        this.skipLocationList = skipLocationList;
    }

    public void addToSkipLocationList(Location location) {
        if (skipLocationList == null) {
            skipLocationList = new ArrayList<>();
        }
        skipLocationList.add(location);
    }
    
    public boolean skipLocation(Location location) {
        if (skipLocationList == null) {
            return false;
        }
        return skipLocationList.contains(location);
    }

    public MenuModel createLocationMenu() {
        locationMenu = new DefaultMenuModel();
        List<Location> locationsWithoutParents = locationFacade.findLocationsWithoutParents();
        for (Location location : locationsWithoutParents) {
            if (location.getChildLocationList().isEmpty()) {
                DefaultMenuItem menuItem = new DefaultMenuItem(location.getName());
                menuItem.setCommand("#{locationController.selectParentLocation}");
                locationMenu.addElement(menuItem);
            } else {
                DefaultSubMenu subMenu = new DefaultSubMenu(location.getName());
                populateLocationSubMenu(subMenu, location);
                locationMenu.addElement(subMenu);
            }
        }
        return locationMenu;
    }

    private void populateLocationSubMenu(DefaultSubMenu subMenu, Location location) {
        for (Location childLocation : location.getChildLocationList()) {
            if (childLocation.getChildLocationList().isEmpty()) {
                subMenu.addElement(new DefaultMenuItem(childLocation.getName()));
            }
            else {
                DefaultSubMenu childSubMenu = new DefaultSubMenu(childLocation.getName());
                populateLocationSubMenu(childSubMenu, childLocation);
                subMenu.addElement(childSubMenu);
            }
        }
    }

}
