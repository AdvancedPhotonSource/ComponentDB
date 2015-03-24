/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.beans.ComponentDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named("componentListTreeViewBean")
@RequestScoped
public class ComponentListTreeViewBean implements Serializable {

    @EJB
    ComponentDbFacade componentFacade;

    private TreeNode rootNode;

    @PostConstruct
    public void init() {
        rootNode = createComponentRoot();
    }

    public void resetRootNode() {
        rootNode = null;
    }
    
    public TreeNode getRootNode() {
        if (rootNode == null) {
            rootNode = createComponentRoot(); 
        }
        return rootNode;
    }

    public TreeNode createComponentRoot() {
        TreeNode componentRoot = new DefaultTreeNode(new Component(), null);
        List<Component> components = componentFacade.findAll();
        for (Component component : components) {
            TreeNode componentNode = new DefaultTreeNode(component, componentRoot);
            populateComponentNode(componentNode, component);
        }
        return componentRoot;
    }

    private void populateComponentNode(TreeNode componentNode, Component component) {
        for (AssemblyComponent assemblyComponent : component.getAssemblyComponentList()) {
            TreeNode assemblyComponentNode = new DefaultTreeNode(assemblyComponent.getComponent(), componentNode);
            populateComponentNode(assemblyComponentNode, assemblyComponent.getComponent());
        }
    }

}
