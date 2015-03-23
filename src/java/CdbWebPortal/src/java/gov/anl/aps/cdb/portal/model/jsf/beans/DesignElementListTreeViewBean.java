/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignElementUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

@Named("designElementListTreeViewBean")
@RequestScoped
public class DesignElementListTreeViewBean implements Serializable {

    private static final Logger logger = Logger.getLogger(DesignElementListTreeViewBean.class.getName());

    private TreeNode rootNode = null;
    private Design parentDesign = null;

    @PostConstruct
    public void init() {
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
            } catch (CdbException ex) {
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
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return rootNode;
    }



}
