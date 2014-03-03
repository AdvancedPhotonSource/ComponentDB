/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw.plugins;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.EventObject;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// XAL framework stuff
import gov.sns.application.*;

// other IRMIS sub-applications
import gov.anl.aps.irmis.apps.component.cfw.ComponentDocument;

// persistence layer
import gov.anl.aps.irmis.persistence.DAOException;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.PersonDAO;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.pv.IOC;
import gov.anl.aps.irmis.persistence.pv.APSIOC;
import gov.anl.aps.irmis.persistence.pv.APSIOCDAO;

// for registering protected gui components
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.login.RolePrincipal;

// application helpers
import gov.anl.aps.irmis.apps.AppsUtil;

/**
 * Plugin for displaying/editing APS specific IOC info based on the aps_ioc table.
 */
public class APSIOCInfo implements IOCExtendedInfoPlugin {

    private JPanel iocDetailsPanel; 
    private JPanel iocAttrPanel; 
    private JButton saveButton; 
    private JTable attrList;

    static RolePrincipal[] editPrincipal = { new RolePrincipal(RoleName.IOC_EDITOR) };

    private IOC selectedIoc;
    private APSIOC extendedIoc;
    private boolean extendedWrite = false;
    private List persons;
    private Person nonePerson;

    public APSIOCInfo() {
        // get some data we need
        PersonDAO personDAO = null;
        try {
            personDAO = new PersonDAO();
            persons = personDAO.findAllPersons();
        } catch (DAOException de) {
            de.printStackTrace(System.out);
        }        
        nonePerson = new Person();
        nonePerson.setFirstName("");
        nonePerson.setMiddleName("");
        nonePerson.setLastName("");
        nonePerson.setUserid("none");
    }

