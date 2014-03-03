/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Collections;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.*;

// jwizz wizard toolkit
import net.javaprog.ui.wizard.*;

// new component wizard
import gov.anl.aps.irmis.apps.component.cfw.wizard.NewComponentWizard;
import gov.anl.aps.irmis.apps.component.cfw.wizard.NewComponentWizardModel;

// persistence layer
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;
import gov.anl.aps.irmis.persistence.SemaphoreValue;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.APSComponent;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.ComponentTypePerson;
import gov.anl.aps.irmis.persistence.component.ComponentTypeFunction;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.login.GroupName;

// for registering protected gui components
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.login.RolePrincipal;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

/**
 * Primary GUI for IRMIS Component Viewer application. All window layout, and Swing event
 * listeners are done here. Business logic work is delegated to <code>ComponentDocument</code>,
 * which in turn requests that the ComponentModel notify us here of changes to the data. In short,
 * we listen for <code>ComponentModelEvent</code> here.
 */
public class ComponentWindow extends XalInternalWindow {

	/** The main model for the document */
	final protected ComponentModel _model;

    final protected ComponentDocument document;

    static RolePrincipal[] editPrincipal = { new RolePrincipal(RoleName.COMPONENT_EDITOR) };

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;    

	// Swing GUI components
    private JPanel topPanel;
    private JPanel locationPanel;
    private JPanel housingPanel;
    private JPanel controlPanel;
    private JPanel powerPanel;
    private JPanel selectionInfoPanel;
    private JPanel selectionPanel;
    private JPanel infoPanel;
    private JPanel dndPanel;
    private JPanel selectionButtonsPanel;
    private JPanel dndButtonsPanel;

    private JButton newButton;
    private JButton commitButton;
    private JButton clearButton;

    private JLabel dndLabel;

    private JTree focusedTree; // which tree was just clicked on

    // housing
    private JTree housingTree;
    private JPanel housingNavPanel;
    private JPanel housingCommitPanel;
    private JLabel housingSpinnerLabel;
    private JTextField housingTextField;
    private LogicalDescriptionKeyAdapter housingKeyAdapter;
    private JButton housingStopButton;
    private JButton housingFindButton;
    private JButton housingPrevButton;
    private JButton housingNextButton;
    private JButton housingUpButton;
    private JButton housingDownButton;
    private JTextField housingSearchField;
    private JCheckBox housingVerifiedCheckBox;
    private FilteredHousingComponentTreeModel housingTreeModel;
    private ComponentTreeFindController housingFindController;

    // control
    private JTree controlTree;
    private JPanel controlNavPanel;
    private JPanel controlCommitPanel;
    private JLabel controlSpinnerLabel;
    private JTextField controlTextField;
    private LogicalDescriptionKeyAdapter controlKeyAdapter;
    private JButton controlStopButton;
    private JButton controlFindButton;
    private JButton controlPrevButton;
    private JButton controlNextButton;
    private JButton controlUpButton;
    private JButton controlDownButton;
    private JTextField controlSearchField;
    private JCheckBox controlVerifiedCheckBox;
    private FilteredControlComponentTreeModel controlTreeModel;
    private ComponentTreeFindController controlFindController;

    // power
    private JTree powerTree;
    private JPanel powerNavPanel;
    private JPanel powerCommitPanel;
    private JLabel powerSpinnerLabel;
    private JTextField powerTextField;
    private LogicalDescriptionKeyAdapter powerKeyAdapter;
    private JButton powerStopButton;
    private JButton powerFindButton;
    private JButton powerPrevButton;
    private JButton powerNextButton;
    private JButton powerUpButton;
    private JButton powerDownButton;
    private JTextField powerSearchField;
    private JCheckBox powerVerifiedCheckBox;
    private ComponentTreeModel powerTreeModel;
    private ComponentTreeFindController powerFindController;

    private CompTypeAttrTableModel attrListModel;
    private JTable attrList;

    // local constants
    final int housingRelationshipType = ComponentRelationshipType.HOUSING;

	
	/** 
	 * Creates a new instance of ComponentWindow
	 * @param aDocument The document for this window
	 */
    public ComponentWindow(final XalInternalDocument aDocument) {
        super(aDocument);

        document = (ComponentDocument)aDocument;
		_model = ((ComponentDocument)aDocument).getModel();

        // make ComponentWindow a listener for changes in ComponentModel
		_model.addComponentModelListener( new ComponentModelListener() {
                public void modified(ComponentModelEvent e) {
                    updateView(e);
                }
            });

       _appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();

        // initial application window size
        setSize(775, 500);

        // build contents
		makeContents();
    }

