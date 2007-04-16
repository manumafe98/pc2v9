package edu.csus.ecs.pc2.ui.team;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Represents an arbitrary contest GUI.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TeamView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IModel model = null;

    private IController teamController = null;

    private String lastOpenedFile = null;

    /**
     * 
     */
    private static final long serialVersionUID = 8225187691479543638L;

    private JPanel submitRunPane = null;

    private JPanel mainViewPane = null;

    private JTabbedPane viewTabbedPane = null;

    private JButton submitRunButton = null;

    private JLabel problemLabel = null;

    private JLabel jLabel = null;

    private JComboBox problemComboBox = null;

    private JComboBox languageComboBox = null;

    private JButton pickFileButton = null;

    private JList runSubmissionList = null;

    private JScrollPane runListScrollPane = null;

    private JPanel runListPane = null;

    private DefaultListModel runListModel = new DefaultListModel();

    private JPanel optionsPane = null;

    private JCheckBox showLogWindowCheckBox = null;

    private JPanel filenamePane = null;

    private JLabel fileNameLabel = null;
    
    private LogWindow logWindow = null;

    /**
     * Nevermind this constructor, needed for VE and other reasons.
     * 
     */
    public TeamView() {
        super();
        initialize();
        updateListBox(getPluginTitle() + " Build " + new VersionInfo().getBuildNumber());
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(481, 351));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getMainViewPane());
        this.setTitle("The TeamView");
        FrameUtilities.waitCursor(this);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);
        setTitle("PC^2 Team - Not Logged In ");

        if (logWindow == null) {
            logWindow = new LogWindow();
        }
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void populateGUI() {

        getProblemComboBox().removeAllItems();
        Problem problemN = new Problem("None Selected");
        getProblemComboBox().addItem(problemN);

        for (Problem problem : model.getProblems()) {
            getProblemComboBox().addItem(problem);
        }

        getLanguageComboBox().removeAllItems();
        Language languageN = new Language("None Selected");
        getLanguageComboBox().addItem(languageN);

        for (Language language : model.getLanguages()) {
            getLanguageComboBox().addItem(language);
        }

        setButtonsActive(model.getContestTime().isContestRunning());
    }

    private void updateListBox(String string) {
        runListModel.insertElementAt(string, 0);
        StaticLog.unclassified(string);
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == model.getSiteNumber();
    }

    /**
     * Enable or disable submission buttons.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getSubmitRunButton().setEnabled(turnButtonsOn);
                getPickFileButton().setEnabled(turnButtonsOn);
                if (turnButtonsOn) {
                    setTitle("PC^2 Team " + model.getTitle() + " [STARTED] Build " + new VersionInfo().getBuildNumber());
                } else {
                    setTitle("PC^2 Team " + model.getTitle() + " [STOPPED] Build " + new VersionInfo().getBuildNumber());
                }
            }
        });
        FrameUtilities.regularCursor(this);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " ADDED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " REMOVED ");
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " CHANGED ");
        }

        public void contestStarted(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " STARTED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " STOPPED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getProblemComboBox().addItem(event.getProblem());
                }
            });
        }

        public void problemChanged(ProblemEvent event) {
            updateListBox("Problem CHANGED  " + event.getProblem());
        }

        public void problemRemoved(ProblemEvent event) {
            updateListBox("Problem REMOVED  " + event.getProblem());
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(final LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getLanguageComboBox().addItem(event.getLanguage());
                }
            });
        }

        public void languageChanged(LanguageEvent event) {
            updateListBox("Language CHANGED  " + event.getLanguage());
        }

        public void languageRemoved(LanguageEvent event) {
            updateListBox("Language REMOVED  " + event.getLanguage());
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // updateListBox(event.getRun() + " ADDED ");
            updateListBox("Added run " + event.getRun());
        }

        public void runChanged(RunEvent event) {
            updateListBox(event.getRun() + " CHANGED ");
        }

        public void runRemoved(RunEvent event) {
            updateListBox(event.getRun() + " REMOVED ");
        }
    }

    /**
     * This method initializes submitRunPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSubmitRunPane() {
        if (submitRunPane == null) {
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(28, 62, 80, 21));
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel.setText("Language");
            problemLabel = new JLabel();
            problemLabel.setBounds(new java.awt.Rectangle(28, 19, 80, 21));
            problemLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            problemLabel.setText("Problem");
            submitRunPane = new JPanel();
            submitRunPane.setLayout(null);
            submitRunPane.add(getSubmitRunButton(), null);
            submitRunPane.add(problemLabel, null);
            submitRunPane.add(jLabel, null);
            submitRunPane.add(getProblemComboBox(), null);
            submitRunPane.add(getLanguageComboBox(), null);
            submitRunPane.add(getRunListPane(), null);
            submitRunPane.add(getFilenamePane(), null);
        }
        return submitRunPane;
    }

    protected void autoPopulate() {
        // TODO: auto populate

        String matchProblemString = "umit";
        for (int i = 0; i < problemComboBox.getItemCount(); i++) {
            Problem problem = (Problem) problemComboBox.getItemAt(i);
            int idx = problem.toString().indexOf(matchProblemString);
            if (idx > -1) {
                problemComboBox.setSelectedIndex(i);
            }
        }

        String matchLanguageString = "Java";
        for (int i = 0; i < languageComboBox.getItemCount(); i++) {
            Language language = (Language) languageComboBox.getItemAt(i);
            int idx = language.toString().indexOf(matchLanguageString);
            if (idx > -1) {
                languageComboBox.setSelectedIndex(i);
            }
        }

        // problemComboBox.
        // getProblemCombo().setSelectedIndex(getProblemCombo().getItemCount() - 1);
        // getLanguageCombo().setSelectedIndex(getLanguageCombo().getItemCount() - 1);

        try {
            String filename = "samps/Sumit.java";
            File file = new File(filename);
            if (file.exists()) {
                fileNameLabel.setText(filename);
            }
        } catch (Exception e) {
            StaticLog.log("Exception logged ", e);
        }

        try {
            String filename = "/pc2/samps/sumit.java";
            File file = new File(filename);
            if (file.exists()) {
                fileNameLabel.setText(filename);
            }
        } catch (Exception e) {
            StaticLog.log("Exception logged ", e);
        }
    }

    /**
     * This method initializes mainViewPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainViewPane() {
        if (mainViewPane == null) {
            mainViewPane = new JPanel();
            mainViewPane.setLayout(new BorderLayout());
            mainViewPane.add(getViewTabbedPane(), java.awt.BorderLayout.CENTER);
        }
        return mainViewPane;
    }

    /**
     * This method initializes viewTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getViewTabbedPane() {
        if (viewTabbedPane == null) {
            viewTabbedPane = new JTabbedPane();
            viewTabbedPane.addTab("Submit Run", null, getSubmitRunPane(), null);
            viewTabbedPane.addTab("Option", null, getOptionsPane(), null);
        }
        return viewTabbedPane;
    }

    /**
     * This method initializes submitRunButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSubmitRunButton() {
        if (submitRunButton == null) {
            submitRunButton = new JButton();
            submitRunButton.setBounds(new java.awt.Rectangle(376, 105, 74, 26));
            submitRunButton.setEnabled(false);
            submitRunButton.setText("Submit");
            submitRunButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    submitRun();
                }

            });
        }
        return submitRunButton;
    }

    private boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.isFile();
    }

    protected void submitRun() {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());
        Language language = ((Language) getLanguageComboBox().getSelectedItem());

        if (getProblemComboBox().getSelectedIndex() < 1) {
            JOptionPane.showMessageDialog(this, "Please select problem");
            return;
        }

        if (getLanguageComboBox().getSelectedIndex() < 1) {
            JOptionPane.showMessageDialog(this, "Please select language");
            return;
        }

        String filename = fileNameLabel.getText();

        if (!fileExists(filename)) {
            File curdir = new File(".");

            String message = filename + " not found";
            try {
                message = message + " in " + curdir.getCanonicalPath();
            } catch (Exception e) {
                // ignore exception
                message = message + ""; // What a waste of time and code.
            }
            JOptionPane.showMessageDialog(this, message);
            return;
        }

        try {
            teamController.submitRun(problem, language, filename);
        } catch (Exception e) {
            // TODO nead to make this cleaner
            JOptionPane.showMessageDialog(this, "Exception " + e.getMessage());
        }
    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox();
            problemComboBox.setBounds(new java.awt.Rectangle(126, 15, 221, 28));

            problemComboBox.addItem(new Problem("Select Problem"));
        }
        return problemComboBox;
    }

    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new JComboBox();
            languageComboBox.setBounds(new java.awt.Rectangle(127, 58, 221, 28));

            languageComboBox.addItem(new Language("Select Language"));
        }
        return languageComboBox;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getPickFileButton() {
        if (pickFileButton == null) {
            pickFileButton = new JButton();
            pickFileButton.setEnabled(false);
            pickFileButton.setBounds(new java.awt.Rectangle(250, 12, 74, 26));
            pickFileButton.setText("Pick");
            pickFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectMainFile();
                }
            });
        }
        return pickFileButton;
    }

    /**
     * This method initializes runSubmissionList
     * 
     * @return javax.swing.JList
     */
    private JList getRunSubmissionList() {
        if (runSubmissionList == null) {
            runSubmissionList = new JList(runListModel);
        }
        return runSubmissionList;
    }

    /**
     * This method initializes runListScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getRunListScrollPane() {
        if (runListScrollPane == null) {
            runListScrollPane = new JScrollPane();
            runListScrollPane.setViewportView(getRunSubmissionList());
        }
        return runListScrollPane;
    }

    /**
     * This method initializes runListPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRunListPane() {
        if (runListPane == null) {
            runListPane = new JPanel();
            runListPane.setLayout(new BorderLayout());
            runListPane.setBounds(new java.awt.Rectangle(32, 147, 418, 130));
            runListPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Runs", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            runListPane.add(getRunListScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return runListPane;
    }

    private void selectMainFile() {
        JFileChooser chooser = new JFileChooser(lastOpenedFile);
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastOpenedFile = chooser.getCurrentDirectory().toString();
                fileNameLabel.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                fileNameLabel.setToolTipText(chooser.getSelectedFile().getCanonicalFile().toString());

            }
        } catch (Exception e) {
            System.err.println("Error getting selected file, try again.");
            e.printStackTrace(System.err);
            // getLog().log(Log.CONFIG, "Error getting selected file, try again.", e);
        }
        chooser = null;

    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.teamController = inController;

        model.addRunListener(new RunListenerImplementation());
        model.addContestTimeListener(new ContestTimeListenerImplementation());
        model.addLanguageListener(new LanguageListenerImplementation());
        model.addProblemListener(new ProblemListenerImplementation());

        // TODO add listeners for accounts, login and site.

        // model.addAccountListener(new AccountListenerImplementation());
        // model.addLoginListener(new LoginListenerImplementation());
        // model.addSiteListener(new SiteListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });

        setVisible(true);
    }

    public String getPluginTitle() {
        return "Team Main GUI";
    }

    /**
     * This method initializes optionsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOptionsPane() {
        if (optionsPane == null) {
            optionsPane = new JPanel();
            optionsPane.add(getShowLogWindowCheckBox(), null);
        }
        return optionsPane;
    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

    /**
     * This method initializes showLogWindowCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowLogWindowCheckBox() {
        if (showLogWindowCheckBox == null) {
            showLogWindowCheckBox = new JCheckBox();
            showLogWindowCheckBox.setText("Show Log");
            showLogWindowCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showLog(showLogWindowCheckBox.isSelected());
                }
            });
        }
        return showLogWindowCheckBox;
    }

    /**
     * This method initializes filenamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilenamePane() {
        if (filenamePane == null) {
            fileNameLabel = new JLabel();
            fileNameLabel.setBounds(new java.awt.Rectangle(16, 13, 223, 25));
            fileNameLabel.setText("");
            fileNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1 && e.isShiftDown()) {
                        autoPopulate();
                    }
                }
            });
            filenamePane = new JPanel();
            filenamePane.setLayout(null);
            filenamePane.setBounds(new java.awt.Rectangle(30, 95, 335, 44));
            filenamePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Main File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            filenamePane.add(getPickFileButton(), null);
            filenamePane.add(fileNameLabel, null);
        }
        return filenamePane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
