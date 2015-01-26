/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.exceptions.CdbPortalException;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.utilities.AssemblyComponentUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

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
            } catch (CdbPortalException ex) {
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
            } catch (CdbPortalException ex) {
                logger.error(ex.getMessage());
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return rootNode;
    }

//    public TreeNode createAssemblyRoot() {
//        TreeNode assemblyRoot = new DefaultTreeNode(new AssemblyComponent(), null);
//        if (assembly == null) {
//            String error = "Cannot create assembly tree view: assembly is not set.";
//            logger.error(error);
//            SessionUtility.addErrorMessage("Error", error);
//            return assemblyRoot;
//        }
//
//        // Use "tree branch" list to prevent circular trees
//        // Whenever new assembly is encountered, it will be added to the tree branch list before populating
//        // element node, and removed from the branch list after population is done
//        // If an object is encountered twice in the tree branch, this indicates an error.
//        List<Component> componentTreeBranch = new ArrayList<>();
//        populateAssemblyComponentNode(assemblyRoot, assembly, componentTreeBranch);
//        return assemblyRoot;
//    }
//
//    private void populateAssemblyComponentNode(TreeNode assemblyComponentNode, Component component, List<Component> componentTreeBranch) {
//        List<AssemblyComponent> childAssemblyComponentList = component.getAssemblyComponentList();
//        if (childAssemblyComponentList == null) {
//            return;
//        }
//        componentTreeBranch.add(component);
//        for (AssemblyComponent childAssemblyComponent : childAssemblyComponentList) {
//            Component childComponent = childAssemblyComponent.getComponent();
//
//            if (componentTreeBranch.contains(childComponent)) {
//                String error = "Cannot create assembly tree view: circular child/parent relationship found with assembly "
//                        + childComponent.getName() + ".";
//                logger.error(error);
//                SessionUtility.addErrorMessage("Error", error);
//                break;
//            }
//            TreeNode childComponentNode = new DefaultTreeNode(childAssemblyComponent, assemblyComponentNode);
//            populateAssemblyComponentNode(childComponentNode, childComponent, componentTreeBranch);
//        }
//        componentTreeBranch.remove(component);
//    }

}