    /**
     * Top-level method to build up Swing GUI components.
     */
    public void makeContents() {

        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // topmost organization 
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.PAGE_AXIS));

        // component location panel
        locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel,BoxLayout.LINE_AXIS));
        TitledBorder locationTitle = BorderFactory.createTitledBorder("Component Locator");
        locationPanel.setBorder(locationTitle);

        // fill out housing/control/power sub-panels
        // group housing/control into a panel so we can split-pane with power
        JPanel housingControlPanel = new JPanel();
        housingControlPanel.setLayout(new BoxLayout(housingControlPanel,BoxLayout.LINE_AXIS));

        /***********************************************
         **************** HOUSING TREE *****************
         ***********************************************/
        housingPanel = new JPanel();
        housingPanel.setLayout(new BoxLayout(housingPanel,BoxLayout.PAGE_AXIS));
        TitledBorder housingTitle = BorderFactory.createTitledBorder("Housing");
        housingPanel.setBorder(housingTitle);
        housingTreeModel = new FilteredHousingComponentTreeModel();
        housingTree = new JTree(housingTreeModel);
        housingTree.setCellRenderer(new ComponentTreeCellRenderer(housingRelationshipType));

        // construct other 2 trees here since we need them
        controlTree = new JTree();
        powerTree = new JTree();

        // re-usable configurations
        Dimension smallButtonDim = new Dimension(20,20);

        // nav
        housingNavPanel = new JPanel();
        housingNavPanel.setLayout(new BoxLayout(housingNavPanel, BoxLayout.LINE_AXIS));
        housingNavPanel.setMinimumSize(new Dimension(100,25));
        housingNavPanel.setPreferredSize(new Dimension(100,25));
        housingNavPanel.setMaximumSize(new Dimension(500,25));
        JLabel housingNavLabel = new JLabel("Find:",SwingConstants.LEFT);
        housingStopButton = new JButton();
        housingStopButton.setMaximumSize(smallButtonDim);
        housingStopButton.setPreferredSize(smallButtonDim);
        housingFindButton = new JButton();
        housingFindButton.setMaximumSize(smallButtonDim);
        housingFindButton.setPreferredSize(smallButtonDim);
        housingPrevButton = new JButton();
        housingPrevButton.setMaximumSize(smallButtonDim);
        housingPrevButton.setPreferredSize(smallButtonDim);
        housingNextButton = new JButton();
        housingNextButton.setMaximumSize(smallButtonDim);
        housingNextButton.setPreferredSize(smallButtonDim);
        housingSearchField = new JTextField();
        housingSearchField.setMaximumSize(new Dimension(100,20));
        housingNavPanel.add(housingStopButton);
        housingNavPanel.add(housingSearchField);
        housingNavPanel.add(housingPrevButton);
        housingNavPanel.add(housingNextButton);
        housingNavPanel.add(housingFindButton);
        housingPanel.add(housingNavPanel);
        housingFindController = 
            new ComponentTreeFindController(_appFrame, housingTree, housingRelationshipType,
                                            _model, housingStopButton, housingFindButton,
                                            housingPrevButton, housingNextButton, 
                                            housingSearchField);

        // scrolled tree
        JScrollPane housingTreeScroller = 
            new JScrollPane(housingTree, vsbp, hsbp);
        housingPanel.add(housingTreeScroller);

        // logical desc (Slot) and logical order 
        housingCommitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        housingCommitPanel.setPreferredSize(new Dimension(300,25));
        housingCommitPanel.setMaximumSize(new Dimension(300,25));
        housingSpinnerLabel = new JLabel("Locator:");
        housingTextField = new JTextField(8);
        housingTextField.setToolTipText("Physical location relative to parent, such as slot #.");
        housingTextField.setEditable(false);
        housingKeyAdapter = 
            new LogicalDescriptionKeyAdapter(housingRelationshipType, housingTextField);
        housingTextField.addKeyListener(housingKeyAdapter);
        housingUpButton = new JButton();
        housingUpButton.setMaximumSize(smallButtonDim);
        housingUpButton.setPreferredSize(smallButtonDim);
        housingDownButton = new JButton();
        housingDownButton.setMaximumSize(smallButtonDim);
        housingDownButton.setPreferredSize(smallButtonDim);
        housingVerifiedCheckBox = new JCheckBox();
        housingVerifiedCheckBox.setEnabled(false);
        housingVerifiedCheckBox.setToolTipText("Not verified");
        housingVerifiedCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBox cb = (JCheckBox)e.getSource();
                    boolean selected = cb.isSelected();
                    if (selected) {
                        String username = LoginUtil.getUsername();
                        document.actionSetVerifiedPerson(housingRelationshipType, username);
                        housingVerifiedCheckBox.setToolTipText("Verified by "+username);

                    } else {
                        document.actionSetVerifiedPerson(housingRelationshipType, null);
                        housingVerifiedCheckBox.setToolTipText("Not verified");
                    }
                }
            });
        housingCommitPanel.add(housingSpinnerLabel);
        housingCommitPanel.add(housingTextField);
        housingCommitPanel.add(housingDownButton);
        housingCommitPanel.add(housingUpButton);
        housingCommitPanel.add(housingVerifiedCheckBox);
        housingPanel.add(housingCommitPanel);

        housingTree.setVisibleRowCount(15);
        housingTree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        housingTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Component leadSelection = (Component)leadPath.getLastPathComponent();
                        focusedTree = housingTree;
                        // do selection work, updating other 2 trees to match
                        setHierarchySelection(leadSelection, false, housingRelationshipType, 
                                              housingTextField, housingDownButton, housingUpButton,
                                              housingVerifiedCheckBox, housingTree, controlTree, powerTree);
                    }
                }
            });
        MouseListener housingMouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        housingTree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Component leadSelection = (Component)leadPath.getLastPathComponent();
                        focusedTree = housingTree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other 2 trees to match
                            setHierarchySelection(leadSelection, false,
                                                  housingRelationshipType, 
                                                  housingTextField, housingDownButton, 
                                                  housingUpButton, housingVerifiedCheckBox,
                                                  housingTree,controlTree,powerTree);
                        } else if (e.getClickCount() == 2) {
                            // ditto, but open all subtrees under selected component
                            setHierarchySelection(leadSelection, true,
                                                  housingRelationshipType, 
                                                  housingTextField, housingDownButton, 
                                                  housingUpButton, housingVerifiedCheckBox,
                                                  housingTree, controlTree, powerTree);
                        }
                    }
                }
            };
        housingTree.addMouseListener(housingMouseListener);

        // assign behavior to ctrl-up and ctrl-down keys, and up/down buttons
        KeyStroke ctrlUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP,InputEvent.CTRL_MASK);
        KeyStroke ctrlDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,InputEvent.CTRL_MASK);

        Action relocateHousingUpAction = 
            new RelocateComponentAction(housingTree, housingRelationshipType,true);
        Action relocateHousingDownAction = 
            new RelocateComponentAction(housingTree, housingRelationshipType,false);
        housingTree.getInputMap().put(ctrlUp, "relocateUp");
        housingTree.getActionMap().put("relocateUp", relocateHousingUpAction);
        housingTree.getInputMap().put(ctrlDown, "relocateDown");
        housingTree.getActionMap().put("relocateDown", relocateHousingDownAction);
        housingUpButton.setAction(relocateHousingUpAction);
        housingUpButton.setEnabled(false);
        housingDownButton.setAction(relocateHousingDownAction);
        housingDownButton.setEnabled(false);

        ComponentPopupMenu housingPopupMenu = 
            new ComponentPopupMenu(housingRelationshipType, housingFindController);
        housingTree.addMouseListener(new PopupTrigger(housingTree, controlTree, 
                                                      powerTree, housingPopupMenu));

        /***********************************************
         **************** CONTROL TREE *****************
         ***********************************************/
        final int controlRelationshipType = ComponentRelationshipType.CONTROL;
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.PAGE_AXIS));
        TitledBorder controlTitle = BorderFactory.createTitledBorder("Control");
        controlPanel.setBorder(controlTitle);
        controlTreeModel = new FilteredControlComponentTreeModel();
        controlTree.setModel(controlTreeModel);
        controlTree.setCellRenderer(new ComponentTreeCellRenderer(controlRelationshipType));

        // nav
        controlNavPanel = new JPanel();
        controlNavPanel.setLayout(new BoxLayout(controlNavPanel, BoxLayout.LINE_AXIS));
        controlNavPanel.setMinimumSize(new Dimension(100,25));
        controlNavPanel.setPreferredSize(new Dimension(100,25));
        controlNavPanel.setMaximumSize(new Dimension(500,25));
        JLabel controlNavLabel = new JLabel("Find:",SwingConstants.LEFT);
        controlStopButton = new JButton();
        controlStopButton.setMaximumSize(smallButtonDim);
        controlStopButton.setPreferredSize(smallButtonDim);
        controlFindButton = new JButton();
        controlFindButton.setMaximumSize(smallButtonDim);
        controlFindButton.setPreferredSize(smallButtonDim);
        controlPrevButton = new JButton();
        controlPrevButton.setMaximumSize(smallButtonDim);
        controlPrevButton.setPreferredSize(smallButtonDim);
        controlNextButton = new JButton();
        controlNextButton.setMaximumSize(smallButtonDim);
        controlNextButton.setPreferredSize(smallButtonDim);
        controlSearchField = new JTextField();
        controlSearchField.setMaximumSize(new Dimension(100,20));
        controlNavPanel.add(controlStopButton);
        controlNavPanel.add(controlSearchField);
        controlNavPanel.add(controlPrevButton);
        controlNavPanel.add(controlNextButton);
        controlNavPanel.add(controlFindButton);
        controlPanel.add(controlNavPanel);
        controlFindController = 
            new ComponentTreeFindController(_appFrame, controlTree, controlRelationshipType,
                                            _model, controlStopButton, controlFindButton,
                                            controlPrevButton, controlNextButton,
                                            controlSearchField);

        // scrolled tree
        JScrollPane controlTreeScroller = 
            new JScrollPane(controlTree, vsbp, hsbp);
        controlPanel.add(controlTreeScroller);

        // logical desc (Card) and logical order 
        controlCommitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlCommitPanel.setPreferredSize(new Dimension(300,25));
        controlCommitPanel.setMaximumSize(new Dimension(300,25));
        controlSpinnerLabel = new JLabel("Logical Addr:");
        controlTextField = new JTextField(8);
        controlTextField.setToolTipText("Logical address such as card #, host name, or link/port #.");
        controlTextField.setEditable(false);
        controlKeyAdapter = 
            new LogicalDescriptionKeyAdapter(controlRelationshipType, controlTextField);
        controlTextField.addKeyListener(controlKeyAdapter);
        controlUpButton = new JButton();
        controlUpButton.setMaximumSize(smallButtonDim);
        controlUpButton.setPreferredSize(smallButtonDim);
        controlDownButton = new JButton();
        controlDownButton.setMaximumSize(smallButtonDim);
        controlDownButton.setPreferredSize(smallButtonDim);
        controlVerifiedCheckBox = new JCheckBox();
        controlVerifiedCheckBox.setEnabled(false);
        controlVerifiedCheckBox.setToolTipText("Not verified");
        controlVerifiedCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBox cb = (JCheckBox)e.getSource();
                    boolean selected = cb.isSelected();
                    if (selected) {
                        String username = LoginUtil.getUsername();
                        document.actionSetVerifiedPerson(controlRelationshipType, username);
                        controlVerifiedCheckBox.setToolTipText("Verified by "+username);

                    } else {
                        document.actionSetVerifiedPerson(controlRelationshipType, null);
                        controlVerifiedCheckBox.setToolTipText("Not verified");
                    }
                }
            });
        controlCommitPanel.add(controlSpinnerLabel);
        controlCommitPanel.add(controlTextField);
        controlCommitPanel.add(controlDownButton);
        controlCommitPanel.add(controlUpButton);
        controlCommitPanel.add(controlVerifiedCheckBox);
        controlPanel.add(controlCommitPanel);

        controlTree.setVisibleRowCount(15);
        controlTree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        controlTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Component leadSelection = (Component)leadPath.getLastPathComponent();
                        focusedTree = controlTree;
                        // do selection work, updating other 2 trees to match
                        setHierarchySelection(leadSelection, false, controlRelationshipType, 
                                              controlTextField, controlDownButton, controlUpButton,
                                              controlVerifiedCheckBox, controlTree, housingTree, powerTree);
                    }
                }
            });
        MouseListener controlMouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        controlTree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Component leadSelection = (Component)leadPath.getLastPathComponent();
                        focusedTree = controlTree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other 2 trees to match
                            setHierarchySelection(leadSelection, false,
                                                  controlRelationshipType,
                                                  controlTextField, controlDownButton, 
                                                  controlUpButton, controlVerifiedCheckBox,
                                                  controlTree, housingTree, powerTree);
                        } else if (e.getClickCount() == 2) {
                            // ditto, but open all subtrees under selected component
                            setHierarchySelection(leadSelection, true,
                                                  controlRelationshipType, 
                                                  controlTextField, controlDownButton, 
                                                  controlUpButton, controlVerifiedCheckBox,
                                                  controlTree, housingTree, powerTree);
                        }
                    }
                }
            };
        controlTree.addMouseListener(controlMouseListener);

        Action relocateControlUpAction = 
            new RelocateComponentAction(controlTree, controlRelationshipType,true);
        Action relocateControlDownAction = 
            new RelocateComponentAction(controlTree, controlRelationshipType,false);
        controlTree.getInputMap().put(ctrlUp, "relocateUp");
        controlTree.getActionMap().put("relocateUp", relocateControlUpAction);
        controlTree.getInputMap().put(ctrlDown, "relocateDown");
        controlTree.getActionMap().put("relocateDown", relocateControlDownAction);
        controlUpButton.setAction(relocateControlUpAction);
        controlUpButton.setEnabled(false);
        controlDownButton.setAction(relocateControlDownAction);
        controlDownButton.setEnabled(false);

        ComponentPopupMenu controlPopupMenu = 
            new ComponentPopupMenu(controlRelationshipType, controlFindController);
        controlTree.addMouseListener(new PopupTrigger(controlTree, housingTree, 
                                                      powerTree, controlPopupMenu));


        /***********************************************
         **************** POWER TREE *******************
         ***********************************************/
        final int powerRelationshipType = ComponentRelationshipType.POWER;
        powerPanel = new JPanel();
        powerPanel.setLayout(new BoxLayout(powerPanel,BoxLayout.PAGE_AXIS));
        TitledBorder powerTitle = BorderFactory.createTitledBorder("Power");
        powerPanel.setBorder(powerTitle);
        powerTreeModel = new ComponentTreeModel(powerRelationshipType);
        powerTree.setModel(powerTreeModel);
        powerTree.setCellRenderer(new ComponentTreeCellRenderer(powerRelationshipType));

        // nav
        powerNavPanel = new JPanel();
        powerNavPanel.setLayout(new BoxLayout(powerNavPanel, BoxLayout.LINE_AXIS));
        powerNavPanel.setMinimumSize(new Dimension(100,25));
        powerNavPanel.setPreferredSize(new Dimension(100,25));
        powerNavPanel.setMaximumSize(new Dimension(500,25));
        JLabel powerNavLabel = new JLabel("Find:",SwingConstants.LEFT);
        powerStopButton = new JButton();
        powerStopButton.setMaximumSize(smallButtonDim);
        powerStopButton.setPreferredSize(smallButtonDim);
        powerFindButton = new JButton();
        powerFindButton.setMaximumSize(smallButtonDim);
        powerFindButton.setPreferredSize(smallButtonDim);
        powerPrevButton = new JButton();
        powerPrevButton.setMaximumSize(smallButtonDim);
        powerPrevButton.setPreferredSize(smallButtonDim);
        powerNextButton = new JButton();
        powerNextButton.setMaximumSize(smallButtonDim);
        powerNextButton.setPreferredSize(smallButtonDim);
        powerSearchField = new JTextField();
        powerSearchField.setMaximumSize(new Dimension(100,20));
        powerNavPanel.add(powerStopButton);
        powerNavPanel.add(powerSearchField);
        powerNavPanel.add(powerPrevButton);
        powerNavPanel.add(powerNextButton);
        powerNavPanel.add(powerFindButton);
        powerPanel.add(powerNavPanel);
        powerFindController = 
            new ComponentTreeFindController(_appFrame, powerTree, powerRelationshipType,
                                            _model, powerStopButton, powerFindButton,
                                            powerPrevButton, powerNextButton,
                                            powerSearchField);

        // scrolled tree
        JScrollPane powerTreeScroller = 
            new JScrollPane(powerTree, vsbp, hsbp);
        powerPanel.add(powerTreeScroller);

        // logical desc (Outlet) and logical order 
        powerCommitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        powerCommitPanel.setPreferredSize(new Dimension(300,25));
        powerCommitPanel.setMaximumSize(new Dimension(300,25));
        powerSpinnerLabel = new JLabel("Outlet:");
        powerTextField = new JTextField(7);
        powerTextField.setToolTipText("Location where plugged into parent, such as outlet #, or breaker slot.");
        powerTextField.setEditable(false);
        powerKeyAdapter = 
            new LogicalDescriptionKeyAdapter(powerRelationshipType, powerTextField);
        powerTextField.addKeyListener(powerKeyAdapter);
        powerUpButton = new JButton();
        powerUpButton.setMaximumSize(smallButtonDim);
        powerUpButton.setPreferredSize(smallButtonDim);
        powerDownButton = new JButton();
        powerDownButton.setMaximumSize(smallButtonDim);
        powerDownButton.setPreferredSize(smallButtonDim);
        powerVerifiedCheckBox = new JCheckBox();
        powerVerifiedCheckBox.setEnabled(false);
        powerVerifiedCheckBox.setToolTipText("Not verified");
        powerVerifiedCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBox cb = (JCheckBox)e.getSource();
                    boolean selected = cb.isSelected();
                    if (selected) {
                        String username = LoginUtil.getUsername();
                        document.actionSetVerifiedPerson(powerRelationshipType, username);
                        powerVerifiedCheckBox.setToolTipText("Verified by "+username);

                    } else {
                        document.actionSetVerifiedPerson(powerRelationshipType, null);
                        powerVerifiedCheckBox.setToolTipText("Not verified");
                    }
                }
            });
        powerCommitPanel.add(powerSpinnerLabel);
        powerCommitPanel.add(powerTextField);
        powerCommitPanel.add(powerDownButton);
        powerCommitPanel.add(powerUpButton);
        powerCommitPanel.add(powerVerifiedCheckBox);
        powerPanel.add(powerCommitPanel);

        powerTree.setVisibleRowCount(15);
        powerTree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        powerTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Component leadSelection = (Component)leadPath.getLastPathComponent();
                        focusedTree = powerTree;
                        // do selection work, updating other 2 trees to match
                        setHierarchySelection(leadSelection, false, powerRelationshipType, 
                                              powerTextField, powerDownButton, powerUpButton,
                                              powerVerifiedCheckBox, powerTree, housingTree, controlTree);
                    }
                }
            });
        MouseListener powerMouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        powerTree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Component leadSelection = (Component)leadPath.getLastPathComponent();
                        focusedTree = powerTree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other 2 trees to match
                            setHierarchySelection(leadSelection, false,
                                                  powerRelationshipType,
                                                  powerTextField, powerDownButton, 
                                                  powerUpButton, powerVerifiedCheckBox,
                                                  powerTree, housingTree, controlTree);
                        } else if (e.getClickCount() == 2) {
                            // ditto, but open all subtrees under selected component
                            setHierarchySelection(leadSelection, true,
                                                  powerRelationshipType, 
                                                  powerTextField, powerDownButton, 
                                                  powerUpButton, powerVerifiedCheckBox,
                                                  powerTree, housingTree, controlTree);
                        }
                    }
                }
            };
        powerTree.addMouseListener(powerMouseListener);

        Action relocatePowerUpAction = 
            new RelocateComponentAction(powerTree, powerRelationshipType,true);
        Action relocatePowerDownAction = 
            new RelocateComponentAction(powerTree, powerRelationshipType,false);
        powerTree.getInputMap().put(ctrlUp, "relocateUp");
        powerTree.getActionMap().put("relocateUp", relocatePowerUpAction);
        powerTree.getInputMap().put(ctrlDown, "relocateDown");
        powerTree.getActionMap().put("relocateDown", relocatePowerDownAction);
        powerUpButton.setAction(relocatePowerUpAction);
        powerUpButton.setEnabled(false);
        powerDownButton.setAction(relocatePowerDownAction);
        powerDownButton.setEnabled(false);

        ComponentPopupMenu powerPopupMenu = 
            new ComponentPopupMenu(powerRelationshipType, powerFindController);
        powerTree.addMouseListener(new PopupTrigger(powerTree, housingTree, 
                                                    controlTree, powerPopupMenu));

        housingControlPanel.add(housingPanel);
        housingControlPanel.add(controlPanel);

        // split-pane between housingControlPanel and powerPanel
        JSplitPane hierarchySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                       housingControlPanel, powerPanel);
        hierarchySplitPane.setOneTouchExpandable(true);
        hierarchySplitPane.setDividerLocation(520);
        hierarchySplitPane.setResizeWeight(1.0);
        locationPanel.add(hierarchySplitPane);

        // component selection and info panel
        selectionInfoPanel = new JPanel();
        selectionInfoPanel.setLayout(new BoxLayout(selectionInfoPanel, BoxLayout.LINE_AXIS));
        selectionInfoPanel.setPreferredSize(new Dimension(1000,120));
        selectionInfoPanel.setMaximumSize(new Dimension(1000,120));

        // fill out selection and info sub-panels

        // selection panel
        selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel,BoxLayout.PAGE_AXIS));
        TitledBorder selectionTitle = BorderFactory.createTitledBorder("Component to Configure");
        selectionPanel.setBorder(selectionTitle);
        selectionPanel.setMaximumSize(new Dimension(200,90));
        selectionPanel.setMinimumSize(new Dimension(200,90));
        LoginUtil.registerProtectedComponent(selectionPanel, editPrincipal);        

        // fill out drag/drop area, and buttons for selection panel

        // drag/drop area
        dndPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // fill out drop/drop label, and buttons panel to right

        // drag/drop label
        Icon dndIcon = new ImageIcon(AppsUtil.getImageURL("component.png"));
        dndLabel = new JLabel(dndIcon);
        dndLabel.setPreferredSize(new Dimension(80,50));
        dndLabel.setEnabled(false);
        dndLabel.setToolTipText("I'm highlighted only when you have selected a component to configure.");

        // drag/drop buttons
        dndButtonsPanel = new JPanel();
        dndButtonsPanel.setLayout(new BoxLayout(dndButtonsPanel, BoxLayout.PAGE_AXIS));
        newButton = new JButton("New...");
        LoginUtil.registerProtectedComponent(newButton, editPrincipal);
        newButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NewComponentWizardModel wizardData = 
                        NewComponentWizard.showWizard(_appFrame, _model.getComponentTypes(), null, 0);
                    // did the user "finish" or "cancel" ?
                    if (wizardData.getCancelled() == false) {
                        latchComponentToConfigure(wizardData.getComponent(), true);
                    } else {
                        latchComponentToConfigure(null, true);
                    }
                }
            });

        commitButton = new JButton("Save");
        LoginUtil.registerProtectedComponent(commitButton, editPrincipal);
        commitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (_model.getComponentToConfigure() != null) {
                        document.actionCommitComponent(_model.getComponentToConfigure());
                        housingKeyAdapter.clearTextFieldModifiedFlag();
                        controlKeyAdapter.clearTextFieldModifiedFlag();
                        powerKeyAdapter.clearTextFieldModifiedFlag();
                        attrListModel.clearTextFieldModifiedFlag();
                    }
                    
                }
            });

        clearButton = new JButton("Undo");
        clearButton.setToolTipText("Undo any edits to this component and un-select it.");
        LoginUtil.registerProtectedComponent(clearButton, editPrincipal);
        clearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    document.actionUndoComponentConfigure(false);
                    housingKeyAdapter.clearTextFieldModifiedFlag();
                    controlKeyAdapter.clearTextFieldModifiedFlag();
                    powerKeyAdapter.clearTextFieldModifiedFlag();
                    attrListModel.clearTextFieldModifiedFlag();
                    latchComponentToConfigure(null, true);
                }
            });

        dndButtonsPanel.add(commitButton);
        dndButtonsPanel.add(clearButton);

        dndPanel.add(dndLabel);
        dndPanel.add(dndButtonsPanel);

        selectionPanel.add(dndPanel);

        // info panel
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.PAGE_AXIS));
        TitledBorder infoTitle = BorderFactory.createTitledBorder("Component Info");
        infoPanel.setBorder(infoTitle);       

        // component type info
        attrListModel = new CompTypeAttrTableModel();
        attrList = new JTable(attrListModel);
        attrList.setTableHeader(null);
        attrList.setShowHorizontalLines(true);
        attrList.setCellSelectionEnabled(true);
        attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle click on component type name
        MouseListener attrListMouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    Point p = new Point(e.getX(), e.getY());
                    int row = attrList.rowAtPoint(p);
                    int col = attrList.columnAtPoint(p);
                    // component type name is in row 0, column 1
                    if (row == 0 && col == 1) {
                        // kick off document action to pop up ct viewer
                        CompTypeAttrTableModel tableModel = 
                            (CompTypeAttrTableModel)attrList.getModel();
                        String compTypeName = (String)tableModel.getValueAt(0,1);
                        document.actionProduceComponentTypeDocument(compTypeName);
                        attrList.clearSelection();
                    }
                }
            };
        attrList.addMouseListener(attrListMouseListener);

        attrList.getColumnModel().getColumn(0).setMaxWidth(150);
        attrList.getColumnModel().getColumn(0).setMinWidth(150);
        attrList.getColumnModel().getColumn(1).
            setCellRenderer(new CompTypeAttrValueTableCellRenderer());
        attrList.getColumnModel().getColumn(1).
            setCellEditor(new CompTypeAttrValueTableCellEditor());
        JScrollPane attrListScroller = 
            new JScrollPane(attrList, vsbp, hsbp);
        infoPanel.add(attrListScroller);

        selectionInfoPanel.add(selectionPanel);
        selectionInfoPanel.add(infoPanel);

        // add location and selection panels to topPanel, with a vertical split-pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                              locationPanel, selectionInfoPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        topPanel.add(splitPane);

    	getContentPane().add(topPanel);
    }

    /**
     * Utility method to expand all children of the node given
     * by path. This method uses recursion.
     */
    public void expandDescendants(JTree tree, TreePath path)
    {
        TreeModel model = tree.getModel();
        Object node = path.getLastPathComponent();
        
        // can't expand a leaf
        if (model.isLeaf(node))
            return;

        for (int count = model.getChildCount(node), i = 0; i < count; i++) {
            Object child = model.getChild(node, i);
            if (!model.isLeaf(child)) {
                TreePath childPath = path.pathByAddingChild(child);
                tree.expandPath(childPath);
                expandDescendants(tree, childPath);
            }
        }
    }

    /**
     * Utility method to collapse all children of the node given
     * by path. This method uses recursion.
     */
    public void collapseDescendants(JTree tree, TreePath path)
    {
        TreeModel model = tree.getModel();
        Object node = path.getLastPathComponent();
        
        for (int count = model.getChildCount(node), i = 0; i < count; i++) {
            Object child = model.getChild(node, i);
            TreePath childPath = path.pathByAddingChild(child);
            collapseDescendants(tree, childPath);
            tree.collapsePath(childPath);
        }
        tree.collapsePath(path);
    }


    /**
     * Given the selection in a particular hierarchy, update the other two
     * to reflect it. Update the component info table. Also decide whether
     * the logical description text field should be enabled or not.
     *
     * @param leadSelection component chosen from one hierarchy (tree)
     * @param expandBelow if true, fully expand tree below leadSelection
     * @param relationshipType the hierarchy of the leadSelection
     * @param logicalDescriptionTextField the text field for the selected hierarchy
     * @param primaryTree the tree in which selection has initially occurred
     * @param otherTree1 one of the other two not selected JTrees
     * @param otherTree2 one of the other two not selected JTrees
     *
     */
    private void setHierarchySelection(Component leadSelection,
                                       boolean expandBelow,
                                       int relationshipType,
                                       JTextField logicalDescriptionTextField,
                                       JButton downButton,
                                       JButton upButton,
                                       JCheckBox verifiedCheckBox,
                                       JTree primaryTree,
                                       JTree otherTree1,
                                       JTree otherTree2) {


        //System.out.println("select id: "+leadSelection.getId()+" in hierarchy "+relationshipType);
        
        _model.setSelectedComponent(leadSelection);

        ComponentRelationship parentRel = 
            leadSelection.getParentRelationship(relationshipType);

        
        // set selection for primary tree
        ComponentTreeModel primaryTreeModel = (ComponentTreeModel)primaryTree.getModel();
        TreePath primaryTreePath = primaryTreeModel.getTreePath(leadSelection);
        primaryTree.setSelectionPath(primaryTreePath);
        primaryTree.scrollPathToVisible(primaryTreePath);

        // show the component info
        if (_model.getComponentToConfigure() == null) {
            // request that comp info table update itself
            CompTypeAttrTableModel attrListTableModel = 
                (CompTypeAttrTableModel)attrList.getModel();
            attrListTableModel.fireTableDataChanged();
        }

        // set selection for other 2 trees
        ComponentTreeModel otherTree1Model = (ComponentTreeModel)otherTree1.getModel();
        TreePath otherTree1Path = otherTree1Model.getTreePath(leadSelection);
        otherTree1.setSelectionPath(otherTree1Path);
        otherTree1.scrollPathToVisible(otherTree1Path);
        
        ComponentTreeModel otherTree2Model = (ComponentTreeModel)otherTree2.getModel();
        TreePath otherTree2Path = otherTree2Model.getTreePath(leadSelection);
        otherTree2.setSelectionPath(otherTree2Path);
        otherTree2.scrollPathToVisible(otherTree2Path);
        
        // update the logical description text field for the chosen hierarchy
        if (parentRel != null) {
            logicalDescriptionTextField.setText(parentRel.getLogicalDescription());
            if (parentRel.getVerifiedPerson() == null) {
                verifiedCheckBox.setSelected(false);
                verifiedCheckBox.setToolTipText("Not verified");
            } else {
                Person person = parentRel.getVerifiedPerson();
                verifiedCheckBox.setSelected(true);
                verifiedCheckBox.setToolTipText("Verified by "+person.getUserid());
            }
            if (leadSelection.equals(_model.getComponentToConfigure()) &&
                LoginUtil.isPermitted(editPrincipal)) {
                logicalDescriptionTextField.setEditable(true);
                downButton.setEnabled(true);
                upButton.setEnabled(true);
                verifiedCheckBox.setEnabled(true);
            } else {
                logicalDescriptionTextField.setEditable(false);
                downButton.setEnabled(false);
                upButton.setEnabled(false);
                verifiedCheckBox.setEnabled(false);
            }
        }

        if (expandBelow) {
            // find out whether we are already expanded below primaryTreePath
            Enumeration desc = primaryTree.getExpandedDescendants(primaryTreePath);
            boolean expanded = false;
            if (desc != null)
                expanded = true;

            // invert this logic, since single click preceeding double click expands one level
            expanded = !expanded;

            Component root = (Component)primaryTreeModel.getRoot();
            boolean rootClicked = (leadSelection.getId() == root.getId());
            if (!expanded) {
                // expanding the root would take forever, so disallow it
                if (!rootClicked) {
                    // expand all children of node given by primaryTreePath
                    // first retrieve all children from database (which can be slow)
                    document.actionFindAllChildren(leadSelection, relationshipType);
                    // updateView() method below will get invoked afterwards to expand tree
                    
                } else {
                    Application.displayWarning("Notice","Fully expanding the root component is not permitted.");
                }
            } else {
                if (!rootClicked) {
                    // we can collapse here, since that is a fast operation
                    collapseDescendants(primaryTree, primaryTreePath);
                } else {
                    TreeModelEvent te = null;
                    if (relationshipType == ComponentRelationshipType.HOUSING) {
                        te = new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
                        housingTextField.setText(null);
                    } else if (relationshipType == ComponentRelationshipType.CONTROL) {
                        te = new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
                        controlTextField.setText(null);
                    } else {
                        te = new TreeModelEvent(this, new Object[] {_model.getUtilityComponent()});
                        powerTextField.setText(null);                    
                    }
                    primaryTreeModel.fireTreeStructureChanged(te);
                }
            }
            
        }
    }

    /**
     * Add a new childComponent to the appropriate hierarchy of the parentComponent.
     * Redisplay the appropriate tree.
     *
     */
    private void addNewChild(Component parentComponent, 
                             Component childComponent, 
                             int hierarchy) {

        int numChildren = 
            document.actionAddNewChildComponent(parentComponent, childComponent, hierarchy);

        // update the tree display to reflect the insertion
        if (hierarchy == ComponentRelationshipType.HOUSING) {
            ComponentTreeModel hModel = (ComponentTreeModel)housingTree.getModel();
            TreePath parentPath = hModel.getTreePath(parentComponent);
            TreeModelEvent he = 
                new TreeModelEvent(this, 
                                   parentPath,
                                   new int[] {numChildren-1},
                                   new Object[] {childComponent});
            hModel.fireTreeNodesInserted(he);
            TreePath newTreePath = parentPath.pathByAddingChild(childComponent);
            housingTree.setSelectionPath(newTreePath);
            housingTree.scrollPathToVisible(newTreePath);
            _model.setSelectedComponent(childComponent);
            
        } else if (hierarchy == ComponentRelationshipType.CONTROL) {
            ComponentTreeModel cModel = (ComponentTreeModel)controlTree.getModel();
            TreePath parentPath = cModel.getTreePath(parentComponent);
            TreeModelEvent ce = 
                new TreeModelEvent(this, 
                                   parentPath,
                                   new int[] {numChildren-1},
                                   new Object[] {childComponent});
            cModel.fireTreeNodesInserted(ce);
            TreePath newTreePath = parentPath.pathByAddingChild(childComponent);
            controlTree.setSelectionPath(newTreePath);
            controlTree.scrollPathToVisible(newTreePath);
            _model.setSelectedComponent(childComponent);


        } else if (hierarchy == ComponentRelationshipType.POWER) {
            ComponentTreeModel pModel = (ComponentTreeModel)powerTree.getModel();
            TreePath parentPath = pModel.getTreePath(parentComponent);
            TreeModelEvent pe = 
                new TreeModelEvent(this, 
                                   parentPath,
                                   new int[] {numChildren-1},
                                   new Object[] {childComponent});
            pModel.fireTreeNodesInserted(pe);
            TreePath newTreePath = parentPath.pathByAddingChild(childComponent);
            powerTree.setSelectionPath(newTreePath);
            powerTree.scrollPathToVisible(newTreePath);
            _model.setSelectedComponent(childComponent);
        }
    }

    /**
     * Insert newParentComponent in place of existing parent of currentComponent.
     * Redisplay the appropriate tree.
     *
     */
    private void insertNewParent(Component currentComponent, 
                                 Component newParentComponent, 
                                 int hierarchy) {

        document.actionInsertNewParentComponent(currentComponent, newParentComponent, hierarchy);

        // update the tree display to reflect the insertion
        TreeModelEvent te = 
            new TreeModelEvent(this, new Object[] {newParentComponent});
        TreePath newPath = null;
        if (hierarchy == ComponentRelationshipType.HOUSING) {
            ComponentTreeModel hModel = (ComponentTreeModel)housingTree.getModel();
            hModel.fireTreeStructureChanged(te);            
            newPath = hModel.getTreePath(newParentComponent);
            housingTree.setSelectionPath(newPath);
            housingTree.scrollPathToVisible(newPath);

        } else if (hierarchy == ComponentRelationshipType.CONTROL) {
            ComponentTreeModel cModel = (ComponentTreeModel)controlTree.getModel();
            cModel.fireTreeStructureChanged(te);
            newPath = cModel.getTreePath(newParentComponent);
            controlTree.setSelectionPath(newPath);
            controlTree.scrollPathToVisible(newPath);

        } else if (hierarchy == ComponentRelationshipType.POWER) {
            ComponentTreeModel pModel = (ComponentTreeModel)powerTree.getModel();
            pModel.fireTreeStructureChanged(te);
            newPath = pModel.getTreePath(newParentComponent);
            powerTree.setSelectionPath(newPath);
            powerTree.scrollPathToVisible(newPath);
        }
        _model.setSelectedComponent(newParentComponent);
        
    }

    /**
     * Change the current parent of componentToConfigure to newParentComponent.
     * Redisplay the appropriate tree.
     *
     */
    private void changeParent(Component newParentComponent, 
                              Component componentToConfigure, 
                              int hierarchy) {

        Component oldParent = componentToConfigure.getParentComponent(hierarchy);
        document.actionChangeParentOfComponent(newParentComponent, componentToConfigure, hierarchy);

        // update the tree display to reflect the changed parent
        TreeModelEvent te1 = 
            new TreeModelEvent(this, new Object[] {newParentComponent});
        TreeModelEvent te2 =
            new TreeModelEvent(this, new Object[] {oldParent});
        TreePath newPath = null;
        TreePath oldPath = null;
        if (hierarchy == ComponentRelationshipType.HOUSING) {
            ComponentTreeModel hModel = (ComponentTreeModel)housingTree.getModel();
            hModel.fireTreeStructureChanged(te1);            
            hModel.fireTreeStructureChanged(te2);            
            oldPath = hModel.getTreePath(oldParent);
            housingTree.expandPath(oldPath);
            newPath = hModel.getTreePath(componentToConfigure);
            housingTree.setSelectionPath(newPath);
            housingTree.scrollPathToVisible(newPath);

        } else if (hierarchy == ComponentRelationshipType.CONTROL) {
            ComponentTreeModel cModel = (ComponentTreeModel)controlTree.getModel();
            cModel.fireTreeStructureChanged(te1);
            cModel.fireTreeStructureChanged(te2);
            oldPath = cModel.getTreePath(oldParent);
            controlTree.expandPath(oldPath);
            newPath = cModel.getTreePath(componentToConfigure);
            controlTree.setSelectionPath(newPath);
            controlTree.scrollPathToVisible(newPath);

        } else if (hierarchy == ComponentRelationshipType.POWER) {
            ComponentTreeModel pModel = (ComponentTreeModel)powerTree.getModel();
            pModel.fireTreeStructureChanged(te1);
            pModel.fireTreeStructureChanged(te2);
            oldPath = pModel.getTreePath(oldParent);
            powerTree.expandPath(oldPath);
            newPath = pModel.getTreePath(componentToConfigure);
            powerTree.setSelectionPath(newPath);
            powerTree.scrollPathToVisible(newPath);
        }
        _model.setSelectedComponent(componentToConfigure);
        
    }

    /**
     * Deletes a component from given hierarchy. When the changes are later
     * committed by the user, the component will be deleted entirely if it is
     * not in at least one hierarchy. 
     */
    private void deleteComponent(Component component, 
                                 int hierarchy) {
        
        ComponentRelationship cr = component.getParentRelationship(hierarchy);
        if (cr != null) {
            Component parentComponent = cr.getParentComponent();
            int childIndex = 
                document.actionDeleteComponent(component, hierarchy);
            
            // update the tree display to reflect the deletion
            if (childIndex != -1 && hierarchy == ComponentRelationshipType.HOUSING) {
                ComponentTreeModel hModel = (ComponentTreeModel)housingTree.getModel();
                TreePath parentPath = hModel.getTreePath(parentComponent);
                TreeModelEvent he = 
                    new TreeModelEvent(this, 
                                       parentPath,
                                       new int[] {childIndex},
                                       new Object[] {component});
                hModel.fireTreeNodesRemoved(he);
                
            } else if (childIndex != -1 && hierarchy == ComponentRelationshipType.CONTROL) {
                ComponentTreeModel cModel = (ComponentTreeModel)controlTree.getModel();
                TreePath parentPath = cModel.getTreePath(parentComponent);
                TreeModelEvent ce = 
                    new TreeModelEvent(this, 
                                       parentPath,
                                       new int[] {childIndex},
                                       new Object[] {component});
                cModel.fireTreeNodesRemoved(ce);
                
            } else if (childIndex != -1 && hierarchy == ComponentRelationshipType.POWER) {
                ComponentTreeModel pModel = (ComponentTreeModel)powerTree.getModel();
                TreePath parentPath = pModel.getTreePath(parentComponent);                
                TreeModelEvent pe = 
                    new TreeModelEvent(this, 
                                       parentPath,
                                       new int[] {childIndex},
                                       new Object[] {component});
                pModel.fireTreeNodesRemoved(pe);
            }
            updateJTreeViews(null);
        } // end if (cr != null)
    }

    /**
     * Update the display of the jtree nodes corresponding to the given component c.
     * Typically used when the underlying data has changed, not the structure. If
     * given component c is null, update all 3 trees fully.
     */
    private void updateJTreeViews(Component c) {

        // have each tree redraw particular component (if it exists in tree)
        ComponentTreeModel tModel = null;
        TreePath pPath = null;
        TreeModelEvent tme = null;
        ComponentRelationship cr = null;
        // housing tree
        tModel = (ComponentTreeModel)housingTree.getModel();
        if (c == null) {
            tme = new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});            
            tModel.fireTreeNodesChanged(tme);
        } else {
            cr = c.getParentRelationship(ComponentRelationshipType.HOUSING);
            if (cr != null) {
                pPath = tModel.getTreePath(cr.getParentComponent());
                int cIndex = tModel.getIndexOfChild(cr.getParentComponent(), c);
                tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, new Object[] {c});
                tModel.fireTreeNodesChanged(tme);
            }
        }

        // control tree
        tModel = (ComponentTreeModel)controlTree.getModel();
        if (c == null) {
            tme = new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});            
            tModel.fireTreeNodesChanged(tme);
        } else {
            cr = c.getParentRelationship(ComponentRelationshipType.CONTROL);
            if (cr != null) {
                pPath = tModel.getTreePath(cr.getParentComponent());
                int cIndex = tModel.getIndexOfChild(cr.getParentComponent(), c);
                tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, new Object[] {c});
                tModel.fireTreeNodesChanged(tme);
            }
        }

        // power tree
        tModel = (ComponentTreeModel)powerTree.getModel();
        if (c == null) {
            tme = new TreeModelEvent(this, new Object[] {_model.getUtilityComponent()});            
            tModel.fireTreeNodesChanged(tme);
        } else {
            cr = c.getParentRelationship(ComponentRelationshipType.POWER);
            if (cr != null) {
                pPath = tModel.getTreePath(cr.getParentComponent());
                int cIndex = tModel.getIndexOfChild(cr.getParentComponent(), c);
                tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, new Object[] {c});
                tModel.fireTreeNodesChanged(tme);
            }
        }

        // have the component info table update itself, too!
        CompTypeAttrTableModel attrListTableModel = 
            (CompTypeAttrTableModel)attrList.getModel();
        attrListTableModel.fireTableDataChanged();
    }

    /**
     * Adjust state of model and user interface to reflect the fact that a
     * component has been selected/deselected for configuration. Mostly 
     * just enables/disables various graphical widgets.
     *
     * @param c the component selected for configure, or null if deselected
     * @param exclusive if true, use semaphore to prevent concurrent editing
     */
    private void latchComponentToConfigure(Component c, boolean exclusive) {

        // have the component info table update itself
        CompTypeAttrTableModel attrListTableModel = 
            (CompTypeAttrTableModel)attrList.getModel();
        attrListTableModel.fireTableDataChanged();
        
        if (c != null) {   // latch

            if (exclusive) {
                try {
                    SemaphoreValue sv = ComponentService.takeSemaphore(LoginUtil.getUsername());
                    _model.setSemaphoreValue(sv);
                    if (sv.getSemaphoreValue() == 0) {
                        String userid = "unknown";
                        if (sv.getUserid() != null)
                            userid = sv.getUserid();
                        Application.displayWarning("IRMIS Service Warning","User "+userid+" is currently editing, please try again in a few moments.");
                        return;
                        
                    } else if (sv.getModifiedDate() > _model.getDataTimestamp()) {
                        Application.displayWarning("IRMIS Service Warning","Your data is stale due to recent editing. We will reload the data, after which you may try again.");
                        Main.requestResetOfDocuments();                    
                        return;
                    }
                } catch (IRMISException ie) {
                    Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Warning.", ie);
                    ie.printStackTrace();
                    return;
                }
            }
            // set component to configure in model
            _model.setComponentToConfigure(c);

            dndLabel.setEnabled(true);

            // filter possible parent component types
            document.actionFilterComponentTypes();
            
            // set the state of the logical description text fields
            ComponentRelationship parentRel = null;

            // housing
            parentRel = 
                c.getParentRelationship(ComponentRelationshipType.HOUSING);
            if (parentRel != null && LoginUtil.isPermitted(editPrincipal)) {
                housingTextField.setEditable(true);
                housingUpButton.setEnabled(true);
                housingDownButton.setEnabled(true);
                housingVerifiedCheckBox.setEnabled(true);
            } else {
                housingTextField.setEditable(false);
                housingUpButton.setEnabled(false);
                housingDownButton.setEnabled(false);
                housingVerifiedCheckBox.setEnabled(false);
            }

            // control
            parentRel = 
                c.getParentRelationship(ComponentRelationshipType.CONTROL);
            if (parentRel != null && LoginUtil.isPermitted(editPrincipal)) {
                controlTextField.setEditable(true);
                controlUpButton.setEnabled(true);
                controlDownButton.setEnabled(true);
                controlVerifiedCheckBox.setEnabled(true);
            } else {
                controlTextField.setEditable(false);
                controlUpButton.setEnabled(false);
                controlDownButton.setEnabled(false);
                controlVerifiedCheckBox.setEnabled(false);
            }

            // power
            parentRel = 
                c.getParentRelationship(ComponentRelationshipType.POWER);
            if (parentRel != null && LoginUtil.isPermitted(editPrincipal)) {
                powerTextField.setEditable(true);
                powerUpButton.setEnabled(true);
                powerDownButton.setEnabled(true);
                powerVerifiedCheckBox.setEnabled(true);
            } else {
                powerTextField.setEditable(false);
                powerUpButton.setEnabled(false);
                powerDownButton.setEnabled(false);
                powerVerifiedCheckBox.setEnabled(false);
            }

            
        } else {  // unlatch

            // set component to configure in model
            _model.setComponentToConfigure(null);

            try {
                if (ComponentService.giveSemaphore(_model.getSemaphoreValue()))
                    _model.setDataTimestamp(_model.getSemaphoreValue().getModifiedDate());

            } catch (IRMISException ie) {
                Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Warning.", ie);
                ie.printStackTrace();
            }

            dndLabel.setEnabled(false);

            // undo the component type filters
            document.actionFilterComponentTypes();

            // disable editing of logical description text fields
            housingTextField.setEditable(false);
            controlTextField.setEditable(false);
            powerTextField.setEditable(false);
            housingUpButton.setEnabled(false);
            housingDownButton.setEnabled(false);
            housingVerifiedCheckBox.setEnabled(false);
            controlUpButton.setEnabled(false);
            controlDownButton.setEnabled(false);
            controlVerifiedCheckBox.setEnabled(false);
            powerUpButton.setEnabled(false);
            powerDownButton.setEnabled(false);
            powerVerifiedCheckBox.setEnabled(false);

            // clear this dirty flag, since we have either finished
            // with commit or undo at this point
            housingKeyAdapter.clearTextFieldModifiedFlag();
            controlKeyAdapter.clearTextFieldModifiedFlag();
            powerKeyAdapter.clearTextFieldModifiedFlag();
            attrListModel.clearTextFieldModifiedFlag();
            
        }

    }


    /**
     * Update graphical view of ComponentModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(ComponentModelEvent event) {

        // check first for any exception
        IRMISException ie = _model.getIRMISException();
        if (ie != null) {  // uh-oh: figure out if it's fatal or we just need to reload data
            Throwable cause = ie.getCause();
            if (cause != null && cause instanceof DAOStaleObjectStateException) {
                Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Warning.", ie);
                Application.displayWarning("IRMIS Service Warning","Your data is stale. Reloading application data.");
                Main.requestResetOfDocuments();

            } else {
                Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
                Application.displayError("IRMIS Service Error", "Exiting due to exception... ", ie);
                System.exit(-1);
            }
            return;
        }

        switch(event.getType()) {

        case ComponentModelEvent.REDRAW: {

            // the initial tree painting takes a long time, so
            _appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // clear slot/card/outlet text fields
            housingTextField.setText(null);
            controlTextField.setText(null);
            powerTextField.setText(null);

            // housing
            ComponentTreeModel hModel = (ComponentTreeModel)housingTree.getModel();
            hModel.setRoot(_model.getSiteComponent());
            if (_model.getSiteComponent() == null) {
                TreeModelEvent he = 
                    new TreeModelEvent(this, (TreePath)null);
                hModel.fireTreeStructureChanged(he);
            } else {
                TreeModelEvent he = 
                    new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
                hModel.fireTreeStructureChanged(he);
            }

            // control
            ComponentTreeModel cModel = (ComponentTreeModel)controlTree.getModel();
            cModel.setRoot(_model.getNetworkComponent());
            if (_model.getNetworkComponent() == null) {
                TreeModelEvent ce = 
                    new TreeModelEvent(this, (TreePath)null);
                cModel.fireTreeStructureChanged(ce);
            } else {
                TreeModelEvent ce = 
                    new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
                cModel.fireTreeStructureChanged(ce);
            }

            // power
            ComponentTreeModel pModel = (ComponentTreeModel)powerTree.getModel();
            pModel.setRoot(_model.getUtilityComponent());
            if (_model.getUtilityComponent() == null) {
                TreeModelEvent pe = 
                    new TreeModelEvent(this, (TreePath)null);
                pModel.fireTreeStructureChanged(pe);
            } else {
                TreeModelEvent pe = 
                    new TreeModelEvent(this, new Object[] {_model.getUtilityComponent()});
                pModel.fireTreeStructureChanged(pe);
            }

            latchComponentToConfigure(null, true);

            // If _model.getSelectedComponent() is not null, preselect it in trees
            Component c = _model.getSelectedComponent();
            if (c != null) {
                setHierarchySelection(c, false, housingRelationshipType, 
                                      housingTextField, housingDownButton, housingUpButton,
                                      housingVerifiedCheckBox, housingTree, controlTree, powerTree);
            } 

            // go back to regular cursor
            _appFrame.setCursor(null);
            
            break;
        }

        case ComponentModelEvent.NEW_FILTERED_TYPES: {
            updateJTreeViews(null);  // redraw all three trees
            break;
        }

        case ComponentModelEvent.COMMIT_COMPLETE: {
            Boolean staleData = (Boolean)event.getPayload();
 
            if (staleData.booleanValue()) {
                Application.displayWarning("IRMIS Service Warning", "Your data became stale before you saved it. Your edit in progress has been undone, and we will reload your data now.");
                Main.requestResetOfDocuments();
            }

            latchComponentToConfigure(null, true);
            break;
        }

        case ComponentModelEvent.CHILD_REMOVED: {
            ConfigureAction action = (ConfigureAction)event.getPayload();
            int hierarchy = action.getHierarchy();
            Component ctc = action.getComponentToConfigure();
            ComponentRelationship cr = action.getParentRelationship();
            int index = action.getIndex();
            JTree tree = null;
            if (hierarchy == ComponentRelationshipType.HOUSING) {
                tree = housingTree;
            } else if (hierarchy == ComponentRelationshipType.CONTROL) {
                tree = controlTree;
            } else if (hierarchy == ComponentRelationshipType.POWER) {
                tree = powerTree;
            }            
            Component parent = cr.getParentComponent();
            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
            TreePath parentPath = tModel.getTreePath(parent);
            TreeModelEvent te = 
                new TreeModelEvent(this, 
                                   parentPath,
                                   new int[] {index},
                                   new Object[] {ctc});
            tModel.fireTreeNodesRemoved(te);
            _model.setSelectedComponent(null);
            break;
        }

        case ComponentModelEvent.CHILD_ADDED: {
            ConfigureAction action = (ConfigureAction)event.getPayload();
            int hierarchy = action.getHierarchy();
            Component ctc = action.getComponentToConfigure();
            ComponentRelationship cr = action.getParentRelationship();
            int index = action.getIndex();
            JTree tree = null;
            if (hierarchy == ComponentRelationshipType.HOUSING) {
                tree = housingTree;
            } else if (hierarchy == ComponentRelationshipType.CONTROL) {
                tree = controlTree;
            } else if (hierarchy == ComponentRelationshipType.POWER) {
                tree = powerTree;
            }            
            Component parent = cr.getParentComponent();
            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
            TreeModelEvent te = 
                new TreeModelEvent(this, new Object[] {parent});
            tModel.fireTreeStructureChanged(te);

            TreePath newTreePath = tModel.getTreePath(ctc);
            tree.setSelectionPath(newTreePath);
            _model.setSelectedComponent(ctc);

            break;
        }

        case ComponentModelEvent.CHILD_RELOCATED: {
            ConfigureAction action = (ConfigureAction)event.getPayload();
            int hierarchy = action.getHierarchy();
            Component ctc = action.getComponentToConfigure();
            ComponentRelationship cr = action.getParentRelationship();
            int index = action.getIndex();
            JTree tree = null;
            if (hierarchy == ComponentRelationshipType.HOUSING) {
                tree = housingTree;
            } else if (hierarchy == ComponentRelationshipType.CONTROL) {
                tree = controlTree;
            } else if (hierarchy == ComponentRelationshipType.POWER) {
                tree = powerTree;
            }            
            // update tree to reflect relocation
            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
            TreePath parentPath = tModel.getTreePath(cr.getParentComponent());

            Component parent = cr.getParentComponent();
            List crList = parent.getChildRelationships(hierarchy);
            int crListSize = crList.size();
            int[] indices = new int[crListSize];
            for (int i=0 ; i < crListSize ; i++)
                indices[i] = i;
            TreeModelEvent te = 
                new TreeModelEvent(this, parentPath, indices, null);
            tModel.fireTreeNodesChanged(te);
            
            break;
        }

        case ComponentModelEvent.MODIFY_LOGICAL_DESCRIPTION: {
            ConfigureAction action = (ConfigureAction)event.getPayload();
            int hierarchy = action.getHierarchy();
            Component ctc = action.getComponentToConfigure();
            ComponentRelationship cr = action.getParentRelationship();
            JTextField textField = null;
            JCheckBox verifiedCheckBox = null;
            if (hierarchy == ComponentRelationshipType.HOUSING) {
                textField = housingTextField;
                verifiedCheckBox = housingVerifiedCheckBox;
            } else if (hierarchy == ComponentRelationshipType.CONTROL) {
                textField = controlTextField;
                verifiedCheckBox = controlVerifiedCheckBox;
            } else if (hierarchy == ComponentRelationshipType.POWER) {
                textField = powerTextField;
                verifiedCheckBox = powerVerifiedCheckBox;
            }            
            textField.setText(cr.getLogicalDescription());
            Person verifiedPerson = cr.getVerifiedPerson();
            if (verifiedPerson == null) {
                verifiedCheckBox.setSelected(false);
                verifiedCheckBox.setToolTipText("Not verified");
            } else {
                verifiedCheckBox.setSelected(true);
                verifiedCheckBox.setToolTipText("Verified by "+verifiedPerson.getUserid());
            }
            break;
        }

        case ComponentModelEvent.MODIFY_COMPONENT_NAME: {
            ConfigureAction action = (ConfigureAction)event.getPayload();
            Component c = action.getComponentToConfigure();
            updateJTreeViews(c);
            break;
        }

        case ComponentModelEvent.SELECT_COMPONENT_IN_HOUSING: {
            Component selectedComponent = (Component)event.getPayload();
            
            // in which case this is not even necessary
            if (selectedComponent != null) {
                setHierarchySelection(selectedComponent, false,
                                      housingRelationshipType, 
                                      housingTextField, housingDownButton, 
                                      housingUpButton, housingVerifiedCheckBox,
                                      housingTree, controlTree, powerTree); 
            }
            break;
        }

        case ComponentModelEvent.HOUSING_FIND_CONFIG: {
            ConfigureFindModel findConfig = (ConfigureFindModel)event.getPayload();

            // housing
            ComponentTreeModel hModel = (ComponentTreeModel)housingTree.getModel();
            TreeModelEvent he = 
                new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
            hModel.fireTreeStructureChanged(he);
            
            if (findConfig.getClearSearchField()) {
                housingSearchField.setText(null);
                findConfig.setClearSearchField(false);
            }
            
            List componentList = null;
            int componentListSize = 0;
            componentList = hModel.getComponentList();
            if (componentList != null)
                componentListSize = componentList.size();
            
            if (componentListSize > 0) {
                if (findConfig.getComponentId() == null) {
                    housingPrevButton.setEnabled(false);
                    housingNextButton.setEnabled(true);
                    housingStopButton.setEnabled(true);
                }
                Component firstFind = (Component)componentList.get(0);
                TreePath firstFindPath = hModel.getTreePath(firstFind);
                housingTree.setSelectionPath(firstFindPath);
                housingTree.scrollPathToVisible(firstFindPath);

            } else if (findConfig.filterApplied()) {
                housingStopButton.setEnabled(true);

            } else {
                housingStopButton.setEnabled(false);
                housingPrevButton.setEnabled(false);
                housingNextButton.setEnabled(false);
            }

            break;
        }

        case ComponentModelEvent.CONTROL_FIND_CONFIG: {
            ConfigureFindModel findConfig = (ConfigureFindModel)event.getPayload();

            // control
            ComponentTreeModel cModel = (ComponentTreeModel)controlTree.getModel();
            TreeModelEvent ce = 
                new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
            cModel.fireTreeStructureChanged(ce);
            if (findConfig.getClearSearchField()) {
                controlSearchField.setText(null);
                findConfig.setClearSearchField(false);
            }

            List componentList = null;
            int componentListSize = 0;
            componentList = cModel.getComponentList();
            if (componentList != null)
                componentListSize = componentList.size();
            
            if (componentListSize > 0) {
                if (findConfig.getComponentId() == null) {
                    controlPrevButton.setEnabled(false);
                    controlNextButton.setEnabled(true);
                    controlStopButton.setEnabled(true);
                }
                Component firstFind = (Component)componentList.get(0);
                TreePath firstFindPath = cModel.getTreePath(firstFind);
                controlTree.setSelectionPath(firstFindPath);
                controlTree.scrollPathToVisible(firstFindPath);

            } else if (findConfig.filterApplied()) {
                controlStopButton.setEnabled(true);
                
            } else {
                controlStopButton.setEnabled(false);
                controlPrevButton.setEnabled(false);
                controlNextButton.setEnabled(false);
            }
            break;
        }

        case ComponentModelEvent.POWER_FIND_CONFIG: {
            ConfigureFindModel findConfig = (ConfigureFindModel)event.getPayload();

            // power
            ComponentTreeModel pModel = (ComponentTreeModel)powerTree.getModel();
            TreeModelEvent pe = 
                new TreeModelEvent(this, new Object[] {_model.getUtilityComponent()});
            pModel.fireTreeStructureChanged(pe);
            if (findConfig.getClearSearchField()) {
                powerSearchField.setText(null);
                findConfig.setClearSearchField(false);
            }
            
            List componentList = null;
            int componentListSize = 0;
            componentList = pModel.getComponentList();
            if (componentList != null)
                componentListSize = componentList.size();

            if (componentListSize > 0) {
                if (findConfig.getComponentId() == null) {
                    powerPrevButton.setEnabled(false);
                    powerNextButton.setEnabled(true);
                    powerStopButton.setEnabled(true);
                }
                Component firstFind = (Component)componentList.get(0);
                TreePath firstFindPath = pModel.getTreePath(firstFind);
                powerTree.setSelectionPath(firstFindPath);
                powerTree.scrollPathToVisible(firstFindPath);

            } else if (findConfig.filterApplied()) {
                powerStopButton.setEnabled(true);

            } else {
                powerStopButton.setEnabled(false);
                powerPrevButton.setEnabled(false);
                powerNextButton.setEnabled(false);
            }
            break;
        }

        case ComponentModelEvent.EXPAND_CHILDREN: {

            Component c = (Component)event.getPayload();
            if (focusedTree != null) {
                // this drawing operation can take a while, so cursor it
                _appFrame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                _appFrame.getGlassPane().setVisible(true);
                ComponentTreeModel treeModel = (ComponentTreeModel)focusedTree.getModel();
                TreePath treePath = treeModel.getTreePath(c);
                expandDescendants(focusedTree, treePath);
                _appFrame.getGlassPane().setVisible(false);
                _appFrame.getGlassPane().setCursor(null);
            }
            break;
        }

        case ComponentModelEvent.REPLACEMENT_LIST: {
            Component c = (Component)event.getPayload();
            
            if (c.hasCable()) {
                // dialog box - we can't replace a component with cables attached
                Application.displayWarning("Component has cables attached","You must first use idt::cable and detach all cables before replacing this component.");
            } else {
                List list = _model.getReplacementComponentTypes();
                if (list == null || list.size() == 0) {
                    Application.displayWarning("No replacement types available","This component has no valid replacement types");
                } else {
                    ComponentType replacementType = 
                        ReplaceComponentDialog.showDialog(_appFrame, null, list);
                    if (replacementType != null)
                        document.actionReplaceComponentType(c, replacementType);
                }

            }
            break;
        }

        default: {}
        }

    }

    /********************************************************************
     * Inner classes supporting Swing components
     ********************************************************************/

    public class ComponentTreeCellRenderer extends DefaultTreeCellRenderer {

        private int hierarchy;

        public ComponentTreeCellRenderer(int hierarchy) {
            super();
            this.hierarchy = hierarchy;
        }

        public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
                                                               boolean selected, boolean expanded,
                                                               boolean leaf, int row, 
                                                               boolean hasFocus) {

            Component c = (Component)value;
            ComponentType ct = c.getComponentType();
            Component ctc = _model.getComponentToConfigure();
            boolean enabled = true;
            boolean newComponent = false;
            boolean componentToConfigure = false;
            List filteredTypes = _model.getFilteredComponentTypes(hierarchy);
            Component parentComponent;

            if (ctc != null && filteredTypes != null) {
                parentComponent = ctc.getParentComponent(hierarchy);
                if (filteredTypes.contains(ct) && !c.equals(parentComponent)) {
                    enabled = true;
                } else {
                    enabled = false;
                }
                if (c.equals(_model.getComponentToConfigure())) {
                    componentToConfigure = true;
                    enabled = true;
                } else if (_model.deleteHasOccurred()) {  // don't enable anything after a delete
                    enabled = false;
                } else {
                    componentToConfigure = false;
                }

            } 
            
            // figure out whether this component is considered "new" in this hierarchy
            // NOTE: probably should put this logic in service
            ComponentRelationship housingParent = 
                c.getParentRelationship(ComponentRelationshipType.HOUSING);
            ComponentRelationship controlParent = 
                c.getParentRelationship(ComponentRelationshipType.CONTROL);
            ComponentRelationship powerParent =
                c.getParentRelationship(ComponentRelationshipType.POWER);

            boolean newHousingParent = housingParent==null || housingParent.getId()==null;
            boolean newControlParent = controlParent==null || controlParent.getId()==null;
            boolean newPowerParent = powerParent==null || powerParent.getId()==null;

            if (c.getId() == null ||
                (hierarchy==ComponentRelationshipType.HOUSING && newHousingParent) ||
                (hierarchy==ComponentRelationshipType.CONTROL && newControlParent) ||
                (hierarchy==ComponentRelationshipType.POWER && newPowerParent)) {
                // need special case so we don't mark root of hierarchy as "new"
                if (!(housingParent==null && 
                      controlParent==null && 
                      powerParent==null)) {
                    newComponent = true;
                    enabled = true;
                }
            }

            if (newComponent || componentToConfigure) {
                setTextNonSelectionColor(Color.green.darker());
                setTextSelectionColor(Color.green.darker());
            } else {
                setTextNonSelectionColor(Color.black);
                setTextSelectionColor(Color.black);
            }
            setLeafIcon(null);
            setOpenIcon(null);
            setClosedIcon(null);

            super.getTreeCellRendererComponent(tree, value, selected, expanded, 
                                               leaf, row, hasFocus);

            
            setText(alignIocName(c.toString(hierarchy)));  // display string provided by component
            setEnabled(enabled);
            return this;
        }
    }

    /**
     * Takes a component.toString() value and adds spaces between
     * component type and ioc or sioc. This is just a bit of custom
     * display string tweaking for iocs in the control tree.
     */
    private String alignIocName(String compString) {
        int indexIoc = compString.indexOf("ioc");
        int indexSioc = compString.indexOf("sioc");
        int index = -1;
        if (indexSioc > 0)
            index = indexSioc;
        else if (indexIoc > 0)
            index = indexIoc;

        if (index == -1)
            return compString;

        String first = compString.substring(0,index);
        String second = compString.substring(index);
        JTextField firstField = new JTextField(first);
        StringBuffer sb = new StringBuffer(first);
        while (firstField.getPreferredSize().getWidth() < 126) {
            sb.append(" ");
            firstField = new JTextField(sb.toString());
        }
        sb.append(second);
        return sb.toString();
        
    }

    /**
     * Component type attribute table model. Displays the myriad of 
     * different scalar info about a component type.
     */
    public class CompTypeAttrTableModel extends AbstractTableModel {
        private String[] columnNames = {"Name","Value"};
        private boolean textFieldModified = false;
        private boolean serialNumberFieldModified = false;
        private boolean groupNameModified = false;
        private boolean verifiedModified = false;
        private boolean imageModified = false;

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            Component comp = null;
            if (_model.getComponentToConfigure() != null)
                comp = _model.getComponentToConfigure();
            else
                comp = _model.getSelectedComponent();

            if (comp != null) {
                return 10;
            } else {
                return 0;
            }

        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            Component comp = null;
            if (_model.getComponentToConfigure() != null)
                comp = _model.getComponentToConfigure();
            else
                comp = _model.getSelectedComponent();

            if (comp != null) {
                ComponentType compType = comp.getComponentType();
                switch (row) {
                case 0: {
                    if (col == 0) 
                        return "Component Type";
                    else
                        return compType.getComponentTypeName();
                }
                case 1: {
                    if (col == 0) {
                        return "Component's Field Name";
                    } else {
                        String cName = comp.getComponentName();
                        if (cName != null)
                            return cName;
                        else
                            return " ";
                    }
                }
                case 2: {
                    if (col == 0) {
                        return "Component ID";
                    } else {
                        Long id = comp.getId();
                        if (id == null)
                            return "not saved yet";
                        else
                            return id.toString();
                    }
                }
                case 3: {
                    if (col == 0)
                        return "Description";
                    else
                        return compType.getDescription();
                }
                case 4: {
                    if (col == 0)
                        return "Serial Number";
                    else
                        return comp.getApsComponent().getSerialNumber();
                }
                case 5: {
                    if (col == 0)
                        return "Group Ownership";
                    else
                        return comp.getApsComponent().getGroupName();
                }
                    /*
                case 6: {
                    if (col == 0)
                        return "Verified";
                    else {
                        if (comp.getApsComponent().getVerified())
                            return "true";
                        else
                            return "false";
                    }
                }
                    */
                case 6: {
                    if (col == 0)
                        return "Manufacturer";
                    else {
                        if (compType.getManufacturer() == null)
                            return "none given";
                        else
                            return compType.getManufacturer().getManufacturerName();
                    }
                }
                case 7: {
                    if (col == 0)
                        return "Form Factor";
                    else {
                        if (compType.getFormFactor() == null)
                            return "none given";
                        else
                            return compType.getFormFactor().getFormFactor();
                    }
                }
                case 8: {
                    if (col == 0)
                        return "Function(s)";
                    else {
                        if (compType.getComponentTypeFunctions() == null) {
                            return "none";
                        } else {
                            Iterator it = compType.getComponentTypeFunctions().iterator();
                            StringBuffer sb = new StringBuffer();
                            while (it.hasNext()) {
                                ComponentTypeFunction ctf = (ComponentTypeFunction)it.next();
                                sb.append(ctf.getFunction().getFunctionName() + ",");
                            }
                            if (sb.length() > 0)
                                sb.deleteCharAt(sb.length()-1);
                            return sb.toString();
                        }
                    }
                }
                case 9: {
                    if (col == 0) {
                        return "Component Image URL";
                    } else {
                        String iURI = comp.getImageURI();
                        if (iURI != null)
                            return iURI;
                        else
                            return " ";
                    }
                }
                }
                return " ";

            } else {
                return " ";
            }
        }

        // called after edit of table cell is complete
        public void setValueAt(Object value, int row, int col) {
            Component c = _model.getComponentToConfigure();
            if (col == 1 && c != null) {
                if (row == 1) {     // component name
                    String compName = (String)value;
                    String originalName = c.getComponentName();
                    c.setComponentName(compName);
                    // Enter a ConfigureAction to undo stack here,
                    // but just once to remember original value.
                    if (!this.textFieldModified) {
                        // add this to undo configure stack
                        ConfigureAction action = 
                            new ConfigureAction(ConfigureAction.MODIFY_COMPONENT_NAME, 
                                                c, null, 0, 0, originalName);
                        _model.pushConfigureUndoStack(action);                            
                        this.textFieldModified = true;
                    }                
                    
                } else if (row == 4) {  // serial no.
                    String serialNo = (String)value;
                    String originalSerialNo = c.getApsComponent().getSerialNumber();
                    c.getApsComponent().setSerialNumber(serialNo);
                    // Enter a ConfigureAction to undo stack here,
                    // but just once to remember original value.
                    if (!this.serialNumberFieldModified) {
                        // add this to undo configure stack
                        ConfigureAction action = 
                            new ConfigureAction(ConfigureAction.MODIFY_SERIAL_NUMBER, 
                                                c, null, 0, 0, originalSerialNo);
                        _model.pushConfigureUndoStack(action);                            
                        this.serialNumberFieldModified = true;
                    }                

                } else if (row == 5) {  // group name
                    GroupName gn = (GroupName)value;
                    GroupName originalGn = c.getApsComponent().getGroupName();
                    c.getApsComponent().setGroupName(gn);
                    // Enter a ConfigureAction to undo stack here,
                    // but just once to remember original value.
                    if (!this.groupNameModified) {
                        // add this to undo configure stack
                        ConfigureAction action = 
                            new ConfigureAction(ConfigureAction.MODIFY_GROUP_NAME, 
                                                c, null, 0, 0, originalGn.getGroupName());
                        _model.pushConfigureUndoStack(action);                            
                        this.groupNameModified = true;
                    }                

                } else if (row == 9) {     // component image URI/URL
                    String iName = (String)value;
                    String originaliName = c.getImageURI();
                    c.setImageURI(iName);
                    // Enter a ConfigureAction to undo stack here,
                    // but just once to remember original value.
                    if (!this.imageModified) {
                        // add this to undo configure stack
                        ConfigureAction action = 
                            new ConfigureAction(ConfigureAction.MODIFY_COMPONENT_IMAGE_URI, 
                                                c, null, 0, 0, originaliName);
                        _model.pushConfigureUndoStack(action);                            
                        this.imageModified = true;
                    }                
                }

                /*else if (row == 6) {  // verified flag
                  String verified = (String)value;
                  String originalFlag = null;
                  if (c.getApsComponent().getVerified())
                  originalFlag = "true";
                  else
                  originalFlag = "false";
                  if (verified.equals("true"))
                  c.getApsComponent().setVerified(true);
                  else
                  c.getApsComponent().setVerified(false);
                  // Enter a ConfigureAction to undo stack here,
                  // but just once to remember original value.
                  if (!this.verifiedModified) {
                  // add this to undo configure stack
                  ConfigureAction action = 
                  new ConfigureAction(ConfigureAction.MODIFY_VERIFIED_FLAG, 
                  c, null, 0, 0, originalFlag);
                  _model.pushConfigureUndoStack(action);                            
                  this.verifiedModified = true;
                  }                
                  } */
                updateJTreeViews(c);
            }

        }

        /**
         * We can clear all of them with this one call
         */
        public void clearTextFieldModifiedFlag() {
            this.textFieldModified = false;
            this.serialNumberFieldModified = false;
            this.groupNameModified = false;
            this.verifiedModified = false;
            this.imageModified = false;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {

            if (_model.getComponentToConfigure() != null) {
                if (col == 1 && (row==1 || row==4 || row==5 || row==6 || row==9))
                    return true;
                else 
                    return false;
            } else {
                return false;
            }
        }
    }

    /**
     * Customized table cell renderer used for comp type attribute table.
     */
    class CompTypeAttrValueTableCellRenderer extends DefaultTableCellRenderer {
        public java.awt.Component getTableCellRendererComponent (JTable table,
                                                                 Object value,
                                                                 boolean selected,
                                                                 boolean focused,
                                                                 int row, int column) {
            setEnabled(table==null || table.isEnabled());
            if (column == 1 && row == 0) {  // highlight component type to look like a link
                setForeground(Color.blue);
            } else if (column == 1 && (row==1 || row==4 || row==5 || row==6 || row==9) &&
                       _model.getComponentToConfigure() != null) {
                setForeground(Color.green.darker());
            } else {
                setForeground(Color.black);
            }
            
            if (row == 0) {
                setToolTipText("Click for more details on component type.");
            } else if (_model.getComponentToConfigure() != null && 
                       (row==1 || row==4 || row==5 || row==6 || row==9)) {
                setToolTipText("Click to edit component property.");
            } else {
                setToolTipText(null);
            }

            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }    

    /**
     * Cell editor for cable color column.
     */
    class CompTypeAttrValueTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {
        
        int rowEdited = 0;

        JTextField compNameTextField = null;
        JTextField serialNumberTextField = null;
        JTextField imageURITextField = null;
        JComboBox gnCb = null;
        JComboBox verifiedCb = null;
        
        // called when cell value edited by user
        public java.awt.Component getTableCellEditorComponent(JTable table,
                                                              Object value, 
                                                              boolean isSelected,
                                                              int row, int col) {

            if (compNameTextField == null) {
                compNameTextField = new JTextField();
                compNameTextField.setEditable(true);
                compNameTextField.setForeground(Color.green.darker());
                compNameTextField.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextField compNameTextField = (JTextField)e.getComponent();
                            
                            if (keyChar == '\n') {
                                // not a good way to get rid of newline,
                                // but I can't figure out how to stop
                                // this from getting entered in text area
                                int cp = compNameTextField.getCaretPosition();
                                Document doc = compNameTextField.getDocument();
                                try {
                                    doc.remove(compNameTextField.getText().length()-1,1);
                                } catch (BadLocationException ble) {}
                                stopCellEditing();
                            }
                        }
                    });
                compNameTextField.addMouseListener(new MouseAdapter() {
                        public void mouseExited(MouseEvent e) {
                            stopCellEditing();
                        }
                    });
            }
            if (imageURITextField == null) {
                imageURITextField = new JTextField();
                imageURITextField.setEditable(true);
                imageURITextField.setForeground(Color.green.darker());
                imageURITextField.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextField imageURITextField = (JTextField)e.getComponent();
                            
                            if (keyChar == '\n') {
                                // not a good way to get rid of newline,
                                // but I can't figure out how to stop
                                // this from getting entered in text area
                                int cp = imageURITextField.getCaretPosition();
                                Document doc = imageURITextField.getDocument();
                                try {
                                    doc.remove(imageURITextField.getText().length()-1,1);
                                } catch (BadLocationException ble) {}
                                stopCellEditing();
                            }
                        }
                    });
                imageURITextField.addMouseListener(new MouseAdapter() {
                        public void mouseExited(MouseEvent e) {
                            stopCellEditing();
                        }
                    });
            }
            if (serialNumberTextField == null) {
                serialNumberTextField = new JTextField();
                serialNumberTextField.setEditable(true);
                serialNumberTextField.setForeground(Color.green.darker());
                serialNumberTextField.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextField serialNumberTextField = (JTextField)e.getComponent();
                            
                            if (keyChar == '\n') {
                                // not a good way to get rid of newline,
                                // but I can't figure out how to stop
                                // this from getting entered in text area
                                int cp = serialNumberTextField.getCaretPosition();
                                Document doc = serialNumberTextField.getDocument();
                                try {
                                    doc.remove(serialNumberTextField.getText().length()-1,1);
                                } catch (BadLocationException ble) {}
                                stopCellEditing();
                            }
                        }
                    });
                serialNumberTextField.addMouseListener(new MouseAdapter() {
                        public void mouseExited(MouseEvent e) {
                            stopCellEditing();
                        }
                    });
            }
            if (gnCb == null) {
                gnCb = new JComboBox();
                gnCb.setEditable(false);
                Iterator gnIt = _model.getGroupNames().iterator();
                while (gnIt.hasNext()) {
                    GroupName gn = (GroupName)gnIt.next();
                    gnCb.addItem(gn);
                }
                gnCb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                stopCellEditing();
                            }
                        }
                    });      
            }
            if (verifiedCb == null) {
                verifiedCb = new JComboBox();
                verifiedCb.setEditable(false);
                verifiedCb.addItem("true");
                verifiedCb.addItem("false");
                verifiedCb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                stopCellEditing();
                            }
                        }
                    });      
            }
            
            // put the data in the text field
            if (value != null) {
                if (row == 1) {
                    rowEdited = 1;
                    compNameTextField.setText(value.toString());
                    return compNameTextField;
                } else if (row == 4) {
                    rowEdited = 4;
                    serialNumberTextField.setText(value.toString());
                    return serialNumberTextField;
                } else if (row == 5) {
                    rowEdited = 5;
                    GroupName gn = (GroupName)value;
                    gnCb.setSelectedItem(gn);
                    return gnCb;

                } else if (row == 6) {
                    rowEdited = 6;
                    String verif = (String)value;
                    if (verif.equals("true"))
                        verifiedCb.setSelectedIndex(0);
                    else
                        verifiedCb.setSelectedIndex(1);
                    return verifiedCb;
                } else if (row == 9) {
                    rowEdited = 9;
                    imageURITextField.setText(value.toString());
                    return imageURITextField;
                }
            }
            
            return null;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            if (rowEdited == 1) {
                return compNameTextField.getText();
            } else if (rowEdited == 4) {
                return serialNumberTextField.getText();
            } else if (rowEdited == 5) {
                return gnCb.getSelectedItem();
            } else if (rowEdited == 6) {
                return verifiedCb.getSelectedItem();
            } else if (rowEdited == 9) {
                return imageURITextField.getText();
            } else {
                return null;
            }
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent)e;
                if (me.getClickCount() == 1)
                    return true;
                else
                    return false;
            }
            return false;
        }

    }

    public class ComponentPopupMenu extends JPopupMenu {

        private TreePath selectedTreePath;

        private JMenuItem sendToComponentAItem;
        private JMenuItem sendToComponentBItem;

        private JMenuItem sendToConfigureItem;
        private JMenuItem copyToConfigureItem;
        private JMenuItem addChildItem;
        private JMenuItem addChildFromConfigureItem;
        private JMenuItem insertParentItem;
        private JMenuItem insertParentFromConfigureItem;
        private JMenuItem changeParentOfConfigureItem;
        private JMenuItem deleteItem;
        private JMenuItem addToFindItem;
        private JMenuItem replaceItem;
        private int hierarchy;
        private ComponentTreeFindController findController;

        public ComponentPopupMenu(int relType, ComponentTreeFindController findCont) {
            super();
            this.hierarchy = relType;
            this.findController = findCont;

            // send to configure
            sendToConfigureItem = new JMenuItem("Send to Configure");
            sendToConfigureItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        latchComponentToConfigure(c, true);
                    }
                });
            this.add(sendToConfigureItem);
            this.addSeparator();

            addChildItem = new JMenuItem("Add Child...");
            addChildItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        // show wizard, but restrict list of possible component types
                        // based on parent component interfaces presented
                        List componentTypes = _model.getComponentTypes();
                        List filteredTypes = ComponentTypeService
                            .filterComponentTypeListByPeerInterface(componentTypes,
                                                                    c.getComponentType(), 
                                                                    false, true,
                                                                    hierarchy);

                        if (filteredTypes.size() == 0) {
                            String hierarchyName = "";
                            if (hierarchy==ComponentRelationshipType.HOUSING)
                                hierarchyName = "housing";
                            else if (hierarchy==ComponentRelationshipType.CONTROL)
                                hierarchyName = "control";
                            else
                                hierarchyName = "power";
                            Application.displayWarning("No Interfaces Presented","The component you selected has no "+hierarchyName+" interfaces presented.");

                        } else {
                            NewComponentWizardModel wizardData = 
                                NewComponentWizard.showWizard(_appFrame, filteredTypes, c, hierarchy);
                            
                            // did the user "finish" or "cancel" ?
                            if (wizardData.getCancelled() == false) {
                                housingTree.clearSelection();
                                controlTree.clearSelection();
                                powerTree.clearSelection();
                                Component newC = wizardData.getComponent();

                                // add the new child
                                addNewChild(c, newC, hierarchy);

                                // auto place in control and power if indicated
                                if (wizardData.getCanAddToControl())
                                    addNewChild(c, newC, ComponentRelationshipType.CONTROL);
                                if (wizardData.getCanAddToPower())
                                    addNewChild(c, newC, ComponentRelationshipType.POWER);

                                latchComponentToConfigure(wizardData.getComponent(), true);
                                
                            } else {
                                latchComponentToConfigure(null, true);
                            }
                        }
                    }
                });
            if (hierarchy == ComponentRelationshipType.HOUSING)
                this.add(addChildItem);

            addChildFromConfigureItem = new JMenuItem("Add Child from Configure");
            addChildFromConfigureItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        housingTree.clearSelection();
                        controlTree.clearSelection();
                        powerTree.clearSelection();
                        addNewChild(c, _model.getComponentToConfigure(), hierarchy);
                        // force update of tree, even though we're already latched
                        latchComponentToConfigure(_model.getComponentToConfigure(), false);
                    }
                });
            this.add(addChildFromConfigureItem);

            insertParentItem = new JMenuItem("Insert Parent...");
            insertParentItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        // show wizard, but restrict list of possible component types
                        // based on types of immediate parent and child
                        List componentTypes = _model.getComponentTypes();
                        // check that we can become valid new parent
                        List filteredTypes = ComponentTypeService
                            .filterComponentTypeListByPeerInterface(componentTypes,
                                                                    c.getComponentType(), 
                                                                    true, false,
                                                                    hierarchy);

                        // check that we can replace old child in-place
                        ComponentRelationship cr = c.getParentRelationship(hierarchy);
                        if (cr != null) {
                            Component parent = cr.getParentComponent();
                            filteredTypes = ComponentTypeService
                                .filterComponentTypeListByPeerInterface(filteredTypes,
                                                                        parent.getComponentType(),
                                                                        false, true,
                                                                        hierarchy);
                        }

                        if (filteredTypes.size() == 0) {
                            String hierarchyName = "";
                            if (hierarchy==ComponentRelationshipType.HOUSING)
                                hierarchyName = "housing";
                            else if (hierarchy==ComponentRelationshipType.CONTROL)
                                hierarchyName = "control";
                            else
                                hierarchyName = "power";
                            Application.displayWarning("No Interfaces Presented","The component you selected has no needed "+hierarchyName+" interfaces.");

                        } else {
                            NewComponentWizardModel wizardData = 
                                NewComponentWizard.showWizard(_appFrame, filteredTypes, null, 0);
                            
                            // did the user "finish" or "cancel" ?
                            if (wizardData.getCancelled() == false) {
                                housingTree.clearSelection();
                                controlTree.clearSelection();
                                powerTree.clearSelection();

                                // insert the new component as a parent of selected component
                                insertNewParent(c, wizardData.getComponent(), hierarchy);
                                latchComponentToConfigure(wizardData.getComponent(), true);
                                
                            } else {
                                latchComponentToConfigure(null, false);
                            }
                        }
                    }
                });
            if (hierarchy == ComponentRelationshipType.HOUSING)
                this.add(insertParentItem);

            insertParentFromConfigureItem = new JMenuItem("Insert Parent from Configure");
            insertParentFromConfigureItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        housingTree.clearSelection();
                        controlTree.clearSelection();
                        powerTree.clearSelection();
                        insertNewParent(c, _model.getComponentToConfigure(), hierarchy);
                        // force update of tree, even though we're already latched
                        latchComponentToConfigure(_model.getComponentToConfigure(), false);
                    }
                });
            this.add(insertParentFromConfigureItem);

            changeParentOfConfigureItem = new JMenuItem("Change Parent of Component in Configure");
            changeParentOfConfigureItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component newParent = (Component)selectedTreePath.getLastPathComponent();
                        housingTree.clearSelection();
                        controlTree.clearSelection();
                        powerTree.clearSelection();
                        // boolean canAddToControl = 
                        //ComponentService.isValidForAutomaticPlacement(newC, ComponentRelationshipType.CONTROL);
                        changeParent(newParent, _model.getComponentToConfigure(), hierarchy);
                        // force update of tree, even though we're already latched
                        latchComponentToConfigure(_model.getComponentToConfigure(), false);
                    }
                });
            this.add(changeParentOfConfigureItem);
            this.addSeparator();

            // copy to configure
            copyToConfigureItem = new JMenuItem("Copy to Configure");
            copyToConfigureItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent(); 
                        try {
                            Component copy = ComponentService.copyComponent(c);
                            latchComponentToConfigure(copy, true);
                        } catch (IRMISException ie) {
                            ie.printStackTrace();
                        }
                    }
                });
            this.add(copyToConfigureItem);

            this.addSeparator();

            // replace component
            replaceItem = new JMenuItem("Replace Component");
            replaceItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent(); 
                        // prepare list of potential replacement component types
                        document.actionPrepareReplacementList(c);
                        // dialog box for user displayed in updateView() method
                    }
                });
            this.add(replaceItem);

            this.addSeparator();
            deleteItem = new JMenuItem("Delete...");
            deleteItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        String resp = 
                            DeleteDialog.showDialog(_appFrame,null,"Delete Component Dialog");
                        if (resp != null) {
                            if (resp.equals("all")) {
                                deleteComponent(c, ComponentRelationshipType.HOUSING);
                                deleteComponent(c, ComponentRelationshipType.CONTROL);
                                deleteComponent(c, ComponentRelationshipType.POWER);
                            } else {
                                deleteComponent(c, hierarchy);
                            }
                        }
                    }
                });
            this.add(deleteItem);
            this.addSeparator();
            addToFindItem = new JMenuItem("Add to Find List");
            addToFindItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Component c = (Component)selectedTreePath.getLastPathComponent();
                        findController.addComponentToFind(c);
                    }
                });            
            this.add(addToFindItem);

        }

        public void show(JTree tree, int x, int y, boolean enabled) {

            Component leadSelection = 
                (Component)selectedTreePath.getLastPathComponent();

            if (!enabled) {  // not logged in with sufficient permissions
                sendToConfigureItem.setEnabled(false);
                copyToConfigureItem.setEnabled(false);
                if (hierarchy == ComponentRelationshipType.HOUSING)
                    addChildItem.setEnabled(false);
                addChildFromConfigureItem.setEnabled(false);
                if (hierarchy == ComponentRelationshipType.HOUSING)
                    insertParentItem.setEnabled(false);
                insertParentFromConfigureItem.setEnabled(false);
                changeParentOfConfigureItem.setEnabled(false);
                replaceItem.setEnabled(false);
                deleteItem.setEnabled(false);

            } else if (_model.getComponentToConfigure() == null) {
                sendToConfigureItem.setEnabled(true);
                copyToConfigureItem.setEnabled(true);
                if (hierarchy == ComponentRelationshipType.HOUSING)
                    addChildItem.setEnabled(true);
                addChildFromConfigureItem.setEnabled(false);
                if (hierarchy == ComponentRelationshipType.HOUSING)                
                    insertParentItem.setEnabled(true); 
                insertParentFromConfigureItem.setEnabled(false);
                changeParentOfConfigureItem.setEnabled(false);
                replaceItem.setEnabled(false);
                deleteItem.setEnabled(false);

            } else {
                // only enable certain items if component not yet in hierarchy
                boolean componentInHierarchy = false;
                Component c = _model.getComponentToConfigure();
                if (c.getParentRelationship(hierarchy) != null)
                    componentInHierarchy = true;                

                if (componentInHierarchy) {
                    sendToConfigureItem.setEnabled(false);
                    copyToConfigureItem.setEnabled(false);
                    if (hierarchy == ComponentRelationshipType.HOUSING)                    
                        addChildItem.setEnabled(false);
                    addChildFromConfigureItem.setEnabled(false);
                    if (hierarchy == ComponentRelationshipType.HOUSING)
                        insertParentItem.setEnabled(false);
                    insertParentFromConfigureItem.setEnabled(false);
                    if (!leadSelection.equals(c)) {
                        changeParentOfConfigureItem.setEnabled(true);
                        replaceItem.setEnabled(false);
                        deleteItem.setEnabled(false);
                    } else {
                        changeParentOfConfigureItem.setEnabled(false);
                        replaceItem.setEnabled(true);
                        deleteItem.setEnabled(true);
                    }

                } else {
                    sendToConfigureItem.setEnabled(false);
                    copyToConfigureItem.setEnabled(false);
                    if (hierarchy == ComponentRelationshipType.HOUSING)
                        addChildItem.setEnabled(false);
                    addChildFromConfigureItem.setEnabled(true);
                    if (hierarchy == ComponentRelationshipType.HOUSING)
                        insertParentItem.setEnabled(false);
                    insertParentFromConfigureItem.setEnabled(true); 
                    changeParentOfConfigureItem.setEnabled(false);
                    replaceItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                }
            }
            super.show(tree, x, y);
        }

        public void setSelectedTreePath(TreePath path) {
            selectedTreePath = path;
        }

        public int getHierarchy() {
            return hierarchy;
        }

    }

    public class PopupTrigger extends MouseAdapter {
        private JTree tree;
        private JTree otherTree1;
        private JTree otherTree2;
        private ComponentPopupMenu popupMenu;

        public PopupTrigger(JTree tree, JTree otherTree1, 
                            JTree otherTree2, ComponentPopupMenu popupMenu) {
            super();
            this.tree = tree;
            this.otherTree1 = otherTree1;
            this.otherTree2 = otherTree2;
            this.popupMenu = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                TreePath selectedTreePath = tree.getPathForLocation(x,y);
                if (selectedTreePath != null) {
                    Component leadSelection = 
                        (Component)selectedTreePath.getLastPathComponent();

                    JTextField logicalDescriptionTextField = null;
                    JButton upButton = null;
                    JButton downButton = null;
                    JCheckBox verifiedCheckBox = null;
                    boolean enabled = true;
                    List filteredTypes = 
                        _model.getFilteredComponentTypes(popupMenu.getHierarchy());

                    switch (popupMenu.getHierarchy()) {
                    case ComponentRelationshipType.CONTROL: {
                        logicalDescriptionTextField = controlTextField;
                        upButton = controlUpButton;
                        downButton = controlDownButton;
                        verifiedCheckBox = controlVerifiedCheckBox;
                        break;
                    }
                    case ComponentRelationshipType.HOUSING: {
                        logicalDescriptionTextField = housingTextField;
                        upButton = housingUpButton;
                        downButton = housingDownButton;
                        verifiedCheckBox = housingVerifiedCheckBox;
                        break;
                    }
                    case ComponentRelationshipType.POWER: {
                        logicalDescriptionTextField = powerTextField;
                        upButton = powerUpButton;
                        downButton = powerDownButton;
                        verifiedCheckBox = powerVerifiedCheckBox;
                        break;
                    }
                    }
                    
                    if (_model.getComponentToConfigure() != null &&
                        filteredTypes != null) {
                        ComponentType ct = leadSelection.getComponentType();
                        if (filteredTypes.contains(ct) ||
                            leadSelection.equals(_model.getComponentToConfigure()))
                            if (_model.deleteHasOccurred())
                                enabled  = false;
                            else
                                enabled = true;
                        else
                            enabled = false;
                    }
                    
                    if (enabled) {
                        popupMenu.setSelectedTreePath(selectedTreePath);
                        // do selection work, updating other 2 trees to match
                        setHierarchySelection(leadSelection, false, popupMenu.getHierarchy(), 
                                              logicalDescriptionTextField, upButton, downButton,
                                              verifiedCheckBox, tree, otherTree1, otherTree2);
                        // show menu
                        popupMenu.show(tree, x, y, LoginUtil.isPermitted(editPrincipal));
                    }
                }
            }
        }
        
    }

    public class LogicalDescriptionKeyAdapter extends KeyAdapter {

        private boolean textFieldModified = false;
        private int hierarchy;
        private JTextField textField;

        public LogicalDescriptionKeyAdapter(int h, JTextField field) {
            this.hierarchy = h;
            this.textField = field;
        }

        public void clearTextFieldModifiedFlag() {
            this.textFieldModified = false;
        }
        
        public void keyTyped(KeyEvent e) {
            char keyChar = e.getKeyChar();
            if (keyChar == KeyEvent.VK_ENTER)
                return;
            Component c = _model.getComponentToConfigure();
            ComponentRelationship cr = 
                c.getParentRelationship(this.hierarchy);
            if (cr != null) {
                // jump through all kinds of hoops to emulate text select behavior
                int selectionStart = this.textField.getSelectionStart();
                int selectionEnd = this.textField.getSelectionEnd();
                String selectedText = this.textField.getSelectedText();
                String textFieldValue = this.textField.getText();
                String newTextFieldValue = null;
                String preSelect = textFieldValue.substring(0,selectionStart);
                String postSelect = textFieldValue.substring(selectionEnd);
                if (keyChar != KeyEvent.VK_BACK_SPACE) {
                    newTextFieldValue = preSelect+keyChar+postSelect;
                } else {
                    if (selectedText == null && preSelect.length() > 0) 
                        newTextFieldValue = preSelect.substring(0,preSelect.length()-1)+postSelect;
                    else
                        newTextFieldValue = preSelect+postSelect;
                }
                cr.setLogicalDescription(newTextFieldValue);
                // mark an undo point, but just once (not for every character)
                if (!this.textFieldModified) {
                    // add this to undo configure stack
                    ConfigureAction action = 
                        new ConfigureAction(ConfigureAction.MODIFY_LOGICAL_DESCRIPTION, 
                                            c, cr, 0, 
                                            this.hierarchy,
                                            this.textField.getText());
                    _model.pushConfigureUndoStack(action);                            
                    this.textFieldModified = true;
                }
                updateJTreeViews(c);
            }
        }
    }
    
    public class RelocateComponentAction extends AbstractAction {
        private JTree tree;
        private int hierarchy;
        private boolean up;

        public RelocateComponentAction(JTree tree, int hierarchy, boolean up) {
            super();
            this.tree = tree;
            this.hierarchy = hierarchy;
            this.up = up;
            if (up) {
                ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Up16.gif"));
                putValue("SmallIcon",icon);
                putValue("ShortDescription","Move (green) component up one line");
            } else {
                ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Down16.gif"));
                putValue("SmallIcon",icon);
                putValue("ShortDescription","Move (green) component down one line");
            }
        }

        public void actionPerformed(ActionEvent e) {
            Component ctc = _model.getComponentToConfigure();
            Component sc = _model.getSelectedComponent();
            if (ctc != null && sc != null && ctc.equals(sc) &&
                LoginUtil.isPermitted(editPrincipal)) {

                document.actionRelocateComponent(sc, hierarchy, up);

                ComponentRelationship cr = 
                    sc.getParentRelationship(hierarchy);
                if (cr != null) {
                    // update trees to reflect relocation
                    ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
                    TreePath parentPath = tModel.getTreePath(cr.getParentComponent());

                    Component parent = cr.getParentComponent();
                    List crList = parent.getChildRelationships(hierarchy);
                    int crListSize = crList.size();
                    int[] indices = new int[crListSize];
                    for (int i=0 ; i < crListSize ; i++)
                        indices[i] = i;
                    TreeModelEvent te = 
                        new TreeModelEvent(this, parentPath, indices, null);
                    tModel.fireTreeNodesChanged(te);
                    
                    // make sure ctc is still selected and visible
                    TreePath cPath = tModel.getTreePath(ctc);
                    tree.setSelectionPath(cPath);
                    tree.scrollPathToVisible(cPath);

                }
            }
        }
    }
}
