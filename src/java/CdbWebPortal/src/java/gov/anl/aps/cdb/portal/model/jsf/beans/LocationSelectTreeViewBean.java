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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named("locationSelectTreeViewBean")
@RequestScoped
public class LocationSelectTreeViewBean implements Serializable {

    @EJB
    LocationDbFacade locationFacade;

    private TreeNode rootNode;

    private List<Location> skipLocationList;

    private TreeNode selectedNode;

    @PostConstruct
    public void init() {
        rootNode = createLocationRoot();
    }

    public TreeNode getRootNode() {
        return rootNode;
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

    public void removeLocationNode(Location location) {
        
    }
    
    public TreeNode createLocationRoot() {
        TreeNode locationRoot = new DefaultTreeNode(new Location(), null);
        List<Location> locationsWithoutParents = locationFacade.findLocationsWithoutParents();
        for (Location location : locationsWithoutParents) {
            if (!skipLocation(location)) {
                TreeNode locationNode = new DefaultTreeNode(location, locationRoot);
                populateLocationNode(locationNode, location);
            }
        }
        return locationRoot;
    }

    private void populateLocationNode(TreeNode locationNode, Location location) {
        if (skipLocation(location)) {
            return;
        }
        for (Location childLocation : location.getChildLocationList()) {
            TreeNode childLocationNode = new DefaultTreeNode(childLocation, locationNode);
            populateLocationNode(childLocationNode, childLocation);
        }
    }

}
