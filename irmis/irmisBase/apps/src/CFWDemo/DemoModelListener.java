/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

/**
 * Interface that defines how some class is to be notified of a
 * change in the data model. In our case, the DemoWindow will
 * use this interface to add itself to the DemoModel as a 
 * DemoModelListener.
 *
 */
public interface DemoModelListener {

    public void modified(DemoModelEvent e);

}