    /**
     * Returns a Swing JPanel which contains widgets for viewing/editing
     * any site-specific ioc information. A list of <code>IOC</code>
     * objects is supplied to allow lookup of the extended info using
     * the existing ioc id's. 
     *
     * @param iocList - list of <code>IOC</code> objects
     *
     * @return Swing JPanel ready for display
     */
    public JPanel createIOCInfoPanel(List iocList) {
        
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // ioc details panel containing ioc attribute info
        iocDetailsPanel = new JPanel();
        iocDetailsPanel.setLayout(new BoxLayout(iocDetailsPanel,BoxLayout.PAGE_AXIS));

        // ioc attributes panel
        iocAttrPanel = new JPanel(new BorderLayout());
        TitledBorder iocAttrTitle = BorderFactory.createTitledBorder("Extended IOC Info");
        iocAttrPanel.setBorder(iocAttrTitle);

        // ioc attributes table
        attrList = new JTable(new IOCAttrTableModel());
        attrList.setShowHorizontalLines(true);
        attrList.setCellSelectionEnabled(true);
        attrList.getColumnModel().getColumn(0).setMaxWidth(150);
        attrList.getColumnModel().getColumn(0).setPreferredWidth(150);
        attrList.setTableHeader(null);
        attrList.getColumnModel().getColumn(1).setCellRenderer(new IOCAttrValueTableCellRenderer());
        attrList.getColumnModel().getColumn(1).setCellEditor(new IOCAttrValueTableCellEditor());

    	JScrollPane attrListScroller = new JScrollPane(attrList, vsbp, hsbp);
        iocAttrPanel.add(attrListScroller);
        attrList.getColumnModel().getColumn(1).setPreferredWidth(50);
        attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // process mouse click on ioc name, bringing up idt::component view of it
        MouseListener attrListMouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    Point p = new Point(e.getX(), e.getY());
                    int row = attrList.rowAtPoint(p);
                    int col = attrList.columnAtPoint(p);
                    // ioc name is in row 0, column 1
                    if (row == 0 && col == 1) {
                        // if ioc has a component reference, look it up
                        gov.anl.aps.irmis.persistence.component.Component c = selectedIoc.getComponent();
                        if (c != null) {
                            Application app = Application.getApp();
                            List docs = app.getDocuments();
                            
                            // first see if it is already on desktop 
                            Iterator docIt = docs.iterator();
                            ComponentDocument cDoc = null;
                            while (docIt.hasNext()) {
                                XalInternalDocument doc = (XalInternalDocument)docIt.next();
                                if (doc.getTitle().equals("idt::component")) {
                                    cDoc = (ComponentDocument)doc;
                                    break;
                                }
                            }
                            if (cDoc == null) {
                                cDoc = new ComponentDocument();
                                app.produceDocument(cDoc);
                            }
                            
                            // now tell the doc to preselect for our component
                            cDoc.actionSelectComponent(c);            
                        }
                    }
                }
            };
        attrList.addMouseListener(attrListMouseListener);

        // attributes action button bar
        JPanel attrButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        saveButton = new JButton("Save");
        LoginUtil.registerProtectedComponent(saveButton, false, editPrincipal);
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (attrList.getCellEditor() != null)
                        attrList.getCellEditor().stopCellEditing();

                    if (extendedWrite) {
                        APSIOCDAO apsIOCDAO = null;
                        try {
                            apsIOCDAO = new APSIOCDAO();
                            apsIOCDAO.save(extendedIoc);
                            extendedWrite = false;
                            saveButton.setEnabled(false);
                        } catch (DAOException de) {
                            de.printStackTrace(System.out);
                        }
                    }
                }
            });

        // don't have save function yet
        attrButtons.add(saveButton);

        // add attribute subpanel to ioc details panel
        iocDetailsPanel.add(iocAttrPanel);
        iocDetailsPanel.add(attrButtons);

        return iocDetailsPanel;
    }

    /**
     * Externally select a given ioc. The created component (JPanel) should
     * display the extended info pertaining to this ioc.
     *
     * @param ioc - a <code>IOC</code> object
     */
    public void selectIOC(IOC ioc) {
        APSIOCDAO apsIOCDAO = null;

        // stop any cell editing that may be in progress
        if (attrList.getCellEditor() != null)
            attrList.getCellEditor().stopCellEditing();

        // This means we edited previous ioc extended info, but never saved it.
        // So we evict it from the cache, forcing re-read next time it is viewed.
        if (extendedWrite) {
            try {
                apsIOCDAO = new APSIOCDAO();
                apsIOCDAO.evict(extendedIoc);
            } catch (DAOException de) {
                de.printStackTrace(System.out);
            }            
        }
        selectedIoc = ioc;
        extendedIoc = null;

        if (selectedIoc != null) {
            // retrieve extended info from database
            // going straight to DAO instead of service
            try {
                apsIOCDAO = new APSIOCDAO();
                extendedIoc = apsIOCDAO.findByIoc(selectedIoc);
            } catch (DAOException de) {
                de.printStackTrace(System.out);
            }
        }
         
        saveButton.setEnabled(false);
        IOCAttrTableModel iocAttrTableModel = 
            (IOCAttrTableModel)attrList.getModel();
        iocAttrTableModel.fireTableDataChanged();
    }

    /**
     * A new APSIOC object representing extended info for a new generic IOC
     * will be created and persisted.
     */
    public void addNewExtendedIOC(IOC ioc) {
        if (ioc != null) {
            APSIOCDAO apsIOCDAO = null;
            try {
                apsIOCDAO = new APSIOCDAO();
                APSIOC newExtendedIOC = new APSIOC();
                newExtendedIOC.setIoc(ioc);
                apsIOCDAO.save(newExtendedIOC);
            } catch (DAOException de) {
                de.printStackTrace(System.out);
            }
        }
    }

    /**
     * Provides methods to get ioc attribute table cell data from actual
     * <code>IOCModel</code>. See Swing tutorial.
     */
    class IOCAttrTableModel extends AbstractTableModel {
        private String[] columnNames = {"Name","Value"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 28;
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {

            if (selectedIoc != null && extendedIoc != null) {
                switch (row) {
                case 0: {
                    if (col == 0) {
                        return "IOC Name";
                    } else {
                        return selectedIoc.getIocName();
                    }
                }
                case 1: {
                    if (col == 0) {
                        return "location";
                    } else {
                        return extendedIoc.getLocation();
                    }
                }
                case 2: {
                    if (col==0) {
                        return "Status";
                    } else {
                        return selectedIoc.getStatus().toString();
                    }
                }
                case 3: {
                    if (col == 0) {
                        return "TermServRackNo";
                    } else {
                        return extendedIoc.getTermServRackNo();
                    }
                }
                case 4: {
                    if (col == 0) {
                        return "TermServName";
                    } else {
                        return extendedIoc.getTermServName();
                    }
                }
                case 5: {
                    if (col == 0) {
                        return "TermServPort";
                    } else {
                        return Integer.toString(extendedIoc.getTermServPort());
                    }
                }
                case 6: {
                    if (col == 0) {
                        return "TermServFiberConvCh";
                    } else {
                        return extendedIoc.getTermServFiberConvCh();
                    }
                }
                case 7: {
                    if (col == 0) {
                        return "TermServFiberConvPort";
                    } else {
                        return Integer.toString(extendedIoc.getTermServFiberConvPort());
                    }
                }
                case 8: {
                    if (col == 0) {
                        return "PrimEnetSwRackNo";
                    } else {
                        return extendedIoc.getPrimEnetSwRackNo();
                    }
                }
                case 9: {
                    if (col == 0) {
                        return "PrimEnetSwitch";
                    } else {
                        return extendedIoc.getPrimEnetSwitch();
                    }
                }
                case 10: {
                    if (col == 0) {
                        return "PrimEnetBlade";
                    } else {
                        return extendedIoc.getPrimEnetBlade();
                    }
                }
                case 11: {
                    if (col == 0) {
                        return "PrimEnetPort";
                    } else {
                        return Integer.toString(extendedIoc.getPrimEnetPort());
                    }
                }
                case 12: {
                    if (col == 0) {
                        return "PrimEnetMedConvCh";
                    } else {
                        return extendedIoc.getPrimEnetMedConvCh();
                    }
                }
                case 13: {
                    if (col == 0) {
                        return "PrimMediaConvPort";
                    } else {
                        return Integer.toString(extendedIoc.getPrimMediaConvPort());
                    }
                }
                case 14: {
                    if (col == 0) {
                        return "SecEnetSwRackNo";
                    } else {
                        return extendedIoc.getSecEnetSwRackNo();
                    }
                }
                case 15: {
                    if (col == 0) {
                        return "SecEnetSwitch";
                    } else {
                        return extendedIoc.getSecEnetSwitch();
                    }
                }
                case 16: {
                    if (col == 0) {
                        return "SecEnetBlade";
                    } else {
                        return extendedIoc.getSecEnetBlade();
                    }
                }
                case 17: {
                    if (col == 0) {
                        return "SecEnetPort";
                    } else {
                        return Integer.toString(extendedIoc.getSecEnetPort());
                    }
                }
                case 18: {
                    if (col == 0) {
                        return "SecEnetMedConvCh";
                    } else {
                        return extendedIoc.getSecEnetMedConvCh();
                    }
                }
                case 19: {
                    if (col == 0) {
                        return "SecMedConvPort";
                    } else {
                        return Integer.toString(extendedIoc.getSecMedConvPort());
                    }
                }
                case 20: {
                    if (col == 0) {
                        return "general functions";
                    } else {
                        return extendedIoc.getGeneralFunctions();
                    }
                }
                case 21: {
                    if (col == 0) {
                        return "pre boot instr";
                    } else {
                        return extendedIoc.getPreBootInstr();
                    }
                }
                case 22: {
                    if (col == 0) {
                        return "post boot instr";
                    } else {
                        return extendedIoc.getPostBootInstr();
                    }
                }
                case 23: {
                    if (col == 0) {
                        return "power cycle caution";
                    } else {
                        return extendedIoc.getPowerCycleCaution();
                    }
                }
                case 24: {
                    if (col == 0) {
                        return "cog technician";
                    } else {
                        Person person = extendedIoc.getCogTechnician();
                        if (person == null)
                            return nonePerson;
                        else
                            return person;
                    }
                }
                case 25: {
                    if (col == 0) {
                        return "cog developer";
                    } else {
                        Person person = extendedIoc.getCogDeveloper();
                        if (person == null)
                            return nonePerson;
                        else
                            return person;
                    }
                }
                case 26: {
                    if (col == 0) {
                        return "sys reset required?";
                    } else {
                        if (extendedIoc.getSysResetRequired())
                            return "true";
                        else
                            return "false";
                    }
                }
                case 27: {
                    if (col == 0) {
                        return "inhibit auto reboot?";
                    } else {
                        if (extendedIoc.getInhibitAutoReboot())
                            return "true";
                        else
                            return "false";
                    }
                }

                default: {
                    return " ";
                }
                }
            } else {
                return "no selection";
            }

        }

        // called after edit of attribute cell is complete
        public void setValueAt(Object value, int row, int col) {
            switch (row) {
            case 0: {
                break;
            }
            case 1: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getLocation())) {
                    extendedIoc.setLocation(strValue);
                    extendedWrite = true;
                }
                break;
            }

            /* no editing here for row 2 - IOC status */

            case 3: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getTermServRackNo())) {
                    extendedIoc.setTermServRackNo(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 4: {
                String strValue = (String)value;                
                if (!strValue.equals(extendedIoc.getTermServName())) {
                    extendedIoc.setTermServName(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 5: {
                String strValue = (String)value;                
                int portNum = 0;
                try {
                    portNum = Integer.parseInt(strValue);
                    extendedIoc.setTermServPort(portNum);
                    extendedWrite = true;
                } catch (NumberFormatException bne) {
                }
                break;
            }
            case 6: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getTermServFiberConvCh())) {
                    extendedIoc.setTermServFiberConvCh(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 7: {
                String strValue = (String)value;
                int portNum = 0;
                try {
                    portNum = Integer.parseInt(strValue);
                    extendedIoc.setTermServFiberConvPort(portNum);
                    extendedWrite = true;
                } catch (NumberFormatException bne) {
                }
                break;
            }
            case 8: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getPrimEnetSwRackNo())) {
                    extendedIoc.setPrimEnetSwRackNo(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 9: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getPrimEnetSwitch())) {
                    extendedIoc.setPrimEnetSwitch(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 10: {
                String strValue = (String)value;                
                if (!strValue.equals(extendedIoc.getPrimEnetBlade())) {
                    extendedIoc.setPrimEnetBlade(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 11: {
                String strValue = (String)value;
                int portNum = 0;
                try {
                    portNum = Integer.parseInt(strValue);
                    extendedIoc.setPrimEnetPort(portNum);
                    extendedWrite = true;
                } catch (NumberFormatException bne) {
                }
                break;
            }
            case 12: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getPrimEnetMedConvCh())) {
                    extendedIoc.setPrimEnetMedConvCh(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 13: {
                String strValue = (String)value;
                int portNum = 0;
                try {
                    portNum = Integer.parseInt(strValue);
                    extendedIoc.setPrimMediaConvPort(portNum);
                    extendedWrite = true;
                } catch (NumberFormatException bne) {
                }
                break;
            }
            case 14: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getSecEnetSwRackNo())) {
                    extendedIoc.setSecEnetSwRackNo(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 15: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getSecEnetSwitch())) {
                    extendedIoc.setSecEnetSwitch(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 16: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getSecEnetBlade())) {
                    extendedIoc.setSecEnetBlade(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 17: {
                String strValue = (String)value;
                int portNum = 0;
                try {
                    portNum = Integer.parseInt(strValue);
                    extendedIoc.setSecEnetPort(portNum);
                    extendedWrite = true;
                } catch (NumberFormatException bne) {
                }
                break;
            }
            case 18: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getSecEnetMedConvCh())) {
                    extendedIoc.setSecEnetMedConvCh(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 19: {
                String strValue = (String)value;
                int portNum = 0;
                try {
                    portNum = Integer.parseInt(strValue);
                    extendedIoc.setSecMedConvPort(portNum);
                    extendedWrite = true;
                } catch (NumberFormatException bne) {
                }
                break;
            }
            case 20: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getGeneralFunctions())) {
                    extendedIoc.setGeneralFunctions(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 21: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getPreBootInstr())) {
                    extendedIoc.setPreBootInstr(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 22: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getPostBootInstr())) {
                    extendedIoc.setPostBootInstr(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 23: {
                String strValue = (String)value;
                if (!strValue.equals(extendedIoc.getPowerCycleCaution())) {
                    extendedIoc.setPowerCycleCaution(strValue);
                    extendedWrite = true;
                }
                break;
            }
            case 24: {
                Person personValue = (Person)value;
                if (personValue.equals(nonePerson))
                    extendedIoc.setCogTechnician(null);                    
                else
                    extendedIoc.setCogTechnician(personValue);
                extendedWrite = true;
                break;
            }
            case 25: {
                Person personValue = (Person)value;
                if (personValue.equals(nonePerson))
                    extendedIoc.setCogDeveloper(null);
                else
                    extendedIoc.setCogDeveloper(personValue);
                extendedWrite = true;
                break;
            }
            case 26: {
                String strValue = (String)value;
                if (strValue.equals("true"))
                    extendedIoc.setSysResetRequired(true);
                else
                    extendedIoc.setSysResetRequired(false);
                extendedWrite = true;
                break;
            }
            case 27: {
                String strValue = (String)value;
                if (strValue.equals("true"))
                    extendedIoc.setInhibitAutoReboot(true);
                else
                    extendedIoc.setInhibitAutoReboot(false);
                extendedWrite = true;
                break;
            }
                
            default: {
                break;
            }
            }
            if (LoginUtil.isPermitted(editPrincipal)) {
                saveButton.setEnabled(true);
            }
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0) {
                return false;
                
            } else if (col == 1) {
                // permit editing value only if user logged in with sufficient permissions
                if ((LoginUtil.isPermitted(editPrincipal) && row > 0 && row != 2))
                    return true;
                else 
                    return false;
            }
            return false;
        }
    }

    /**
     * Cell renderer for value column of ioc attribute table.
     */
    class IOCAttrValueTableCellRenderer 
        implements TableCellRenderer {
        
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int col) {
            
            JTextArea textArea = new JTextArea();
            // modify properties of text area here
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setToolTipText("Double-click to edit (you must be logged in)");
            
            if (value != null)
                textArea.setText(value.toString());
            
            if (isSelected) {
                textArea.setBackground(new Color(0xCC,0xCC,0xFF));
            } else {
                textArea.setBackground(Color.white);
            }
            if (row == 0)
                textArea.setForeground(Color.blue);
            else
                textArea.setForeground(Color.black);
            
            // autosize the row height to fit text exactly
            textArea.setSize(table.getColumnModel().getColumn(col).getWidth(), Integer.MAX_VALUE);
            int desiredHeight = (int) textArea.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return textArea;
        }
    }

    /**
     * Cell editor for ioc name column.
     */
    class IOCAttrValueTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {

        // text area editors for free text rows, one for each row
        Hashtable textAreaEditors;
        
        // combo boxes for person and true/false pulldowns
        JComboBox personCb = null; // for person select rows
        JComboBox tfCb = null;  // for true/false selection

        int rowEdited = 0;

        public IOCAttrValueTableCellEditor() {

            // initialize text areas for editing rows 0 through 23
            textAreaEditors = new Hashtable();
            for (int row=0 ; row < 24 ; row++) {
                JTextArea textArea = new JTextArea();
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.addKeyListener(new KeyAdapter() {
                        //final int r = row;
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextArea ta = (JTextArea)e.getComponent();
                            // resize table cell if user hits enter
                            if (keyChar == '\n') {
                                ta.setSize(attrList.getColumnModel().getColumn(1).getWidth(), 
                                           Integer.MAX_VALUE);
                                int desiredHeight = (int) ta.getPreferredSize().getHeight();
                                if (desiredHeight > attrList.getRowHeight(rowEdited))
                                    attrList.setRowHeight(rowEdited,desiredHeight);
                            }
                        }
                    });                
                textAreaEditors.put(new Integer(row), textArea);
            }
            
            // initialize person combo box for rows 24,25
            personCb = new JComboBox();
            personCb.setEditable(false);
            personCb.addItem(nonePerson);
            Iterator personIt = persons.iterator();
            while (personIt.hasNext()) {
                Person person = (Person)personIt.next();
                personCb.addItem(person);
            }
            personCb.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            stopCellEditing();
                        }
                    }
                });      

            // initialize true/false combo box for rows 26.27
            tfCb = new JComboBox();
            tfCb.setEditable(false);
            tfCb.addItem("true");
            tfCb.addItem("false");
            tfCb.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            stopCellEditing();
                        }
                    }
                });      
        }

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            rowEdited = row;

            if (row < 24) {
                JTextArea textArea = (JTextArea)textAreaEditors.get(new Integer(row));
                if (value != null)
                    textArea.setText(value.toString());
                if (isSelected) {
                    textArea.setBackground(new Color(0xCC,0xCC,0xFF));
                } else {
                    textArea.setBackground(Color.white);
                }
                textArea.setSize(table.getColumnModel().getColumn(col).getWidth(), 
                                 Integer.MAX_VALUE);
                int desiredHeight = (int) textArea.getPreferredSize().getHeight();
                if (desiredHeight > table.getRowHeight(row))
                    table.setRowHeight(row,desiredHeight);
                return textArea;

            } else if (row == 24 || row == 25) {
                if (value != null)
                    personCb.setSelectedItem((Person)value);
                personCb.setSize(table.getColumnModel().getColumn(col).getWidth(), 
                                 Integer.MAX_VALUE);
                int desiredHeight = (int) personCb.getPreferredSize().getHeight();
                if (desiredHeight > table.getRowHeight(row))
                    table.setRowHeight(row,desiredHeight);
                return personCb;

            } else {
                if (value != null)
                    tfCb.setSelectedItem(value.toString());
                tfCb.setSize(table.getColumnModel().getColumn(col).getWidth(), 
                             Integer.MAX_VALUE);
                int desiredHeight = (int) tfCb.getPreferredSize().getHeight();
                if (desiredHeight > table.getRowHeight(row))
                    table.setRowHeight(row,desiredHeight);
                return tfCb;
            }
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            if (rowEdited < 24) {
                JTextArea textArea = (JTextArea)textAreaEditors.get(new Integer(rowEdited));
                return textArea.getText();
            } else if (rowEdited == 24 || rowEdited == 25) {
                return personCb.getSelectedItem();
            } else {
                return tfCb.getSelectedItem();
            }
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent)e;                
                int x = me.getX();
                int y = me.getY();
                int row = attrList.rowAtPoint(new Point(x, y));
                if (row > 0 && me.getClickCount() > 1) {
                    saveButton.setEnabled(true);
                    return true;
                }
            }
            return false;
        }

    }    

}
