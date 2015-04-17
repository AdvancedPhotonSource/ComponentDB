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
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.utilities.AssemblyComponentUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * JSF bean for assembly component list tree view.
 */
@Named("assemblyComponentListTreeViewBean")
@RequestScoped
public class AssemblyComponentListTreeViewBean implements Serializable {

    private static final Logger logger = Logger.getLogger(AssemblyComponentListTreeViewBean.class.getName());

    private TreeNode rootNode = null;
    private Component assembly = null;

    @PostConstruct
    public void init() {
        //rootNode = createComponentElementRoot();
    }

    public Component getAssembly() {
        return assembly;
    }

    public void setAssembly(Component assembly) {
        this.assembly = assembly;
    }

    public void resetRootNode() {
        rootNode = null;
    }

    public TreeNode createRootNodeForAssembly(Component assembly) {
        this.assembly = assembly;
        if (rootNode == null) {
            // rootNode = createAssemblyRoot();
            try {
                // rootNode = createAssemblyRoot();
                rootNode = AssemblyComponentUtility.createAssemblyRoot(assembly);
            } catch (CdbException ex) {
                logger.error(ex.getMessage());
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return rootNode;
    }

    public TreeNode getRootNode() {
        if (rootNode == null) {
            try {
                // rootNode = createAssemblyRoot();
                rootNode = AssemblyComponentUtility.createAssemblyRoot(assembly);
            } catch (CdbException ex) {
                logger.error(ex.getMessage());
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return rootNode;
    }

}
