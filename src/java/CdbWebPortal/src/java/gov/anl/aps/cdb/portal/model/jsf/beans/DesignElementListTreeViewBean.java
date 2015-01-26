/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.exceptions.CdbPortalException;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignElementUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named("designElementListTreeViewBean")
@RequestScoped
public class DesignElementListTreeViewBean implements Serializable {

    private static final Logger logger = Logger.getLogger(DesignElementListTreeViewBean.class.getName());

    private TreeNode rootNode = null;
    private Design parentDesign = null;

    @PostConstruct
    public void init() {
        //rootNode = createDesignElementRoot();
    }

    public Design getParentDesign() {
        return parentDesign;
    }

    public void setParentDesign(Design parentDesign) {
        this.parentDesign = parentDesign;
    }

    public void resetRootNode() {
        rootNode = null;
    }

    public TreeNode createRootNodeForDesign(Design design) {
        parentDesign = design;
        if (rootNode == null) {
            try {
                //rootNode = createDesignElementRoot();
                rootNode = DesignElementUtility.createDesignElementRoot(design);
            } catch (CdbPortalException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return rootNode;
    }

    public TreeNode getRootNode() {
        if (rootNode == null) {
            //rootNode = createDesignElementRoot();
            try {
                //rootNode = createDesignElementRoot();
                rootNode = DesignElementUtility.createDesignElementRoot(parentDesign);
            } catch (CdbPortalException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return rootNode;
    }

//    public TreeNode createDesignElementRoot() {
//        TreeNode designElementRoot = new DefaultTreeNode(new DesignElement(), null);
//        if (parentDesign == null) {
//            String error = "Cannot create design element tree view: parent design is not set.";
//            logger.error(error);
//            SessionUtility.addErrorMessage("Error", error);
//            return designElementRoot;
//        }
//
//        // Use "tree branch" list to prevent circular trees
//        // Whenever new design is encountered, it will be added to the tree branch list before populating
//        // element node, and removed from the branch list after population is done
//        // If an object is encountered twice in the tree branch, this designates an error.
//        List<Design> designTreeBranch = new ArrayList<>();
//        populateDesignNode(designElementRoot, parentDesign, designTreeBranch);
//        return designElementRoot;
//    }
//
//    private void populateDesignNode(TreeNode designElementNode, Design design, List<Design> designTreeBranch) {
//        List<DesignElement> designElementList = design.getDesignElementList();
//        if (designElementList == null) {
//            return;
//        }
//        designTreeBranch.add(design);
//        for (DesignElement designElement : designElementList) {
//            Component component = designElement.getComponent();
//            Design childDesign = designElement.getChildDesign();
//            if (component == null && childDesign == null) {
//                continue;
//            }
//
//            TreeNode childDesignElementNode = new DefaultTreeNode(designElement, designElementNode);
//            if (childDesign != null) {
//                if (designTreeBranch.contains(childDesign)) {
//                    String error = "Cannot create design element tree view: circular child/parent relationship found with design "
//                            + childDesign.getName() + ".";
//                    logger.error(error);
//                    SessionUtility.addErrorMessage("Error", error);
//                    break;
//                }
//                populateDesignNode(childDesignElementNode, childDesign, designTreeBranch);
//            }
//        }
//        designTreeBranch.remove(design);
//    }

}
