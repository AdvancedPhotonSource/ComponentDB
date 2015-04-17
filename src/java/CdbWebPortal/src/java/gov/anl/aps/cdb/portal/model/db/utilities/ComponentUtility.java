/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * DB utility class for components.
 */
public class ComponentUtility {

    public static List<Component> filterComponent(String query, List<Component> candidateComponentList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Component> filteredComponentList = new ArrayList<>();
        for (Component location : candidateComponentList) {
            boolean nameContainsQuery = searchPattern.matcher(location.getName()).find();
            if (nameContainsQuery) {
                filteredComponentList.add(location);
            }
        }
        return filteredComponentList;
    }

    public static TreeNode createAssemblyRoot(Component assembly) throws CdbException {
        TreeNode assemblyRoot = new DefaultTreeNode(new Component(), null);
        if (assembly != null) {

            // Use "tree branch" list to prevent circular trees
            // Whenever new design is encountered, it will be added to the tree branch list before populating
            // element node, and removed from the branch list after population is done
            // If an object is encountered twice in the tree branch, this designates an error.
            List<Component> assemblyTreeBranch = new ArrayList<>();
            TreeNode assemblyNode = new DefaultTreeNode(assembly, assemblyRoot);
            populateAssemblyNode(assemblyNode, assembly, assemblyTreeBranch);
        }
        return assemblyRoot;
    }

    private static void populateAssemblyNode(TreeNode assemblyComponentNode, Component assembly, List<Component> assemblyTreeBranch) throws InvalidObjectState {
        List<AssemblyComponent> assemblyComponentList = assembly.getAssemblyComponentList();
        if (assemblyComponentList == null) {
            return;
        }
        assemblyTreeBranch.add(assembly);
        for (AssemblyComponent assemblyComponent : assemblyComponentList) {
            Component component = assemblyComponent.getComponent();
            TreeNode childAssemblyComponentNode = new DefaultTreeNode(component, assemblyComponentNode);
            if (assemblyTreeBranch.contains(component)) {
                throw new InvalidObjectState("Cannot create assembly component tree view: circular child/parent relationship found with components "
                        + assembly.getName() + " and " + component.getName() + ".");
            }
            populateAssemblyNode(childAssemblyComponentNode, component, assemblyTreeBranch);
        }

        assemblyTreeBranch.remove(assembly);
    }
}
