package gov.anl.aps.irmis.apps;

import java.awt.Dimension;
import javax.swing.*;


public class ProgressDialog {
    static JDialog dialog = null;
    static JProgressBar progressBar = null;
    public static JProgressBar show(JFrame frame, int min, int max) {
        dialog = new JDialog(frame, "Loading data, please wait...", false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.PAGE_AXIS));
        progressBar = new JProgressBar();
        if (min == max) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setMinimum(min);
            progressBar.setMaximum(max);
        }
        contentPanel.add(progressBar);
        
        dialog.setContentPane(contentPanel);
        dialog.setSize(new Dimension(300,50));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        return progressBar;
    }

    public static void setValue(int val) {
        progressBar.setValue(val);
    }
    
    public static void destroy() {
        dialog.setVisible(false);
        dialog = null;
        progressBar = null;
    }
}
