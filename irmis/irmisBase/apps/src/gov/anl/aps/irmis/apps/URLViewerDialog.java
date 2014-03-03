/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class URLViewerDialog extends JDialog implements ActionListener {

    private static URLViewerDialog dialog;
    JEditorPane urlTextPane;
    
    /**
     * Puts up a dialog box for displaying the target of a given URL.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that login dialog should show up near (can be null)
     * @param url the URL string to display as a test
     */
    public static void showDialog(Component frameComp,
                                    Component locationComp,
                                    String urlString) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        
        dialog = new URLViewerDialog(frame, locationComp, urlString);
        dialog.setVisible(true);
        return;
    }
    
    private URLViewerDialog(Frame frame,
                          Component locationComp, String urlString) {
        super(frame, "URL Viewer Dialog", true);

        //Create and initialize the button.
        final JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);

        //main part of the dialog

        // type name text field
        urlTextPane = new JEditorPane();
        urlTextPane.setEditable(false);
        if (urlString != null) {
            try {
                java.net.URL url = new java.net.URL(urlString);
                urlTextPane.setPage(url);
                urlTextPane.setMinimumSize(new Dimension(300,200));
                urlTextPane.setPreferredSize(new Dimension(300,200));
            } catch (IOException ioe) {
                urlTextPane.setText("Unable to find specified URL");
                urlTextPane.setMinimumSize(new Dimension(300,100));
                urlTextPane.setPreferredSize(new Dimension(300,100));
            }
        }

        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
    	JScrollPane urlScroller = new JScrollPane(urlTextPane, vsbp, hsbp);
        

        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.PAGE_AXIS));
        JPanel urlLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel(urlString);
        urlLabelPanel.add(urlLabel);
        urlPanel.add(urlLabelPanel);
        urlPanel.add(urlScroller);
        urlPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        //buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(okButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(urlPanel, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locationComp);
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            // do nothing for now
        } 
        URLViewerDialog.dialog.setVisible(false);
    }
}
