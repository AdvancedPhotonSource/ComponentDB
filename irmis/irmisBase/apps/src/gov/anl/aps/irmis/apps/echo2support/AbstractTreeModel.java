/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.echo2support;

import echopointng.tree.TreeModel;

// This class takes care of the event listener lists required by TreeModel.
// It also adds "fire" methods that call the methods in TreeModelListener.
// Look in TreeModelSupport for all of the pertinent code.
public abstract class AbstractTreeModel extends TreeModelSupport implements TreeModel {

}
