/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.beans.LocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named("designElementListTreeViewBean")
@RequestScoped
public class DesignElementListTreeViewBean implements Serializable {

    @EJB
    LocationFacade designElementFacade;

    private TreeNode rootNode;

    @PostConstruct
    public void init() {
        rootNode = createDesignElementRoot();
    }

    public void resetRootNode() {
        rootNode = null;
    }
    
    public TreeNode getRootNode() {
        if (rootNode == null) {
            rootNode = createDesignElementRoot(); 
        }
        return rootNode;
    }

    public TreeNode createDesignElementRoot() {
        TreeNode designElementRoot = new DefaultTreeNode(new DesignElement(), null);
//        List<DesignElement> designElementWithoutParents = designElementFacade.findDesignElementsWithoutParents();
//        for (DesignElement designElement : designElementWithoutParents) {
//            TreeNode designElementNode = new DefaultTreeNode(designElement, designElementRoot);
//            populateDesignElementNode(designElementNode, designElement);
//        }
        return designElementRoot;
    }

    private void populateDesignElementNode(TreeNode designElementNode, DesignElement designElement) {
//        for (DesignElement childDesignElement : designElement.getChildDesignElementList()) {
//            TreeNode childDesignElementNode = new DefaultTreeNode(childDesignElement, designElementNode);
//            populateDesignElementNode(childDesignElementNode, childDesignElement);
//        }
    }

}
