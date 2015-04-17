/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignElementUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * JSF bean for design element list tree view.
 */
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
