/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.util.Iterator;
import java.util.EventObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.Application;

import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.admin.cfw.NewPersonDialog;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.pv.URI;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeDocument;
import gov.anl.aps.irmis.persistence.component.ComponentTypePerson;

// IRMIS service layer
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Step03 gets a variety of general info about the new component type, such
 * as cognitive person, documentation url's.
 */
public class NewTypeWizardStep03 extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable docList;
    private List docs;  // needed to temporariliy hold Set as List for gui
    private JComboBox cpCb;
    private ComponentTypeDocument selectedDocument;
    private int selectedRow;

    public NewTypeWizardStep03(NewTypeWizardModel dataModel) {
        super("General Info 2","desc");
        docs = new ArrayList();
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);

        // component type name
        JLabel nameLabel = 
            new JLabel(dataModel.getComponentType().getComponentTypeName(),
                       SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        stepComponent.add(nameLabelPanel);

        // cognitive person pulldown
        JLabel cpLabel = new JLabel("Cognitive Person:",SwingConstants.LEFT);
        JPanel cpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cpPanel.setBackground(Color.white);
        cpPanel.add(cpLabel);
        cpCb = new JComboBox();
        cpCb.removeAllItems();
        cpCb.addItem("New Person...");
        Iterator pIt = dataModel.getPersons().iterator();
        while (pIt.hasNext()) {
            Person p = (Person)pIt.next();
            cpCb.addItem(p);
        }
        cpCb.setSelectedIndex(0);
        cpPanel.add(cpCb);
        stepComponent.add(cpPanel);

        // handle new person selection in pulldown
        cpCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (e.getItem() instanceof String) {
                            Person person = NewPersonDialog.showDialog(stepComponent, null);
                            if (person != null) {
                                List personList = dataModel.getPersons();
                                if (personList.contains(person)) {
                                    Application.displayWarning("Warning","Person with this userid already exists.");
                                } else {
                                    try {
                                        PersonService.savePerson(person);
                                        personList.add(person);
                                        Collections.sort(personList);
                                        cpCb.addItem(person);
                                        cpCb.setSelectedItem(person);
                                    } catch (IRMISException ie) {
                                        ie.printStackTrace();
                                    }
                                }
                            }
                        } 
                    }
                }
            });        
        cpCb.setSelectedIndex(1);

        // variable number of documents
        JLabel docLabel = 
            new JLabel("Documents:",SwingConstants.LEFT);
        JPanel docLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        docLabelPanel.setBackground(Color.white);
        docLabelPanel.add(docLabel);
        stepComponent.add(docLabelPanel);

        // documents table
        docList = new JTable(new DocumentTableModel());
        docList.setShowHorizontalLines(true);
        docList.setRowSelectionAllowed(true);
        docList.getColumnModel().getColumn(0).setMaxWidth(110);
        docList.getColumnModel().getColumn(0).setPreferredWidth(110);        
        docList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        docList.getColumnModel().getColumn(0).setCellEditor(new DocumentTypeTableCellEditor());
        docList.getColumnModel().getColumn(1).setCellEditor(new DocumentTableCellEditor());

        // handle row selection in documents table
        docList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int row = lsm.getMinSelectionIndex();
                        selectedRow = row;
                        selectedDocument = (ComponentTypeDocument)docs.get(row);
                    }
                }
            });

    	JScrollPane docListScroller = new JScrollPane(docList, vsbp, hsbp);
        stepComponent.add(docListScroller);

        // document table action button bar
        JPanel docButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton docAddButton = new JButton("Add");
        docAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (docList.getCellEditor() != null)
                        docList.getCellEditor().stopCellEditing();
                    // create new document row in the table
                    ComponentTypeDocument newDoc = new ComponentTypeDocument();
                    URI newUri = new URI();
                    newUri.setUri("");
                    newDoc.setUri(newUri);
                    newDoc.setDocumentType("feature sheet");

                    selectedDocument = newDoc;
                    docs.add(newDoc);

                    int row = docs.size() - 1;
                    DocumentTableModel documentTableModel = 
                        (DocumentTableModel)docList.getModel();
                    documentTableModel.fireTableRowsInserted(row, row);
                    docList.editCellAt(row,0);
                }
            });

        JButton docRemoveButton = new JButton("Remove");
        docRemoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedDocument != null) {
                        if (docList.getCellEditor() != null)
                            docList.getCellEditor().stopCellEditing();
                        docs.remove(selectedDocument);
                        DocumentTableModel documentTableModel = 
                            (DocumentTableModel)docList.getModel();
                        documentTableModel.fireTableRowsDeleted(selectedRow,selectedRow);
                        documentTableModel.fireTableDataChanged();
                    }
                }
            });

        JButton docTestButton = new JButton("Test URL...");
        docTestButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedDocument != null) {
                        if (docList.getCellEditor() != null)
                            docList.getCellEditor().stopCellEditing();

                        AppsUtil.showURL(stepComponent, selectedDocument.getUri().getUri());
                    }
                }
            });

        docButtons.add(docAddButton);
        docButtons.add(docRemoveButton);
        docButtons.add(docTestButton);
        stepComponent.add(docButtons);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        // pre-load components with any choices that have been made already
        if (stepComponent != null) {
            ComponentType ct = dataModel.getComponentType();
            ComponentTypePerson cogPerson = null;
            if (ct.getComponentTypePersons().size() > 0) {
                // just grab the one entry for now, since we don't yet support multiple
                Iterator it = ct.getComponentTypePersons().iterator();
                cogPerson = (ComponentTypePerson)it.next();
                cpCb.setSelectedItem(cogPerson.getPerson());
            }

            // create list from set for temporary use by gui
            docs = new ArrayList(dataModel.getComponentType().getComponentTypeDocuments());
        }
    }

    public Step[] getPendingSteps() {
        ComponentType ct = dataModel.getComponentType();

        if (docList.getCellEditor() != null)
            docList.getCellEditor().stopCellEditing();

        // transfer data from widgets to dataModel
        ct.clearComponentTypePersons();
        ComponentTypePerson newCtp = new ComponentTypePerson();
        newCtp.setPerson((Person)cpCb.getSelectedItem());
        newCtp.setRoleName(dataModel.getCognitiveRoleName());        
        ct.addComponentTypePerson(newCtp);

        // clear doc set and re-fill from docs list
        ct.clearComponentTypeDocuments();
        Iterator it = docs.iterator();
        while (it.hasNext()) {
            ComponentTypeDocument ctd = (ComponentTypeDocument)it.next();
            ct.addComponentTypeDocument(ctd);
        }

        // return the next step that should follow
        return new Step[] {new NewTypeWizardStep03a(dataModel)};
    }

    /**
     * Model for display/editing of component type document table.
     */
    class DocumentTableModel extends AbstractTableModel {
        private String[] columnNames = {"Type","Document"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (docs != null)
                size = docs.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {

            if (docs != null) {
                ComponentTypeDocument doc = (ComponentTypeDocument)docs.get(row);
                if (col == 0) {
                    return doc.getDocumentType();
                } else {
                    return doc.getUri().getUri();
                }
            } else {
                return " ";
            }

        }

        // called after edit of attribute cell is complete
        public void setValueAt(Object value, int row, int col) {
            String strValue = (String)value;
            
            if (docs != null) {
                ComponentTypeDocument doc = (ComponentTypeDocument)docs.get(row);
                if (col == 0) {  // document type
                    doc.setDocumentType(strValue);

                } else if (col == 1) {  // document uri
                    doc.getUri().setUri(strValue);
                }
            }

        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }
    }

    /**
     * Cell editor for document type column.
     */
    class DocumentTypeTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {

        JComboBox cb = null;

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            String valueStr = (String)value;
            if (cb == null) {
                cb = new JComboBox();

                // set up combo box here
                cb.setEditable(false);
                
                // capture enter key so we don't go to next wizard step
                /*
                  cb.getEditor().addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                  String editValue = e.getActionCommand();
                  cb.setSelectedItem(editValue);
                  stopCellEditing();
                  }
                  });
                */
                
                cb.addItem("feature sheet");
                cb.addItem("quick ref");
                cb.addItem("eng doc");
                cb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                stopCellEditing();
                            }
                        }
                    });      
            }  
            cb.setSelectedItem(valueStr);

            // autosize the row height to fit combo box better
            int desiredHeight = (int) cb.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return cb;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            
            String selectedValue = (String)cb.getSelectedItem();
            return selectedValue;
        }
        
        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }


    /**
     * Cell editor for document column.
     */
    class DocumentTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {
        
        JTextField textField = new JTextField();

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            textField.setEditable(true);
            textField.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char keyChar = e.getKeyChar();

                        if (keyChar == '\n') {
                            // not a good way to get rid of newline,
                            // but I can't figure out how to stop
                            // this from getting entered in text area
                            int cp = textField.getCaretPosition();
                            Document doc = textField.getDocument();
                            try {
                                doc.remove(textField.getText().length()-1,1);
                            } catch (BadLocationException ble) {}
                            stopCellEditing();
                        }
                    }
                });
            
            // put the data in the text field
            if (value != null)
                textField.setText(value.toString());
            
            return textField;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return textField.getText();
        }

        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }
}
