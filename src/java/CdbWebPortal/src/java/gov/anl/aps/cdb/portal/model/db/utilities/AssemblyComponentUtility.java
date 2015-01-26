package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.exceptions.CdbPortalException;
import gov.anl.aps.cdb.portal.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class AssemblyComponentUtility {

    public static TreeNode createAssemblyRoot(Component assembly) throws CdbPortalException {
        TreeNode assemblyRoot = new DefaultTreeNode(new AssemblyComponent(), null);
        if (assembly == null) {
            throw new CdbPortalException("Cannot create assembly tree view: assembly is not set.");
        }

        // Use "tree branch" list to prevent circular trees
        // Whenever new assembly is encountered, it will be added to the tree branch list before populating
        // element node, and removed from the branch list after population is done
        // If an object is encountered twice in the tree branch, this indicates an error.
        List<Component> componentTreeBranch = new ArrayList<>();
        populateAssemblyComponentNode(assemblyRoot, assembly, componentTreeBranch);
        return assemblyRoot;
    }

    private static void populateAssemblyComponentNode(TreeNode assemblyComponentNode, Component component, List<Component> componentTreeBranch) throws InvalidObjectState {
        List<AssemblyComponent> childAssemblyComponentList = component.getAssemblyComponentList();
        if (childAssemblyComponentList == null) {
            return;
        }
        componentTreeBranch.add(component);
        for (AssemblyComponent childAssemblyComponent : childAssemblyComponentList) {
            Component childComponent = childAssemblyComponent.getComponent();

            if (componentTreeBranch.contains(childComponent)) {
                throw new InvalidObjectState("Cannot create assembly tree view: circular child/parent relationship found with components "
                        + component.getName() + " and " + childComponent.getName() + ".");
            }
            TreeNode childComponentNode = new DefaultTreeNode(childAssemblyComponent, assemblyComponentNode);
            populateAssemblyComponentNode(childComponentNode, childComponent, componentTreeBranch);
        }
        componentTreeBranch.remove(component);
    }

    
}
