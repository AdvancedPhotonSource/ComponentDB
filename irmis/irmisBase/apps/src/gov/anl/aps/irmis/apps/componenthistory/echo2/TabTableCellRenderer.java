/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

// Echo2 
import nextapp.echo2.app.Table;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.table.DefaultTableCellRenderer;

/**
 * Customized table cell renderer for table cells in the tabbed history tables.
 */
class TabTableCellRenderer extends DefaultTableCellRenderer {
    
    public nextapp.echo2.app.Component getTableCellRendererComponent (Table table,
                                                                      Object value,
                                                                      int column, int row) {
        
        Label c = new Label((String)value);
        c.setStyleName("Default.TableEx.Label");
        return c;
    }
    
}